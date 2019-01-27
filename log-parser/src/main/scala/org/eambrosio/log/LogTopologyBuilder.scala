package org.eambrosio.log

import java.time.Duration

import JavaSessionize.avro.LogTrace
import org.apache.kafka.streams.kstream.{TimeWindows, Windowed}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, Topology}

object LogTopologyBuilder extends App
  with Printed
  with Implicits {

  def createTopology(): Topology = {


    val oneMinuteWindow = TimeWindows.of(Duration.ofSeconds(60))
    val builder = new StreamsBuilder()

    val initialStream: KStream[String, String] = builder.stream(inputTopic)

    val logStream: KStream[Long, LogTrace] = initialStream
      .selectKey((_: String, value: String) => value.split(" ")(0).toLong)
      .mapValues { value =>
        val trace: Array[String] = value.split(" ")
        new LogTrace(trace(0).toLong, trace(1), trace(2))
      }


    val windowedConnectedFromStream: KStream[Windowed[String], Long] =
      logStream
        .filter((_, trace) => trace.getFrom == "host1")
        .groupBy((_, trace) => trace.getFrom)
        .windowedBy(oneMinuteWindow)
        .count()
        .toStream

    val windowedConnectedToStream: KStream[Windowed[String], Long] =
      logStream
        .filter((_, trace) => trace.getTo == "host3")
        .groupBy((_, trace) => trace.getTo)
        .windowedBy(oneMinuteWindow)
        .count()
        .toStream

    windowedConnectedFromStream
      .to("output-topic")

    windowedConnectedToStream
      .to("output-topic")


    windowedConnectedFromStream.print(sysoutConnectedFrom)
    windowedConnectedToStream.print(sysoutConnectedTo)


    builder.build()
  }

  private val topology: Topology = createTopology()
  val ks = new KafkaStreams(topology, kafkaConfig)
  ks.start
}

