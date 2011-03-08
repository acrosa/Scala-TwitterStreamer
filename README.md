# Twitter Streaming Client

## Motivation
Create a library to support the Twitter streaming API, the reconnect strategy, OAuth authentication, and error policies specified on their documentation.

## What's in the package

- Basic GET OAuth support (POST *doesn't work* yet)
- Apache HttpClient based.
- Back off strategy built in, so in the case of unexpected errors the library will reconnect:
  - TCP errors start at 250 miliseconds and cap at 16 seconds
  - HTTP errors start at 10 seconds and cap at 240 seconds
- Easy to implement your own parsing and processing of tweets.

## Requirements

- sbt (get it at http://code.google.com/p/simple-build-tool/)

## Give it a try

1. $ cd Scala-TwitterStreamer
2. Edit config/TwitterStreamer.conf and add your Twitter username and password *Important*
   If you want to use OAuth authentication enter your Consumer pair Key, and Access token secret. You can obtain your Access Token by going to http://dev.twitter.com/apps
3. $ ./sbt update
4. $ ./sbt run
5. Select one of the two sample clients.

## Run the sample app
1. $ cd Scala-TwitterStreamer
2. Edit config/TwitterStreamer.conf and add your Twitter username and password *Important*
   If you want to use OAuth authentication enter your Consumer pair Key, and Access token secret. You can obtain your Access Token by going to http://dev.twitter.com/apps
3. $ ./sbt update
4. $ ./sh example/run_example.sh
5. watch the tweets on your screen

## Usage

Create a client and run it:

    import com.streamer.twitter._
    val twitterClient = new StreamingClient(username, password, processor)
    twitterClient.sample

## API Methods
- Sample Returns a random sample of all public statuses.
- Filter Returns public statuses that match one or more filter predicates.
- Firehose Returns all public statuses. The Firehose is not a generally available resource.
- Links Returns all statuses containing http: and https:. The links stream is not a generally available resource.
- Retweet Returns all retweets. The retweet stream is not a generally available resource.
- Sites Stream Returns all events for the users you specify to follow and that OAuth'ed to your application.

### Custom behavior
First you need to define what you want to do with the stream. Here's an example that just prints every line we get to stdout:


   import com.streamer.twitter._
   import com.streamer.twitter.oauth._
   import com.streamer.twitter.config._
   import java.io.InputStream
   import java.io.InputStreamReader
   import java.io.BufferedReader

   class CustomProcessor extends StreamProcessor {
     override def process(is: InputStream): Unit = {
       val reader: BufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"))
       
       var line = reader.readLine()
       while (line != null) {
         println(line)
         line = reader.readLine()
       }
       
       is.close
     }
    }

  object TestOAuthStream {
    def main(args: Array[String]) = {
      val processor = new CustomProcessor()
      val consumer = Consumer(Config.readString("consumer.key"), Config.readString("consumer.secret"))
      val token = Token(Config.readString("access.token"), Config.readString("access.secret"))

      val twitterClient = new OAuthStreamingClient(consumer, token, processor)
      twitterClient.siteStream(Set(16741237,14344469)) // The ids we are going to track, they should have OAuth'ed to us
    }
  }

Alejandro Crosa <<alejandrocrosa@gmail.com>>
