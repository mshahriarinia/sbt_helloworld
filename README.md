Make sure you have sbt and jdk in your machine and paths.

To use with Spark: The details are here: <https://mindfulmachines.io/blog/2018/3/18/wikipedia-data-in-spark-and-scala-updated>

To generate something like this repo (not exactly same structure) you can simply run the following in commandline:

```scala
PROJECT_NAME='myproj'
mkdir $PROJECT_NAME
cd $PROJECT_NAME
COMPANY_PATH='/com/morteza/'
mkdir -p src/{main,test}/{java,scala}$COMPANY_PATH
mkdir -p lib project target

# touch src/{main,test}/{java,scala}$COMPANY_PATH.gitkeep  # keep empty directories in git to maintain directory structure.

echo 'addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.8") // to create a fat jar containing all the dependencies' > project/assembly.sbt

# create an initial build.sbt file
echo 'name := '\"$PROJECT_NAME\"'

version := "1.0"

scalaVersion := "2.11.8"  // MAKE SURE THIS IS THE CORRECT VERSION ON THE MACHINE

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0" // Commandline parameters parser
libraryDependencies += "io.circe" %% "circe-core" % "0.9.1" // circe is used for case class to json
libraryDependencies += "io.circe" %% "circe-generic" % "0.9.1"
libraryDependencies += "io.circe" %% "circe-parser" % "0.9.1"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
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

' > build.sbt

curl https://www.gitignore.io/api/scala,sbt,intellij,java > .gitignore
echo '
import org.apache.spark.sql.SparkSession

object Main {
    Console.println("Hello World!")
  val APP_NAME = "enwiki_props"

  val isInputLocal = true

  def main(args: Array[String]): Unit = {

      // spark needs to be initialized inside main. https://stackoverflow.com/a/46274029/715483
      if (isInputLocal) {
        println("Input is local, setting local master for spark.")
        SparkSession.builder.appName("WikiPagePropsParser").master("local[4]").getOrCreate()
      } else {
        println("Input is NOT local, setting master to default.")
        SparkSession.builder.appName("WikiPagePropsParser").getOrCreate()
      }

      val sparkContext = SparkSession.builder().getOrCreate().sparkContext
    }
 }' > src/main/scala/HelloWorld.scala


echo '
package com.morteza

import scopt.OptionParser

/**
  * Define commandline arguments and how to treat them
  */
case class Args(input: String = null,
                output: String = null,
                timestamp: String = null
               )

object Args {
  val optionParser: OptionParser[Args] = new scopt.OptionParser[Args]("Wikidata") {
    head("This program runs a spark job on wikidata.")

    // call opt()  which needs arg type, and short and long form param name
    opt[String]('i', "input")
      .required()
      .text("input is the input path")
      .action {
        (paramValue: String, argsCopy: Args) =>
          argsCopy.copy(input = paramValue)
      }

    opt[String]('o', "output")
      .required()
      .text("output is the output path")
      .action {
        (paramValue: String, argsCopy: Args) =>
          argsCopy.copy(output = paramValue)
      }
  }
}' > src/main/scala/com/morteza/Args.scala

echo '
package com.morteza

import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.spark.sql.SparkSession

object Main {

  def initSpark(args: Args): Unit = {
    if (args.input.substring(0, 4).equals("gs://")) {
      println("Input is NOT local, setting master to default.")
      SparkSession.builder.appName("Wiki-links Data Processor Job").getOrCreate()
    }
    else {
      println("Input is local, setting local master for spark.")
      SparkSession.builder.appName("Wiki-links Data Processor Job").master("local[2]").getOrCreate()
    }
  }

  def process(args: Args): Unit = {
    initSpark(args)
    val sparkSession = SparkSession.builder().getOrCreate()
    val sparkContext = sparkSession.sparkContext

  }

  def main(args: Array[String]): Unit = {
    Args.optionParser.parse(args, Args()) map { args: Args =>
      val format = new SimpleDateFormat("y_M_d__H_m_s")
      val nowStr = format.format(Calendar.getInstance().getTime)

      process(args.copy(timestamp = nowStr))
    } getOrElse {
      println("arguments are not properly provided, terminating.")
      System.exit(0)
    }
  }
}' > src/main/scala/com/morteza/Main.scala

sbt assembly
java -jar /Users/morteza/zProject/test/mylala/myproj/target/scala-2.11/myproj-assembly-1.0.jar

sbt compile  
sbt run
ls -a
sbt package
scala target/scala-2.11/"$PROJECT_NAME"_2.11-1.0.jar

```    
