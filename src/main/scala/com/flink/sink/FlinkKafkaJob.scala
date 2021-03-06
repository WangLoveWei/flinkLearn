package com.flink.sink

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011

/**
  * @author 王犇
  * @date 2019/11/22 10:34
  * @version 1.0
  */
object FlinkKafkaJob {
  def main(args: Array[String]): Unit = {
    val util = new MyKafkaUtil
    val product: FlinkKafkaProducer011[String] = util.getProduct("student");

    val tools = ParameterTool.fromArgs(args);
    val see: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    val stream: DataStream[String] = see.socketTextStream(tools.get("host"), tools.get("port").toInt)

    stream.print()

    import org.apache.flink.api.scala._
    stream.flatMap(_.split(","))
      .filter(_.length > 3)
      .map((_, 1))
      .keyBy(0)
      .reduce((ch1, ch2) => (ch1._1, ch1._2 + ch2._2))
      .map(a => (a._1 + "," + a._2))
      .addSink(product)

    see.execute()
  }
}
