package io.soul.spark.kafka.es

import org.apache.spark.sql.Encoders
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

case class LineItemData(orderkey: Double, partkey: Double, supplierkey: Double, linenumber: Double, quantity: Double, extendedprice: Double, discount: Double, tax: Double, returnflag: String, status: String, shipdate: String, commitdate: String, receiptdate: String, shipinstructions: String, shipmode: String, comment: String)

object SparkKafkaTestApp {
  def main(args: Array[String]) = {

    val sparkS = SparkSession.builder().getOrCreate()
    import sparkS.implicits._

    val df = sparkS.readStream.format("kafka").option("kafka.bootstrap.servers", "192.168.0.105:9092").option("subscribe", "li_eight").option("startingOffsets", "latest").load()

    
    
    val schema = Encoders.product[LineItemData].schema

    val rawValues = df.selectExpr("CAST(value AS STRING)").as[String]
    val jsonValues = rawValues.select(from_json($"value", schema) as "record")
    val liData = jsonValues.select("record.*").as[LineItemData]
    
    print(liData)

    val query = liData.writeStream.queryName("temp").outputMode("append").format("console").start()

    val d = sparkS.sql("select * from temp")
    d.show()

    sparkS.stop()
  }

}
