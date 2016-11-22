package some.example.namespace.name

import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._

import org.slf4j.{ MDC, LoggerFactory }

import scala.concurrent.ExecutionContext.Implicits.global

class Module {
  private val log = LoggerFactory.getLogger(classOf[Module])

  def run(): Unit = {
    log.info("hi")
    log.debug("debug")

    MDC.put("key", "asdf")
    val f = Future {
      log.info(s"infuture ${MDC.get("key")}")
    }
    MDC.clear()
    Await.result(f, 1 second)
  }
}
