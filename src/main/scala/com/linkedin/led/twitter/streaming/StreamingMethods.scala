package com.linkedin.led.twitter.streaming

import scala.collection.mutable.ArrayBuffer
import org.apache.commons.httpclient.util.URIUtil
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.HttpMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.GetMethod

/**
 * StreamingMethods
 *
 * This trait implements the public streaming API methods, filter, sample, etc.
 */
trait StreamingMethods {

  /**
   * Sample
   *
   * Returns a random sample of all public statuses.
   *
   * @param count Indicates the number of previous statuses to consider for delivery before transitioning to live stream delivery.
   * @param delimited Indicates that statuses should be delimited. Statuses are represented by a length, in bytes, a newline, and the status text that is exactly length bytes. ex. length
   */
  def sample(count: Int, delimited: String) = {
    val baseUrl = Config.readString("twitterStreamUrl")

    // Add the params
    val params = new ArrayBuffer[String]
    if(count > 0) {
      params += "count="+ count
    }
    if(delimited != "") {
      params += "delimited="+ delimited
    }

    val getMethod = buildGet(baseUrl, params)
    stream(getMethod)
  }

  def sample(count: Int): Unit = sample(0, "")
  def sample: Unit = sample(0, "")

  /**
   * Filter 
   *
   * Returns public statuses that match one or more filter predicates.
   *
   * @param count Indicates the number of previous statuses to consider for delivery before transitioning to live stream delivery.
   * @param delimited Indicates that statuses should be delimited. Statuses are represented by a length, in bytes, a newline, and the status text that is exactly length bytes.
   * @param follow Specifies the list of Twitter user id's to follow
   * @param track Specifies the list of keywords to keep track of
   */
  def filter(count: Int, delimited: String, follow: Set[Int], track: Set[String]) = {
    val baseUrl = Config.readString("twitterFilterUrl")

    // Add the params
    val params = new ArrayBuffer[NameValuePair]

    if(!follow.isEmpty) {
      params += new NameValuePair("follow", follow.mkString(","))
    }

    if(!track.isEmpty) {
      params += new NameValuePair("track", track.mkString(","))
    }

    val postMethod = buildPost(baseUrl, params)
    stream(postMethod)
  }

  def filter(count: Int, delimited: String): Unit = filter(count, delimited, Set(), Set())
  def filter(count: Int): Unit = filter(count, "", Set(), Set())
  def filter: Unit = filter(0, "", Set(), Set())

  /**
   * Firehose
   *
   * Returns all public statuses. The Firehose is not a generally available resource.
   * Few applications require this level of access. i.e. you are probably not whitelisted for this.
   *
   * @param count Indicates the number of previous statuses to consider for delivery before transitioning to live stream delivery.
   * @param delimited Indicates that statuses should be delimited. Statuses are represented by a length, in bytes, a newline, and the
   */
   def firehose(count: Int, delimited: String) = {
     val baseUrl = Config.readString("twitterFirehoseUrl")

     // Add the params
     val params = new ArrayBuffer[String]
     if(count > 0) {
       params += "count="+ count
     }
     if(delimited != "") {
       params += "delimited="+ delimited
     }

     val getMethod = buildGet(baseUrl, params)
     stream(getMethod)
   }

  def firehose(count: Int): Unit = firehose(count, "")
  def firehose: Unit = firehose(0, "")

  /**
   * Links
   *
   * Returns all statuses containing http: and https:. The links stream is not a generally available resource.
   * Few applications require this level of access.
   *
   * @param delimited Indicates that statuses should be delimited. Statuses are represented by a length, in bytes, a newline, and the
   */
  def links(delimited: String) = {
    val baseUrl = Config.readString("twitterLinksUrl")

     // Add the params
     val params = new ArrayBuffer[String]
     if(delimited != "") {
       params += "delimited="+ delimited
     }

     val getMethod = buildGet(baseUrl, params)
     stream(getMethod)
  }

  def links(): Unit = links("")

  /**
   * Retweet
   *
   * Returns all retweets. The retweet stream is not a generally available resource.
   * Few applications require this level of access.
   *
   * @param delimited Indicates that statuses should be delimited. Statuses are represented by a length, in bytes, a newline, and the
   */
   def retweet(delimited: String) = {
     val baseUrl = Config.readString("twitterRetweetUrl")

      // Add the params
      val params = new ArrayBuffer[String]
      if(delimited != "") {
        params += "delimited="+ delimited
      }

      val getMethod = buildGet(baseUrl, params)
      stream(getMethod)
   }

  def retweet(): Unit = retweet("")

  def stream(method: HttpMethod): Unit

  def buildGet(baseUrl: String, params: ArrayBuffer[String]): GetMethod = {
    val getMethod: GetMethod = new GetMethod(baseUrl)
    getMethod.setQueryString(URIUtil.encodeQuery(params.mkString("&")))
    getMethod
  }

  def buildPost(baseUrl: String, params: ArrayBuffer[NameValuePair]): PostMethod = {
    val postMethod = new PostMethod(baseUrl)
    postMethod.setRequestBody(params.toArray)
    postMethod
  }
}
