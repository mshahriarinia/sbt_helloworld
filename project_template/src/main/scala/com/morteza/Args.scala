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
}
