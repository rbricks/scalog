package io.rbricks.slog

case class LogMessage(
  level: Level,
  message: Any,
  className: Option[String],
  method: Option[String],
  fileName: Option[String],
  line: Option[Int],
  cause: Option[Throwable],
  mdc: Option[Map[String, String]])
