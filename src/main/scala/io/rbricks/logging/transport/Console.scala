package io.rbricks.nozzle.logging.transport

import io.rbricks.nozzle.logging._

import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat

import scala.{Console => C}

class Console(colorized: Boolean = false) extends Transport {
  private[this] val timeZone = TimeZone.getTimeZone("UTC");
  private[this] val curTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  curTimeFormat.setTimeZone(timeZone);

  def write(
      name: String,
      msg: LogMessage): Unit = {
    val today = Calendar.getInstance.getTime
    val timeString = colored(C.BOLD, curTimeFormat.format(today))
    val classMethodSegment =
      s" [${msg.className.getOrElse("?")}.${msg.method.getOrElse("?")}]"
    val fileLineSegment = (for {
      fileName <- msg.fileName
      line <- msg.line
    } yield s" [${fileName}:${line}]").getOrElse("")
    val level = colored(C.CYAN, msg.level.toString)
    val nameMsg = colored(C.YELLOW, name)
    println(s"[$level] [$timeString] [$nameMsg]${classMethodSegment}${fileLineSegment} ${msg.message}")
    msg.cause map (_.printStackTrace)
  }

  private def colored(color: String, s: String) = if (colorized) s"$color$s${C.RESET}" else s

}
