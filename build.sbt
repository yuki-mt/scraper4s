name := """scraper4s"""

version := "1.0"

scalaVersion := "2.11.7"
organization := "com.yukimt"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.17" % "test",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0",
  "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0"
)

publishTo := Some(Resolver.file("scraper4s",file("./docs"))(Patterns(true, Resolver.mavenStyleBasePattern)))
