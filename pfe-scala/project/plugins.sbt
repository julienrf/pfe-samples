// Comment to get more information during initialization
logLevel := Level.Warn

resolvers += Resolver.url("Typesafe Ivy Snapshots Repository", url("http://repo.typesafe.com/typesafe/ivy-snapshots"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe snasphots" at "http://repo.typesafe.com/typesafe/snapshots/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3-2014-04-06-d2bc002-SNAPSHOT")
