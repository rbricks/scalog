package io.rbricks.slog

import io.rbricks.slog.transport.Transport

case class LoggingTransport(transport: Transport, levelsEnabled: Seq[(String, Level)])

trait Backend {
  def cease(): Unit
}

private[slog] class LoggingBackend(
  loggingTransports: Seq[LoggingTransport]
) extends Backend {

  val queue = new java.util.concurrent.ArrayBlockingQueue[(Transport, String, LogMessage)](1000)

  val thread = new Thread(new Runnable {
    def run() {
      try {
        while (true) {
          val (t, name, message) = queue.take()
          t.write(name, message)
        }
      } catch {
        case ex: InterruptedException =>
          for ((t, name, message) <- Iterator
            .continually(queue.poll(0, java.util.concurrent.TimeUnit.SECONDS))
            .takeWhile(_ != null)) {

            t.write(name, message)
          }
      }
    }
  })

  thread.setDaemon(true)
  thread.start()

  def cease(): Unit = {
    thread.interrupt()
    thread.join()
  }

  val transports = loggingTransports.map { case LoggingTransport(t, le) => (t, PackageTrie(le)) }.toMap

  val loggerNames = scala.collection.mutable.HashSet[String]()

  val slogDisabledTransportLevels = transports.map { case (t, le) =>
    t -> le.getAllOnPath("io.rbricks.slog.disabled").lastOption.getOrElse(Disabled)
  }

  @inline
  private[this] def loggerDisabled(name: String): Unit = {
    if (!loggerNames.contains(name)) {
      for ((t, lvl) <- slogDisabledTransportLevels) {
        if (Level.Info.value >= lvl.value) {
          try {
            queue.add((t, "io.rbricks.slog.disabled", LogMessage(
              Level.Info,
              s"Logger with name ${ "\"" + name + "\"" } available and disabled",
              className = Some("io.rbricks.slog.LoggingBackend"), method = None, fileName = None, line = None, cause = None)))
          } catch {
            case _: IllegalStateException => ()
          }
        }
      }
    }
    loggerNames += name
  }

  org.slf4j.impl.SimpleLoggerFactory.setLoggerFactoryInterface(new org.slf4j.impl.LoggerFactoryInterface {
    def getNewLogger(name: String): org.slf4j.Logger = {
      val transportLevels = transports.map { case (t, le) => t -> le.getAllOnPath(name).lastOption.getOrElse(Disabled) }

      def writeToTransports(name: String, logMessage: LogMessage) = {
        for ((t, lvl) <- transportLevels) {
          if (logMessage.level.value >= lvl.value) {
            queue.add((t, name, logMessage))
          }
        }
      }

      val le = transportLevels.map { case (_, lvl) => lvl }.min
      if (le == Disabled) {
        loggerDisabled(name)
      }

      new Logger(name, le, writeToTransports)
    }
  })

}

object LoggingBackend {
  def console(enabledLevels: (String, Level)*): Backend = {
    val transports = Seq(
      LoggingTransport(
        new transport.PrintStream(format.PlainText(colorized = (System.console() != null)), System.out), enabledLevels)
    )
    new LoggingBackend(transports)
  }

  def singleTransport(transport: Transport, enabledLevels: (String, Level)*): Backend = {
    new LoggingBackend(
      Seq(LoggingTransport(transport, enabledLevels)))
  }

  def consoleFromConfig(config: com.typesafe.config.Config): Backend = {
    val enabledLevels = typesafeconfig.enabledLevelsFromConfig(config)
    console(enabledLevels: _*)
  }

  def testing(level: Level = Level.Info): Backend =
    LoggingBackend.console("" -> level)
}
