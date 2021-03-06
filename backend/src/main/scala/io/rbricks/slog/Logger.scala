package io.rbricks.scalog

import org.slf4j.helpers.MessageFormatter
import org.slf4j.MDC

import scala.collection.JavaConverters._

private[scalog] class Logger(
  name: String,
  enabled: Level,
  writeToTransports: (String, LogMessage) => Unit
) extends org.slf4j.helpers.MarkerIgnoringBase {

  def write(name: String, level: Level, message: String, cause: Option[Throwable]): Unit = {
    import scala.collection.JavaConversions._
    if (level.value >= enabled.value) {
      val callerDataArray = CallSiteData.extract(
        new Throwable(),
        Logger.fullyQualifiedClassName)
      val topFrame = if (callerDataArray != null && callerDataArray.length > 0) {
        Some(callerDataArray(0))
      } else None
      val file = topFrame.map(_.getFileName())
      val line = topFrame.map(x => x.getLineNumber())
      val className = topFrame.map(_.getClassName())
      val method = topFrame.map(_.getMethodName())
      val mdc: Option[Map[String, String]] = Option(MDC.getMDCAdapter()).flatMap {
        case adapter: ScalaMDCAdapter =>
          adapter.propertyMap
        case otherwise => 
          Option(otherwise.getCopyOfContextMap()).map(_.asScala.toMap)
      }
      writeToTransports(name, LogMessage(
        time = java.time.Instant.now(),
        level,
        message,
        className = className,
        method = method,
        fileName = file,
        line = line,
        cause = cause,
        mdc = mdc))
    }
  }

  def debug(msg: String, cause: Throwable): Unit =
    write(name,
      Level.Debug,
      msg,
      cause = Option(cause))
  def debug(msg: String, params: Object*): Unit =
    debug(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def debug(msg: String, param1: Object, param2: Object): Unit =
    debug(MessageFormatter.format(msg, param1, param2).getMessage())
  def debug(msg: String, param: Object): Unit =
    debug(MessageFormatter.format(msg, param).getMessage())
  def debug(msg: String): Unit =
    write(name,
      Level.Debug,
      msg,
      cause = None)
  def error(msg: String, cause: Throwable): Unit =
    write(name,
      Level.Error,
      msg,
      cause = Option(cause))
  def error(msg: String, params: Object*): Unit =
    error(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def error(msg: String, param1: Object, param2: Object): Unit =
    error(MessageFormatter.format(msg, param1, param2).getMessage())
  def error(msg: String, param: Object): Unit =
    error(MessageFormatter.format(msg, param).getMessage())
  def error(msg: String): Unit =
    write(name,
      Level.Error,
      msg,
      cause = None)
  def info(msg: String, cause: Throwable): Unit =
    write(name,
      Level.Info,
      msg,
      cause = Option(cause))
  def info(msg: String, params: Object*): Unit =
    info(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def info(msg: String, param1: Object, param2: Object): Unit =
    info(MessageFormatter.format(msg, param1, param2).getMessage())
  def info(msg: String, param: Object): Unit =
    info(MessageFormatter.format(msg, param).getMessage())
  def info(msg: String): Unit =
    write(name,
      Level.Info,
      msg,
      cause = None)
  def warn(msg: String, cause: Throwable): Unit =
    write(name,
      Level.Warn,
      msg,
      cause = Option(cause))
  def warn(msg: String, params: Object*): Unit =
    warn(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def warn(msg: String, param1: Object, param2: Object): Unit =
    warn(MessageFormatter.format(msg, param1, param2).getMessage())
  def warn(msg: String, param: Object): Unit =
    warn(MessageFormatter.format(msg, param).getMessage())
  def warn(msg: String): Unit =
    write(name,
      Level.Warn,
      msg,
      cause = None)
  def trace(msg: String, cause: Throwable): Unit =
    write(name,
      Level.Trace,
      msg,
      cause = Option(cause))
  def trace(msg: String, params: Object*): Unit =
    trace(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def trace(msg: String, param1: Object, param2: Object): Unit =
    trace(MessageFormatter.format(msg, param1, param2).getMessage())
  def trace(msg: String, param: Object): Unit =
    trace(MessageFormatter.format(msg, param).getMessage())
  def trace(msg: String): Unit =
    write(name,
      Level.Trace,
      msg,
      cause = None)
  def isDebugEnabled(): Boolean = Level.Debug.value >= enabled.value
  def isErrorEnabled(): Boolean = Level.Error.value >= enabled.value
  def isWarnEnabled(): Boolean =  Level.Warn.value  >= enabled.value
  def isInfoEnabled(): Boolean =  Level.Info.value  >= enabled.value
  def isTraceEnabled(): Boolean = Level.Trace.value >= enabled.value
}

private[scalog] object Logger {
  final val fullyQualifiedClassName: String = io.rbricks.scalog.Logger.getClass.getName();
}
