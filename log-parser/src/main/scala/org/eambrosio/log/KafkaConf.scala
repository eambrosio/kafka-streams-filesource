package org.eambrosio.log

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.scala.Serdes

import scala.util.Random


trait KafkaProperties {

  //The clean-up method does not work for us so we generate a new app key for each test to overcome this issue
  def generateRandomApplicationKey: String = {
    val randomSeed = Random.alphanumeric
    randomSeed.take(12).mkString
  }

  val zookeeperHost = "zookeeper"
  val zookeeperPort = "2181"
  val zookeeperPortAsInt = zookeeperPort.toInt
  val kafkaHost = "kafka"
  val kafkaPort = "9092"
  val applicationKey = generateRandomApplicationKey
  val schemaRegistryHost = "schema_registry"
  val schemaRegistryPort = "8081"
  val logDir = "/tmp/kafka-logs"
  val inputTopic = "connect-test"
  val outputTopic = "output-topic"

}

trait KafkaGlobalProperties {

  val defaultAutoCreateTopics = "true"
  val defaultPartitions = "1"
  val defaultBrokerIdProp = "0"
  val bootstrapServerKey = "bootstrap.servers"
  val schemaRegistryUrlKey = "schema.registry.url"
  val keySerializerKey = "key.serializer"
  val keyDeserializerKey = "key.deserializer"
  val listenersKey = "listeners"
  val groupIdKey = "group.id"
  val groupIdValue = "prove_group"
  val valueSerializerKey = "value.serializer"
  val valueDeserializerKey = "value.deserializer"
  val applicationIdKey = "application.id"
  val autoCreateTopicsKey = "auto.create.topics.enable"
  val zookeeperPortConfig = "zookeeper.port"
  val zookeeperHostConfig = "zookeeper.host"
  val cacheMaxBytesBufferingKey = "cache.max.bytes.buffering"
}


trait KafkaConfiguration extends KafkaProperties with KafkaGlobalProperties {
  val kafkaConfig = new Properties()
  kafkaConfig.put(bootstrapServerKey, s"""$kafkaHost:$kafkaPort""")
  kafkaConfig.put("zookeeper.host", zookeeperHost)
  kafkaConfig.put("zookeeper.port", zookeeperPort)
  kafkaConfig.put(schemaRegistryUrlKey, s"""http://$schemaRegistryHost:$schemaRegistryPort""")
  kafkaConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long.getClass.getName)
  kafkaConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
    LogTopologyBuilder.getAvroLogTraceSerde(schemaRegistryHost, schemaRegistryPort).getClass.getName)
  kafkaConfig.put("schema.registry.url", s"""http://$schemaRegistryHost:$schemaRegistryPort""")
  kafkaConfig.put(groupIdKey, groupIdValue)
  kafkaConfig.put("broker.id", defaultBrokerIdProp)
  kafkaConfig.put("host.name", kafkaHost)
  kafkaConfig.put("port", kafkaPort)
  kafkaConfig.put("num.partitions", defaultPartitions)
  kafkaConfig.put("auto.create.topics.enable", defaultAutoCreateTopics)
  kafkaConfig.put(applicationIdKey, applicationKey)
  kafkaConfig.put(cacheMaxBytesBufferingKey, "0")
  kafkaConfig.put("offsets.topic.replication.factor", "1")
  kafkaConfig.put("log.dir", logDir)
  kafkaConfig.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "1000")
  kafkaConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  kafkaConfig.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, new GenericTimeStampExtractor().getClass.getName)
  kafkaConfig.put(StreamsConfig.DEFAULT_WINDOWED_KEY_SERDE_INNER_CLASS, Serdes.String.getClass.getName)
  kafkaConfig.put(StreamsConfig.DEFAULT_WINDOWED_VALUE_SERDE_INNER_CLASS, LogTopologyBuilder.getAvroLogTraceSerde(schemaRegistryHost, schemaRegistryPort).getClass.getName)
}