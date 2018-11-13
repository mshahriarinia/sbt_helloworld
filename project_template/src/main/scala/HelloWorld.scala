
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
 }
