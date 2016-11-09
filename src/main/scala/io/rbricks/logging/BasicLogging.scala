package io.rbricks.nozzle.logging

import org.slf4j.helpers.MessageFormatter

sealed abstract trait Level { protected[logging] val value: Int }

object Level {
  case object Trace extends Level { val value = 1 }
  case object Debug extends Level { val value = 2 }
  case object Info  extends Level { val value = 3 }
  case object Warn  extends Level { val value = 4 }
  case object Error extends Level { val value = 5 }

  implicit object `Ordering for Level` extends Ordering[Level] {
    def compare(a: Level, b: Level) = a.value - b.value
  }
}

case object Disabled extends Level { val value = 0 }

trait BasicLogging

private[logging] class BasicLoggingImpl(private val levelsEnabled: PartialFunction[String, Level], showDisabledLoggers: Boolean) extends BasicLogging {

  private val transports = Seq(new transport.Console(colorized = true))

  private def writeToTransports(name: String, logMessage: LogMessage) = transports.foreach(_.write(name, logMessage))
      
  // private val loggingLogger = Logger("io.rbricks.nozzle.logging.BasicLogging", transports, { case _ => true })

  private val loggerNames = scala.collection.mutable.HashSet[String]()

  @inline
  private[this] def loggersEnabled(name: String): Map[Level, Boolean] = {
    val enabled = levelsEnabled.applyOrElse(name, (_: String) => Disabled)
    if (showDisabledLoggers && enabled == Disabled) {
      if (!loggerNames.contains(name)) {
        writeToTransports("io.rbricks.nozzle.logging.BasicLogging", LogMessage(
          Level.Info,
          s"Logger with name ${ "\"" + name + "\"" } available and disabled",
          className = Some("io.rbricks.logging.BasicLogging"), method = None, fileName = None, line = None, cause = None))
      }
      loggerNames += name
    }
    Map[Level, Boolean](
      Level.Debug -> (enabled.value >= Level.Debug.value),
      Level.Info  -> (enabled.value >= Level.Info.value ),
      Level.Warn  -> (enabled.value >= Level.Warn.value ),
      Level.Error -> (enabled.value >= Level.Error.value)
    )
  }

  org.slf4j.impl.SimpleLoggerFactory.setLoggerFactoryInterface(new org.slf4j.impl.LoggerFactoryInterface {
    def getNewLogger(name: String): org.slf4j.Logger = {
      val enabled = loggersEnabled(name)
      new Logger(name, enabled, writeToTransports)
    }
  })

}

private[logging] class Logger(name: String, enabled: Map[Level, Boolean], writeToTransports: (String, LogMessage) => Unit) extends org.slf4j.helpers.MarkerIgnoringBase {

  def write(name: String, level: Level, message: String, cause: Option[Throwable]): Unit = {
    import scala.collection.JavaConversions._
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
    writeToTransports(name, LogMessage(
      level,
      message,
      className = className,
      method = method,
      fileName = file,
      line = line,
      cause = cause))
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
  def isDebugEnabled(): Boolean = enabled(Level.Debug)
  def isErrorEnabled(): Boolean = enabled(Level.Error)
  def isWarnEnabled(): Boolean =  enabled(Level.Warn)
  def isInfoEnabled(): Boolean =  enabled(Level.Info)
  def isTraceEnabled(): Boolean = enabled(Level.Trace)
}

private[logging] object Logger {
  final val fullyQualifiedClassName: String = io.rbricks.nozzle.logging.Logger.getClass.getName();
}

object BasicLogging {
  def apply(showDisabledLoggers: Boolean = false)(levelsEnabled: PartialFunction[String, Level]): BasicLogging = new BasicLoggingImpl(levelsEnabled, showDisabledLoggers)
}
