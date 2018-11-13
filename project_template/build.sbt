name := "mergeWikilinksWikipedia"

version := "1.0"

scalaVersion := "2.11.8"  // MAKE SURE THIS IS THE CORRECT VERSION ON THE MACHINE

//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0" // Commandline parameters parser
libraryDependencies += "io.circe" %% "circe-core" % "0.9.1" // circe is used for case class to json
libraryDependencies += "io.circe" %% "circe-generic" % "0.9.1"
libraryDependencies += "io.circe" %% "circe-parser" % "0.9.1"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.1" //% "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.1" //% "provided"

assemblyMergeStrategy in assembly := {   // used for sbt assembly to bulndle all dependencies inside one jar and send it to cloud cluster for spark
  case PathList("org","aopalliance", xs @ _*) => MergeStrategy.last
  case PathList("javax", "inject", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "overview.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}


