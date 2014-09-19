lazy val `pfe-scala` = project.in(file(".")).enablePlugins(PlayScala).dependsOn(service, oauth)

name := "pfe-scala"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  filters,
  cache,
  "org.webjars" % "requirejs" % "2.1.11-1",
  "com.google.inject" % "guice" % "3.0",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

scalacOptions += "-feature"

includeFilter in (Assets, LessKeys.less) := "shop.less"

pipelineStages := Seq(rjs, gzip, digest)

CoffeeScriptKeys.bare := true

RjsKeys.mainModule := "shop"

RjsKeys.paths += "routes" -> ("routes", "empty:")

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

lazy val service = project.settings(
  scalaVersion := "2.11.4",
  resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "2.1.0",
    jdbc,
    ws,
    "org.specs2" %% "specs2-core" % "2.3.12" % "test",
    component("play-test") % "test"
  ),
  resolvers += "Typesafe" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val oauth = project.enablePlugins(PlayScala).settings(
  scalaVersion := "2.11.4",
  resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    ws,
    "com.google.inject" % "guice" % "3.0"
  )
)