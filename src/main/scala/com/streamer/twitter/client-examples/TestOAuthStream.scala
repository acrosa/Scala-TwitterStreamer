package com.streamer.twitter

import com.streamer.twitter.config._
import com.streamer.twitter.oauth._

object TestOAuthStream {
  def main(args: Array[String]) = {
    val processor = new OutputStreamProcessor()
    val consumer = Consumer(Config.readString("consumer.key"), Config.readString("consumer.secret"))
    val token = Token(Config.readString("access.token"), Config.readString("access.secret"))

    val twitterClient = new OAuthStreamingClient(consumer, token, processor)
    twitterClient.siteStream(Set(16741237,14344469,134879387,39848709,9980812,55600683,18948541,16031975,69128362,14452238,12301142,13058772,17765013,32692341,14470999,188093253,21493276,102131542,71363379,29239854,8526432))
  }
}
