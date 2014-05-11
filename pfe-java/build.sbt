name := "pfe-java"

version := "1.0-SNAPSHOT"

libraryDependencies += javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api")

libraryDependencies += "org.hibernate" % "hibernate-entitymanager" % "4.3.4.Final"

includeFilter in (Assets, LessKeys.less) := "shop.less"

lazy val `pfe-java` = project.in(file(".")).enablePlugins(PlayJava)