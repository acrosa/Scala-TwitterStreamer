package com.streamer.twitter

import com.streamer.twitter.config._

object TestStream {
  def main(args: Array[String]) = {
    val processor = new OutputStreamProcessor()
    val username = Config.readString("username")
    val password = Config.readString("password")

    val twitterClient = new BasicStreamingClient(username, password, processor)
    twitterClient.sample
  }
}
