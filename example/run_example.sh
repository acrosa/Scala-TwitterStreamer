#!/bin/sh
# Sample demo

JAVA_OPTS="-server"
JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=22134 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
MAIN_CLASS="com.streamer.twitter.TestStream"

java ${JAVA_OPTS} -classpath build/sbt-launch.jar:lib_managed/compile/'*':project/boot/scala-2.7.7/lib/'*':lib/'*' ${MAIN_CLASS}
