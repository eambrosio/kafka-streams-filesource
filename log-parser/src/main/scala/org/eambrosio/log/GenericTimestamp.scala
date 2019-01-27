package org.eambrosio.log

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.TimestampExtractor

class GenericTimeStampExtractor extends TimestampExtractor {

  override def extract(record: ConsumerRecord[AnyRef, AnyRef], l: Long): Long = {
    val logTrace = record.value().asInstanceOf[String]
    logTrace.split(" ")(0).toLong
  }
}
