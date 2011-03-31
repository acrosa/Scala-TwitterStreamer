package com.streamer.twitter

/**
 * BackOff
 *
 * This class implements the waiting strategy when errors arise, and trust me, they will.
 */
case class BackOff(var origBackOffTime: Long, capBackOffAt: Long) {
  var backOffTime = origBackOffTime

  def backOff = {
    Thread.sleep(backOffTime)
    // Let's wait some more
    backOffTime *= 2
    // Limit the wait to the specified cap
    if(backOffTime > capBackOffAt) {
      backOffTime = capBackOffAt
    }
  }

  /**
   * After all errors are resolved (ie successful connection), we reset the sleeping counter.
   */
  def reset() = { backOffTime = origBackOffTime }
}
