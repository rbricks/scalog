package io.rbricks.slog.transport

import io.rbricks.slog._

import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat

import scala.{Console => C}

class PrintStream(fmt: format.Format[String], printStream: java.io.PrintStream) extends Transport {
  def write(
      name: String,
      msg: LogMessage): Unit = {

    printStream.println(fmt(name, msg))
  }
}
