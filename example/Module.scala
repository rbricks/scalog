package some.example.namespace.name

import org.slf4j.LoggerFactory

class Module {
  private val log = LoggerFactory.getLogger(classOf[Module])

  def run(): Unit = {
    log.info("hi")
  }
}
