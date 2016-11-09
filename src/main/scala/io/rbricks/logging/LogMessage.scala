package io.rbricks.nozzle.logging

case class LogMessage(
  level: Level,
  message: Any,
  className: Option[String],
  method: Option[String],
  fileName: Option[String],
  line: Option[Int],
  cause: Option[Throwable])
