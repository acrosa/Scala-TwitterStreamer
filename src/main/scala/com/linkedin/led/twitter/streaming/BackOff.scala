package com.linkedin.led.twitter.streaming

/**
 * BackOff
 *
 * This class implements the waiting strategy when errors arise, and trust me, they will.
 */
case class BackOff(var backOffTime: Long, capBackOffAt: Long) {

  def backOff = {
    Thread.sleep(backOffTime)
    // Let's wait some more
    backOffTime *=  2
    // Limit the wait to the specified cap
    if(backOffTime > capBackOffAt) {
      backOffTime = capBackOffAt
    }
  }

  /**
   * After all errors are resolved (ie successful connection), we reset the sleeping counter.
   */
  def reset() = { backOffTime = 0 }
}
