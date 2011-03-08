package com.streamer.twitter

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpURL
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.HttpMethod

import com.streamer.twitter.oauth._

/**
 * Twitter Streaming OAuth API client
 *
 * @param consumer Consumer key and secret pair
 * @param token Access Token for the app
 * @param streamProcessor handles the processing of the stream
 *
 * @author <a href="mailto:alejandrocrosa@gmail.com">Alejandro Crosa</a>
 */

class OAuthStreamingClient(val consumer: Consumer, val token: Token, val streamProcessor: StreamProcessor) extends Client with StreamingMethods {

  val client = this.getHttpClient

  /**
   * Returns a Basic Auth ready http client
   */
  def getClient(method: HttpMethod): HttpClient = {
    OAuth.sign(method, consumer, token)
    client
  }
}

