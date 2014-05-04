name := "pfe-scala"

version := "1.0-SNAPSHOT"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"

libraryDependencies += PlayKeys.jdbc

libraryDependencies += "com.typesafe.slick" %% "slick" % "2.0.1"

scalacOptions += "-feature"

lazy val `pfe-scala` = project.in(file(".")).addPlugins(PlayScala)