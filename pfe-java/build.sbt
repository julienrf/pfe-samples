lazy val `pfe-java` = project.in(file(".")).enablePlugins(PlayJava).dependsOn(service)

name := "pfe-java"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  filters,
  cache,
  javaWs,
  "org.webjars" % "requirejs" % "2.1.11-1",
  "com.google.inject" % "guice" % "3.0"
)

resolvers += "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"

includeFilter in (Assets, LessKeys.less) := "shop.less"

pipelineStages := Seq(rjs, gzip, digest)

CoffeeScriptKeys.bare := true

RjsKeys.mainModule := "shop"

RjsKeys.paths += "routes" -> ("routes", "empty:")

lazy val service = project.settings(
  libraryDependencies ++= Seq(
    javaWs,
    javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"),
    "org.hibernate" % "hibernate-entitymanager" % "4.3.4.Final",
    component("play-test") % "test"
  )
)