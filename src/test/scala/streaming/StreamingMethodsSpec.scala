import com.streamer.twitter._

import org.specs._

import scala.collection.mutable.ArrayBuffer
import org.apache.commons.httpclient._

import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.GetMethod

object StreamingMethodsSpec extends Specification {
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
      val request = client.buildGet("www.github.com", params)
      request.getURI mustEqual new URI("www.github.com?count=10", false)
    }

    "build a post request with specified params" in {
      val params = new ArrayBuffer[NameValuePair]
        params += new NameValuePair("follow", "cool,works,awesome")
      val request = client.buildPost("www.github.com", params)
      request.getURI mustEqual new URI("www.github.com", false)
      request.getParameters.length mustEqual 1
      request.getParameter("follow") mustEqual new NameValuePair("follow", "cool,works,awesome")
    }
  }
}