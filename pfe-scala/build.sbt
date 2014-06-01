name := "pfe-scala"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.webjars" % "requirejs" % "2.1.11-1",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

resolvers += "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"

scalacOptions += "-feature"

includeFilter in (Assets, LessKeys.less) := "shop.less"

pipelineStages := Seq(rjs, gzip, digest)

CoffeeScriptKeys.bare := true

RjsKeys.mainModule := "shop"

RjsKeys.paths += "routes" -> ("routes", "empty:")

lazy val `pfe-scala` = project.in(file(".")).enablePlugins(PlayScala)