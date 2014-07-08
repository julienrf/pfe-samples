lazy val `pfe-scala` = project.in(file(".")).enablePlugins(PlayScala).dependsOn(service, oauth)

name := "pfe-scala"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  filters,
  cache,
  "org.webjars" % "requirejs" % "2.1.11-1",
  "com.google.inject" % "guice" % "3.0",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

resolvers += "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"

scalacOptions += "-feature"

includeFilter in (Assets, LessKeys.less) := "shop.less"

pipelineStages := Seq(rjs, gzip, digest)

CoffeeScriptKeys.bare := true

RjsKeys.mainModule := "shop"

RjsKeys.paths += "routes" -> ("routes", "empty:")

lazy val service = project.settings(
  libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "2.0.1",
    jdbc,
    ws,
    "org.specs2" %% "specs2-core" % "2.3.12" % "test",
    component("play-test") % "test"
  )
)

lazy val oauth = project.enablePlugins(PlayScala).settings(
  libraryDependencies ++= Seq(
    ws,
    "com.google.inject" % "guice" % "3.0"
  )
)