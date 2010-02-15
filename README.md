# Twitter Streaming Client

## Motivation
Create a library to support the Twitter streaming API, the reconnect strategy, and error policies specified on their documentation.

## What's in the package

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
3. $ sbt update
4. $ sbt run
5. Select one of the two sample clients.

## Usage

Create a client and run it:

    import com.linkedin.led.twitter.streaming._
    val twitterClient = new StreamingClient(username, password, processor)
    twitterClient.sample

## API Methods
- Sample Returns a random sample of all public statuses.
- Filter Returns public statuses that match one or more filter predicates.
- Firehose Returns all public statuses. The Firehose is not a generally available resource.
- Links Returns all statuses containing http: and https:. The links stream is not a generally available resource.
- Retweet Returns all retweets. The retweet stream is not a generally available resource.

### Custom behavior
First you need to define what you want to do with the stream. Here's an example that just prints every line we get to stdout:

1.
CustomProcessing.scala

   package com.linkedin.led.twitter.streaming
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

2.
  package com.linkedin.led.twitter.streaming
  object TestStream {
    def main(args: Array[String]) = {
      val username = Config.readString("username")
      val password = Config.readString("password")
      val processor = new CustomProcessor()
      
      val twitterClient = new StreamingClient(username, password, processor)
      twitterClient.sample
    }
  }

Alejandro Crosa <<alejandrocrosa@gmail.com>>
