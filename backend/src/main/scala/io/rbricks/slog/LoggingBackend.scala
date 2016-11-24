package io.rbricks.scalog

import io.rbricks.scalog.transport.Transport

/**
 * Public interface for the logging backend.
 */
trait Backend {
  /**
   * Interrupts the logging threads and makes an effort to flush any remaining in-flight messages.
   */
  def cease(): Unit
}

/**
 * Holds a transport and the logger levels enabled for that transport.
 */
private[scalog] case class LoggingTransport(transport: Transport, levelsEnabled: Seq[(String, Level)])

/**
 * Implementation of the slf4j backend.
 * Registers itself as the slf4j logger factory on instantiation.
 */
private[scalog] class LoggingBackend(
  loggingTransports: Seq[LoggingTransport]
) extends Backend {

  val queue = new java.util.concurrent.ArrayBlockingQueue[(Transport, String, LogMessage)](1000)

  private def flush(): Unit = {
    queue.synchronized {
      try {
        for ((t, name, message) <- Iterator
          .continually(queue.poll(0, java.util.concurrent.TimeUnit.SECONDS))
          .takeWhile(_ != null)) {

          t.write(name, message)
        }
      } catch { // defensive catch-em-all
        case _: Exception => ()
      }
    }
  }

  java.lang.Runtime.getRuntime().addShutdownHook(new Thread(new Runnable {
    def run() {
      flush()
    }
  }))

  val thread = new Thread(new Runnable {
    def run() {
      try {
        while (true) {
          val (t, name, message) = queue.take()
          t.write(name, message)
        }
      } catch {
        case ex: InterruptedException =>
          flush()
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

  val scalogDisabledTransportLevels = transports.map { case (t, le) =>
    t -> le.getAllOnPath("io.rbricks.scalog.disabled").lastOption.getOrElse(Disabled)
  }

  @inline
  private[this] def logLoggerIsDisable(name: String): Unit = {
    if (!loggerNames.contains(name)) {
      for ((t, lvl) <- scalogDisabledTransportLevels) {
        if (Level.Info.value >= lvl.value) {
          try {
            queue.add((t, "io.rbricks.scalog.disabled", LogMessage(
              java.time.Instant.now(),
              Level.Info,
              s"Logger with name ${ "\"" + name + "\"" } available and disabled",
              className = Some("io.rbricks.scalog.LoggingBackend"),
              method = None, fileName = None, line = None, cause = None, mdc = None)))
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
      // NOTE: the following is on the critical path for creating a new Logger instance
      val transportLevels = transports.map { case (t, le) => t -> le.getAllOnPath(name).lastOption.getOrElse(Disabled) }

      def writeToTransports(name: String, logMessage: LogMessage) = {
        // NOTE: the following is on the critical path for writing a log message (in the caller's thread)
        for ((t, lvl) <- transportLevels) {
          if (logMessage.level.value >= lvl.value) {
            queue.add((t, name, logMessage))
          }
        }
      }

      val le = transportLevels.map { case (_, lvl) => lvl }.min
      if (le == Disabled) {
        logLoggerIsDisable(name)
      }

      new Logger(name, le, writeToTransports)
    }
  })

}


// TODO: describe level propagation to loggers
/**
 * Instantiate logging backends.
 */
object LoggingBackend {
  /**
   * Backend that outputs to stdout, colorizing if the console is a tty.
   * @enabledLevels: which levels are enabled for each logger name
   */
  def console(enabledLevels: (String, Level)*): Backend = {
    val transports = Seq(
      LoggingTransport(
        new transport.PrintStream(format.PlainText(colorized = (System.console() != null)), System.out), enabledLevels)
    )
    new LoggingBackend(transports)
  }

  /**
   * Backend that outputs to the provided transport.
   * @enabledLevels: which levels are enabled for each logger name
   */
  def singleTransport(transport: Transport, enabledLevels: (String, Level)*): Backend = {
    new LoggingBackend(
      Seq(LoggingTransport(transport, enabledLevels)))
  }

  /**
   * Backend that outputs to stdout, colorizing if the console is a tty.
   * Logging level configuration is obtained by a typesafe-config Config.
   *
   * Example:
   * The following reads the config from the "logging" key.
   * {{{
   * LoggingBackend.consoleFromConfig(
   *   ConfigFactory.load().getConfig("logging"))
   * }}}
   */
  def consoleFromConfig(config: com.typesafe.config.Config): Backend = {
    val enabledLevels = typesafeconfig.enabledLevelsFromConfig(config)
    console(enabledLevels: _*)
  }

  /**
   * Backend for testing, outputs to console all messages with the provided level, or higher.
   */
  def testing(level: Level = Level.Info): Backend =
    LoggingBackend.console("" -> level)
}
