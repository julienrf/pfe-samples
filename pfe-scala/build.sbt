name := "pfe-scala"

version := "1.0-SNAPSHOT"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"

libraryDependencies += jdbc

libraryDependencies += "com.typesafe.slick" %% "slick" % "2.0.1"

scalacOptions += "-feature"

includeFilter in (Assets, LessKeys.less) := "shop.less"

pipelineStages := Seq(rjs, gzip, digest)

CoffeeScriptKeys.bare := true

lazy val `pfe-scala` = project.in(file(".")).enablePlugins(PlayScala)