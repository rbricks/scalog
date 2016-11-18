package io.rbricks.slog

import org.slf4j.helpers.MessageFormatter

trait TestLogging

private[slog] class TestLoggingImpl(private val level: Level) extends TestLogging {

  private val transports = Seq(new transport.Console(colorized = true))

  private def writeToTransports(name: String, logMessage: LogMessage) = transports.foreach(_.write(name, logMessage))
      
  // private val loggingLogger = Logger("io.rbricks.slog.TestLogging", transports, { case _ => true })

  private val loggerNames = scala.collection.mutable.HashSet[String]()

  @inline
  private[this] def loggersEnabled(name: String): Level = {
    level
  }

  org.slf4j.impl.SimpleLoggerFactory.setLoggerFactoryInterface(new org.slf4j.impl.LoggerFactoryInterface {
    def getNewLogger(name: String): org.slf4j.Logger = {
      val enabled = loggersEnabled(name)
      new Logger(name, enabled, writeToTransports)
    }
  })

}


object TestLogging {
  def apply(level: Level = Level.Info): TestLogging = new TestLoggingImpl(level)
}
