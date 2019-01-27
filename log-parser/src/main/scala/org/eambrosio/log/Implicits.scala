package org.eambrosio.log

import java.util.Collections

import JavaSessionize.avro.LogTrace
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.WindowedSerdes
import org.apache.kafka.streams.scala.Serdes

trait Implicits extends KafkaConfiguration {
  def getAvroLogTraceSerde(schemaRegistryHost: String, schemaRegistryPort: String) = {
    val specificAvroSerde = new SpecificAvroSerde[LogTrace]()
    specificAvroSerde.configure(
      Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, s"""http://$schemaRegistryHost:$schemaRegistryPort/"""),
      false)
    specificAvroSerde
  }

    implicit val avroLogTraceSerde: SpecificAvroSerde[LogTrace] = getAvroLogTraceSerde(schemaRegistryHost, schemaRegistryPort)
    implicit val stringSerde: Serde[String] = Serdes.String
    implicit val longSerde: Serde[Long] = Serdes.Long
    implicit val stringTimeWindowedSerde: WindowedSerdes.TimeWindowedSerde[String] = new WindowedSerdes.TimeWindowedSerde[String](Serdes.String)

}
