package com.linkedin.led.twitter.streaming

import com.linkedin.led.twitter.config._

object TestStream {
  def main(args: Array[String]) = {
    val username = Config.readString("username")
    val password = Config.readString("password")
    val processor = new OutputStreamProcessor()

    val twitterClient = new StreamingClient(username, password, processor)
    twitterClient.sample
  }
}
