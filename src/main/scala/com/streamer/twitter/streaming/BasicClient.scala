package com.streamer.twitter

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpURL
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.HttpMethod

/**
 * Twitter Streaming API client
 *
 * @param username the twitter account username
 * @param password the twitter account password
 * @param streamProcessor handles the processing of the stream
 *
 * @author <a href="mailto:alejandrocrosa@gmail.com">Alejandro Crosa</a>
 */

class BasicStreamingClient(val username: String, val password: String, val streamProcessor: StreamProcessor) extends BasicClient with StreamingMethods {

  val client = this.getHttpClient

  /**
   * Returns a Basic Auth ready http client
   */
  def getClient(method: HttpMethod): HttpClient = {
    this.setBasicAuthCredentials(client, method) // Set the Basic Auth username and password params
  }
}

/**
 * Twitter Basic authentication support
 */
trait BasicClient extends Client {

  val username: String
  val password: String

  def setBasicAuthCredentials(client: HttpClient, method: HttpMethod): HttpClient = {
    // Set the username and password credentials for this url scope
    client.getState.setCredentials(this.getAuthScope(method), this.getCredentials)
    client.getParams.setAuthenticationPreemptive(true)
    client
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
}
