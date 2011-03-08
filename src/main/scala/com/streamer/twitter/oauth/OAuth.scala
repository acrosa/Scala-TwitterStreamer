package com.streamer.twitter.oauth

import collection.Map
import collection.immutable.{ TreeMap, Map => IMap }

import javax.crypto
import java.net.URI

import org.apache.commons.codec.binary.Base64.encodeBase64
import org.apache.commons.httpclient.HttpMethod

case class Consumer(key: String, secret: String)
case class Token(value: String, secret: String)

object OAuth {
  private val UTF_8 = "UTF-8"
  private val charset = UTF_8

  private def encode (string: String): String = java.net.URLEncoder.encode(string, charset)
  private def decode (string: String): String = java.net.URLDecoder.decode(string, charset)

  private def encodePercent (string: String): String = encode(string) replace ("+", "%20") replace ("%7E", "~") replace ("*", "%2A")
  private def encodePercent (seq: Seq[String]): String = seq map encodePercent mkString "&"
  private def encodePercent (tuple: (String, Any)): (String, String) = (encodePercent(tuple._1), encodePercent(tuple._2.toString))

  private def bytes(string: String) = string.getBytes(UTF_8)

  val split_decode: (String => IMap[String, String]) = {
    case null => IMap.empty
    case query => IMap.empty ++ query.trim.split('&').map { nvp =>
      ( nvp split "=" map decode ) match { 
        case Array(name) => name -> ""
        case Array(name, value) => name -> value
      }
    }
  }

  def sign(httpMethod: HttpMethod, consumer: Consumer, token: Token): HttpMethod = {
    val method = httpMethod.getName
    val url = httpMethod.getURI.toString.split('?')(0)
    val user_params = split_decode(httpMethod.getURI.getQuery)
    val headers = signatureHeaders(method, url, user_params, consumer, token, None, None)
    val encodedHeaders = headers.map { 
      case (k, v) => (encode(k)) + "=\"%s\"".format(encode(v))
    }.mkString(",")

    httpMethod.addRequestHeader("Authorization", "OAuth " + encodedHeaders)
    httpMethod
  }

  def signatureHeaders(method: String, url: String, user_params: Map[String, Any], consumer: Consumer, token: Token, verifier: Option[String], callback: Option[String]): scala.collection.immutable.Map[java.lang.String, java.lang.String] = {
    val oauth_params = IMap(
      "oauth_consumer_key" -> consumer.key,
      "oauth_signature_method" -> "HMAC-SHA1",
      "oauth_timestamp" -> (System.currentTimeMillis / 1000).toString,
      "oauth_nonce" -> System.nanoTime.toString,
      "oauth_version" -> "1.0"
    ) ++ Map("oauth_token" -> token.value) ++ 
      verifier.map { "oauth_verifier" -> _ } ++
      callback.map { "oauth_callback" -> _ } map encodePercent

    val encoded_ordered_params = (
      new TreeMap[String, String] ++ (user_params ++ oauth_params)
    ) map { case (k, v) => k + "=" + v } mkString "&"

    val message = encodePercent(method :: url :: encoded_ordered_params :: Nil)

    val SHA1 = "HmacSHA1"
    val key_str = encodePercent(consumer.secret :: (token.secret) :: Nil)
    val key = new crypto.spec.SecretKeySpec(bytes(key_str), SHA1)
    val sig = {
      val mac = crypto.Mac.getInstance(SHA1)
      mac.init(key)
      new String(encodeBase64(mac.doFinal(bytes(message))))
    }
    oauth_params + ("oauth_signature" -> sig)
  }
}
