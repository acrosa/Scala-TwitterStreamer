import sbt._

class TwitterStreamerProject(info: ProjectInfo) extends DefaultProject(info) with AutoCompilerPlugins
{
  override def useDefaultConfigurations = true

  val scalatest = "org.scala-tools.testing" % "scalatest" % "0.9.5" % "test->default"
  val specs     = "org.scala-tools.testing" % "specs" % "1.5.0"
  val mockito   = "org.mockito" % "mockito-all" % "1.7"
  val junit     = "junit" % "junit" % "4.5"

  val httpclient = "commons-httpclient" % "commons-httpclient" % "3.1"
  val logging    = "commons-logging" % "commons-logging" % "1.1"
  //val configgy   = "net.lag" % "configgy" % "1.4.7" from "http://repository.jboss.org/maven2/net/lag/configgy/1.4.7/configgy-1.4.7.jar"

  // Logging
  System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
  // System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
  // System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");

  // Show unchecked errors when compiling
  override def compileOptions = super.compileOptions ++ Seq(Unchecked)
}
