package com.streamer.twitter

import com.streamer.twitter.config._

object TestFilterStream {
  def main(args: Array[String]) = {
    val username = Config.readString("username")
    val password = Config.readString("password")
    val processor = new OutputStreamProcessor()

    val twitterClient = new BasicStreamingClient(username, password, processor)
    twitterClient.track(Set("apple", "ipad"))
  }
}
