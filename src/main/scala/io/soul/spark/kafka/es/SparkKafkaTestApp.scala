package io.soul.spark.kafka.es

import org.apache.spark.sql.Encoders
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._

case class LineItemData(orderkey: Double, partkey: Double, supplierkey: Double, linenumber: Double, quantity: Double, extendedprice: Double, discount: Double, tax: Double, returnflag: String, status: String, shipdate: String, commitdate: String, receiptdate: String, shipinstructions: String, shipmode: String, comment: String)

object SparkKafkaTestApp {
  def main(args: Array[String]) = {

    val sparkS = SparkSession.builder().getOrCreate()

    import sparkS.implicits._

    val df = sparkS.readStream.format("kafka").option("kafka.bootstrap.servers", "192.168.0.105:9092").option("subscribe", "li_eight").option("startingOffsets", "latest").load()

    df.printSchema()

    val schema = Encoders.product[LineItemData].schema

    //    val rawValues = df.selectExpr("CAST(value AS STRING)").as[String]
    //    val jsonValues = rawValues.select(from_json($"value", schema) as "record")
    //    val liData = jsonValues.select("record.*").as[LineItemData]

    //
    //    val rawValues = df.selectExpr("CAST(value AS STRING)")
    //    val liData = rawValues.select(from_json(col("value"), schema).as("data")).select("data.*")
    //
    //
    //    liData.writeStream.format("console").outputMode("append").start().awaitTermination()

    //=====Old ======
    val rawValues = df.selectExpr("CAST(value AS STRING)").as[String]
    val jsonValues = rawValues.select(from_json($"value", schema) as "record")
    val liData = jsonValues.select("record.*").as[LineItemData]

    val query = liData.writeStream.queryName("temp").outputMode("append").format("console").start().awaitTermination()

    //sparkS.sql("select * from temp").show()

  }

}