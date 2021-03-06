package com.mdcurran.utils

import org.apache.spark.sql.SparkSession
import org.scalatest.FlatSpec

class CustomSparkSessionTests extends FlatSpec {

  val spark: SparkSession = CustomSparkSession.createSparkSession

  "The createSparkSession function" should "create an active SparkSession" in {
    assert(!spark.sparkContext.isStopped)
  }

  it should "have an appName of the custom Spark object" in {
    assert(spark.sparkContext.appName == "CustomSparkSession$")
  }

  it should "create a local instance of a SparkContext" in {
    assert(spark.sparkContext.isLocal)
  }

  it should "have the master set to local" in {
    assert(spark.sparkContext.master == "local")
  }

}
