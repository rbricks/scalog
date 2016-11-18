package some.example.namespace.name

import io.rbricks.slog._
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

object Main extends App {
  val loggingBackend = LoggingBackend.consoleFromConfig(ConfigFactory.load().getConfig("logging"))

  val log = LoggerFactory.getLogger(Main.getClass)

  log.info("starting")
  log.debug("for real")
  log.error("oh!", new Exception())
  new Module().run()
}
