import com.streamer.twitter._

import org.apache.commons.httpclient._

class StreamingClientSpecHelper(val username: String, val password: String, val streamProcessor: StreamProcessor)
  extends BasicClient with StreamingMethods {
  val client = this.getHttpClient

  /**
   * Returns a Basic Auth ready http client
   */
  def getClient(method: HttpMethod): HttpClient = this.client
}
