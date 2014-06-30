import sbt._

class TwitterStreamerProject(info: ProjectInfo) extends DefaultProject(info) with AutoCompilerPlugins
{
  override def useDefaultConfigurations = true

  val scalatest = "org.scala-tools.testing" % "scalatest" % "0.9.5"
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.2"
  val junit     = "junit" % "junit" % "4.5"

  val httpclient = "commons-httpclient" % "commons-httpclient" % "3.1"
  val logging    = "commons-logging" % "commons-logging" % "1.1"
  val configgy = "net.lag" % "configgy" % "2.0.0" intransitive()

  // Logging
  System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");

  // Show unchecked errors when compiling
  override def compileOptions = super.compileOptions ++ Seq(Unchecked)
}
