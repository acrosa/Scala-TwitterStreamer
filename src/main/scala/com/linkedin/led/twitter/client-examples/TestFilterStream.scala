package com.linkedin.led.twitter.streaming

object TestFilterStream {
  def main(args: Array[String]) = {
    val username = Config.readString("username")
    val password = Config.readString("password")
    val processor = new OutputStreamProcessor()

    val twitterClient = new StreamingClient(username, password, processor)
    twitterClient.filter(0, "length", Set(), Set("apple", "ipad"))
  }
}
