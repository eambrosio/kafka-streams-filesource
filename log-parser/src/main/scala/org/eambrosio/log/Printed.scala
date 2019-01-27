package org.eambrosio.log

import org.apache.kafka.streams.kstream.Windowed

trait Printed {

  // Print out records to stdout for debugging purposes
  import org.apache.kafka.streams.kstream.Printed

  val sysoutConnectedFrom = Printed
    .toSysOut[Windowed[String], Long]
    .withLabel("ConnectedFrom")

  val sysoutConnectedTo = Printed
    .toSysOut[Windowed[String], Long]
    .withLabel("ConnectedTo")

}
