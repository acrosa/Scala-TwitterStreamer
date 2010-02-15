package com.linkedin.led.twitter.streaming

import java.io.InputStream

/**
 * StreamProcessor
 *
 * In order to process the stream of tweets, you have to concretely define
 * on your subclass what to do with the InputStream
 */
abstract class StreamProcessor {

  // This method customizes the handling of the stream
  def process(is: InputStream): Unit
}
