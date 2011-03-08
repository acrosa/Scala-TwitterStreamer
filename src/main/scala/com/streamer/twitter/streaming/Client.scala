package com.streamer.twitter

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

import com.streamer.twitter.config._

trait Client {

  val streamProcessor: StreamProcessor
  val client: HttpClient

  /**
    * Back off strategy is:
    * TCP errors start at 250 miliseconds and cap at 16 seconds
    * HTTP errors start at 10 seconds and cap at 240 seconds
    */
   val tcpBackOff  = BackOff(250, 16000)
   val httpBackOff = BackOff(10000, 240000)

   // Method that returns a constructed and initialized client, Basic Auth or OAuth
   def getClient(method: HttpMethod): HttpClient

   /**
    * stream is a recursive method that fetches the stream
    * in the case of errors, backs off.
    *  @param method the http method to use with the necessary parameters already set
    */
   final def stream(method: HttpMethod): Unit = {
     try {
       val client = this.getClient(method)

       // Use compression and a custom user agent
       method.setRequestHeader("Accept-Encoding", "gzip")
       method.setRequestHeader("User-Agent", Config.readString("userAgent"))

       try {
         client.executeMethod(method)

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
       method.releaseConnection
     }

     // This is a recursive method that internally backs off when there's an exception or error processing
     stream(method)
   }

   /**
    * Returns an HttpClient object adding the necessary configuration params.
    */
   def getHttpClient: HttpClient = {
     val client = new HttpClient
     // Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
     // A timeout value of zero is interpreted as an infinite timeout.
     client.getHttpConnectionManager.getParams.setSoTimeout(Config.readInt("socketTimeout"))
     // Default recovery procedure can be replaced with a custom one, let's do that.
     client.getParams.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false))
     client
   }

   /*
    * Resets the error counts for the back off strategy.
    */
   def resetBackOffs() = {
     httpBackOff.reset()
     tcpBackOff.reset()
   }
}
