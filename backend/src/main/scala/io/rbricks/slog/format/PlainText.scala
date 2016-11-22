package io.rbricks.slog.format

import io.rbricks.slog._

import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat

import scala.{Console => C}

class PlainText(colorized: Boolean = false) extends Format[String] {
  type Out = String

  private[this] val timeZone = TimeZone.getTimeZone("UTC");
  private[this] val curTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  curTimeFormat.setTimeZone(timeZone);

  private def colored(color: String, s: String) = if (colorized) s"$color$s${C.RESET}" else s

  def apply(
      name: String,
      msg: LogMessage): String = {
    val today = Calendar.getInstance.getTime
    val timeString = colored(C.BOLD, curTimeFormat.format(today))
    val classMethodSegment =
      s"${msg.className.getOrElse("?")}.${msg.method.getOrElse("?")}"
    val fileLineSegment = (for {
      fileName <- msg.fileName
      line <- msg.line
    } yield s" [${fileName}:${line}]").getOrElse("")
    val level = colored(C.CYAN, msg.level.toString)
    val nameMsg = colored(C.YELLOW, name)
    val stackTrace = msg.cause.map { ex =>
      val sw = new java.io.StringWriter()
      val pw = new java.io.PrintWriter(sw)
      ex.printStackTrace(pw)
      "\n\t" + sw.getBuffer
    }.getOrElse("")
    val mdc = msg.mdc.map(m => "{" + m.toIterator.map { case (k, v) => s"$k: $v" }.mkString(",") + "} ").getOrElse("")
    val message = colored(C.BOLD, msg.message.toString)
    s"[$level] [$timeString] [$nameMsg]${fileLineSegment} ${message} $mdc[$classMethodSegment]$stackTrace"
  }
}

object PlainText {
  def apply(colorized: Boolean = false) = new PlainText(colorized)
}
