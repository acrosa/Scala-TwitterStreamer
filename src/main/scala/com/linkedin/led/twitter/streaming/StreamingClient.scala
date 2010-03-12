package com.linkedin.led.twitter.streaming

import java.io.IOException
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpException
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.httpclient.HttpURL
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.HttpMethod
import org.apache.commons.httpclient.params.HttpMethodParams

/**
 * Twitter Streaming API client
 *
 * @param username the twitter account username
 * @param password the twitter account password
 * @param streamProcessor handles the processing of the stream
 *
 * @author <a href="mailto:acrosa@linkedin.com">Alejandro Crosa</a>
 */
class StreamingClient(val username: String, val password: String, streamProcessor: StreamProcessor) extends StreamingMethods {

  /**
   * Back off strategy is:
   * TCP errors start at 250 miliseconds and cap at 16 seconds
   * HTTP errors start at 10 seconds and cap at 240 seconds
   */
  val tcpBackOff  = BackOff(250, 16000)
  val httpBackOff = BackOff(10000, 240000)

  /**
   * stream is a recursive method that fetches the stream
   * in the case of errors, backs off.
   *  @param method the http method to use with the necessary parameters already set
   */
  final def stream(method: HttpMethod): Unit = {
    try {
      val httpClient: HttpClient = this.getStreamingHttpClient(method)
      
      // Use compression and a custom user agent
      method.setRequestHeader("Accept-Encoding", "gzip")
      method.setRequestHeader("User-Agent", Config.readString("userAgent"))
      
      try {
        httpClient.executeMethod(method)
      
        // Check we received the correct HTTP status on the first attempt to connect
        if (method.getStatusCode() != HttpStatus.SC_OK) {
          throw new HttpException("There was a problem connecting, HTTP code received was: " + 
          method.getStatusCode() +" "+ method.getStatusLine())
        }
      
        // Reset the errors since the request was successful
        this.resetBackOffs()
      
        // Let's delegate the processing to the StreamProcessor object
        // You must override the process method in order to do a custom processing with the stream
        streamProcessor.process(method.getResponseBodyAsStream())
      } catch {
        case e: InterruptedException => return;
        case e: HttpException => httpBackOff.backOff
        case e: IOException   => tcpBackOff.backOff
        case _ =>
      }      
    } finally {
      method.abort
      method.releaseConnection
    }

    // This is a recursive method that internally backs off when there's an exception or error processing
    stream(method)
  }

  /**
   * Returns an HttpClient object adding the necessary configuration params.
   */
  def getStreamingHttpClient(method: HttpMethod): HttpClient = {
    val httpClient = new HttpClient
    // Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
    // A timeout value of zero is interpreted as an infinite timeout.
    httpClient.getHttpConnectionManager.getParams.setSoTimeout(Config.readInt("socketTimeout"))
    // Default recovery procedure can be replaced with a custom one, let's do that.
    httpClient.getParams.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false))
    // Set the username and password credentials for this url scope
    httpClient.getState.setCredentials(this.getAuthScope(method), this.getCredentials)
    httpClient.getParams.setAuthenticationPreemptive(true)
    httpClient
  }

  /*
   * Returns the credentials for the user.
   */
  def getCredentials = new UsernamePasswordCredentials(username +":"+ password)

  /*
   * Returns the AuthScope for the current resource url.
   */
  def getAuthScope(method: HttpMethod): AuthScope = {
    val url = new HttpURL(method.getURI.toString)
    new AuthScope(url.getHost, url.getPort)
  }

  /*
   * Resets the error counts for the back off strategy.
   */
  def resetBackOffs() = {
    httpBackOff.reset()
    tcpBackOff.reset()
  }
}
