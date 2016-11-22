package io.rbricks.slog.format

import io.rbricks.slog._

import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat

import scala.{Console => C}

import helpers._

class PlainText(colorized: Boolean = false) extends Format[String] {
  private def colored(color: String)(s: String) = if (colorized) s"$color$s${C.RESET}" else s

  def apply(
      name: String,
      msg: LogMessage): String = {
    val classMethodSegment =
      s"${msg.className.getOrElse("?")}.${msg.method.getOrElse("?")}"
    val fileLineSegment = for {
      fileName <- msg.fileName
      line <- msg.line
    } yield s"${fileName}:${line}"
    val levelColor = msg.level match {
      case Level.Error => C.RED + C.BOLD
      case Level.Warn => C.RED
      case Level.Info => C.BLUE
      case _ => C.WHITE
    }
    val level = colored(levelColor)(msg.level.toString.toLowerCase)
    val nameMsg = colored(C.YELLOW)(name)
    val stackTrace = msg.cause.map { ex =>
      val sw = new java.io.StringWriter()
      val pw = new java.io.PrintWriter(sw)
      ex.printStackTrace(pw)
      "\n\t" + sw.getBuffer
    }
    val mdc = msg.mdc.map(m => "{" + m.toIterator.map { case (k, v) => s"$k: $v" }.mkString(",") + "} ")
    val message = colored(C.BOLD)(msg.message.toString)
    so"${msg.time} [$nameMsg] [$level] $message $mdc [$fileLineSegment] [$classMethodSegment]$stackTrace" 
  }
}

object PlainText {
  def apply(colorized: Boolean = false) = new PlainText(colorized)
}
