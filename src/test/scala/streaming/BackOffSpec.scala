import com.linkedin.led.twitter.streaming._

import org.specs._

object BackOffSpec extends Specification {
  "Back Off strategy" should {

    var backOff: BackOff = null

    doBefore {
      backOff = BackOff(250, 16000)
    }

    "set the correct backOffTime and capBackOffAt" in {
      backOff.backOffTime mustEqual 250
      backOff.capBackOffAt mustEqual 16000
    }

    "increment the back off time incrementally" in {
      backOff.backOff
      backOff.backOffTime mustEqual 500
      backOff.backOff
      backOff.backOffTime mustEqual 1000
    }

    "allow to reset the incremental back off time" in {
      backOff.backOff
      backOff.backOffTime mustEqual 500
      backOff.reset()
      backOff.backOffTime mustEqual 0
    }
  }
}