package io.rbricks.slog

import org.slf4j.helpers.MessageFormatter

trait BasicLogging

private[slog] class BasicLoggingImpl(enabledLevels: Seq[(String, Level)], private val showDisabledLoggers: Boolean, private val transports: Seq[Transport]) extends BasicLogging {

  val enabledLevelsTrie = PackageTrie(enabledLevels)

  private def writeToTransports(name: String, logMessage: LogMessage) = transports.foreach(_.write(name, logMessage))
      
  private val loggerNames = scala.collection.mutable.HashSet[String]()

  @inline
  private[this] def loggersEnabled(name: String): Map[Level, Boolean] = {
    val enabled = enabledLevelsTrie.getAllOnPath(name).lastOption.getOrElse(Disabled)
    if (showDisabledLoggers && enabled == Disabled) {
      if (!loggerNames.contains(name)) {
        writeToTransports("io.rbricks.slog.BasicLogging", LogMessage(
          Level.Info,
          s"Logger with name ${ "\"" + name + "\"" } available and disabled",
          className = Some("io.rbricks.slog.BasicLogging"), method = None, fileName = None, line = None, cause = None))
      }
      loggerNames += name
    }
    Map[Level, Boolean](
      Level.Debug -> (enabled.value <= Level.Debug.value),
      Level.Info  -> (enabled.value <= Level.Info.value ),
      Level.Warn  -> (enabled.value <= Level.Warn.value ),
      Level.Error -> (enabled.value <= Level.Error.value)
    )
  }

  org.slf4j.impl.SimpleLoggerFactory.setLoggerFactoryInterface(new org.slf4j.impl.LoggerFactoryInterface {
    def getNewLogger(name: String): org.slf4j.Logger = {
      val enabled = loggersEnabled(name)
      new Logger(name, enabled, writeToTransports)
    }
  })

}

private[slog] class Logger(name: String, enabled: Map[Level, Boolean], writeToTransports: (String, LogMessage) => Unit) extends org.slf4j.helpers.MarkerIgnoringBase {

  def write(name: String, level: Level, message: String, cause: Option[Throwable]): Unit = {
    import scala.collection.JavaConversions._
    if (enabled(level)) {
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

private[slog] object Logger {
  final val fullyQualifiedClassName: String = io.rbricks.slog.Logger.getClass.getName();
}

object BasicLogging {
  def apply(showDisabledLoggers: Boolean)(enabledLevels: (String, Level)*): BasicLogging = {
    val transports = Seq(new transport.Console(colorized = true))
    apply(transports, showDisabledLoggers)(enabledLevels: _*)
  }

  private[slog] def apply(transports: Seq[Transport], showDisabledLoggers: Boolean = false)(enabledLevels: (String, Level)*): BasicLogging = {
    new BasicLoggingImpl(enabledLevels, showDisabledLoggers, transports)
  }

  def fromConfig(showDisabledLoggers: Boolean)(config: com.typesafe.config.Config): BasicLogging = {
    val enabledLevels = typesafeconfig.enabledLevelsFromConfig(config)
    apply(showDisabledLoggers)(enabledLevels: _*)
  }
}
