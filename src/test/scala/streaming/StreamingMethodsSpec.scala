import com.linkedin.led.twitter.streaming._

import org.specs._

import org.specs.mock.Mockito
import org.mockito.Mock._
import org.mockito.Mockito._
import org.mockito.Mockito.doNothing
import org.mockito.Matchers._

import scala.collection.mutable.ArrayBuffer
import org.apache.commons.httpclient._

import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.GetMethod

object StreamingMethodsSpec extends Specification with Mockito {
  "Streaming Methods" should {

    var client: StreamingClientSpecHelper = null
    var o: OutputStreamProcessor = null

    doBefore {
      o = new OutputStreamProcessor
      client = new StreamingClientSpecHelper("user", "password", o)
    }

    "build a Get request with specified params" in {
      val params = new ArrayBuffer[String]
        params += "count=10"
      val request = client.buildGet("www.linkedin.com", params)
      request.getURI mustEqual new URI("www.linkedin.com?count=10")
    }

    "build a post request with specified params" in {
      val params = new ArrayBuffer[NameValuePair]
        params += new NameValuePair("follow", "linkedin,inapps,cool")
      val request = client.buildPost("www.linkedin.com", params)
      request.getURI mustEqual new URI("www.linkedin.com")
      request.getParameters.length mustEqual 1
      request.getParameter("follow") mustEqual new NameValuePair("follow", "linkedin,inapps,cool")
    }
  }
}