package com.mdcurran.utils

import com.mdcurran.utils.CustomSparkSession._
import cucumber.api.DataTable
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{DataType, DataTypes, StructField, StructType}

import scala.collection.JavaConversions._

object CucumberConversions {

  val spark: SparkSession = createSparkSession

  def dataTableToDataFrame(data: DataTable): DataFrame = {
    val columns = extractColumns(data)
    val schema = deriveSchema(columns)
    val rows = extractRows(data, schema)
    spark.sqlContext.createDataFrame(spark.sparkContext.parallelize(rows), schema)
  }

  def extractColumns(data: DataTable): List[(String, DataType)] = {
    data.topCells()
      .map(_.split(":"))
      .map(splits => (splits(0), splits(1).trim.toLowerCase))
      .map {
        case (name, "string") => (name, DataTypes.StringType)
        case (name, "int") => (name, DataTypes.IntegerType)
        case (name, _) => (name, DataTypes.StringType)
      }.toList
  }

  def deriveSchema(columns: List[(String, DataType)]): StructType = {
    StructType(columns.map {
      case (name, dataType) => StructField(name, dataType)
    })
  }

  def extractRows(data: DataTable, schema: StructType): List[Row] = {
    data.asMaps(classOf[String], classOf[String])
      .map { row =>
        val values = row
          .values()
          .zip(extractColumns(data))
          .map { case (v, (_, dt)) => (v, dt) }
          .map {
            case (v, DataTypes.StringType) => v
            case (v, DataTypes.IntegerType) => v.toInt
          }.toSeq
        Row.fromSeq(values)
      }.toList
  }

}
