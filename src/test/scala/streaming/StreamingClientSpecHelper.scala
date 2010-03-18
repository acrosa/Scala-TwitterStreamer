import com.linkedin.led.twitter.streaming._

import org.apache.commons.httpclient.HttpMethod

class StreamingClientSpecHelper(username: String, password: String, streamProcessor: StreamProcessor)
  extends StreamingClient(username, password, streamProcessor)