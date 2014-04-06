resolvers += "Typesafe snasphots" at "http://repo.typesafe.com/typesafe/snapshots/"

name := """pfe-scala"""

version := "1.0-SNAPSHOT"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"

scalacOptions += "-feature"

play.Project.playScalaSettings
