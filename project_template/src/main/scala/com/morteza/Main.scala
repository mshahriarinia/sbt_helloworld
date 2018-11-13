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
}
