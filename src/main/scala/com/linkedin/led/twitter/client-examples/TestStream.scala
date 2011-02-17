package com.linkedin.led.twitter.streaming

object TestStream {
  def main(args: Array[String]) = {
    val username = Config.readString("username")
    val password = Config.readString("password")

    val processor = Config.getString("processorClass") match {
	    case Some(value) => Class.forName(value).newInstance.asInstanceOf[StreamProcessor]
	    case _ => new OutputStreamProcessor()
	}
	
    val twitterClient = new StreamingClient(username, password, processor)
    twitterClient.sample
  }
}
