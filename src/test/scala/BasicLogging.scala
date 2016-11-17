package io.rbricks.slog

import org.slf4j.LoggerFactory

import utest._

object BasicLoggingTests extends TestSuite {
  val tests = this {
    "BasicLogging logs endabled levels to transports" - {
      val writes = scala.collection.mutable.ListBuffer.empty[(String, LogMessage)]

      val transport = new Transport {
        def write(name: String, logMessage: LogMessage): Unit = {
          writes += ((name, logMessage))
        }
      }
      
      val basicLogging = BasicLogging(Seq(transport), showDisabledLoggers = true)(
        "com.example.a" -> Level.Info,
        "com.example.a.inner" -> Level.Debug,
        "com.example" -> Level.Error
      )

      val asth = LoggerFactory.getLogger("com.example.a.sth")
      asth.info("something")
      asth.debug("else")

      val innersth = LoggerFactory.getLogger("com.example.a.inner.c")
      innersth.debug("ok")

      val err = LoggerFactory.getLogger("com.example.d")
      err.info("aa")
      err.warn("no")
      err.error("!!")

      val examp = LoggerFactory.getLogger("com.example")
      examp.error("start")

      val other = LoggerFactory.getLogger("com.other")
      other.info("info")
      other.info("info")

      println(writes)
      assertMatch(writes){
        case scala.collection.mutable.ListBuffer(
          ("com.example.a.sth", LogMessage(_, "something", _, _, _, _, _)),
          ("com.example.a.inner.c", LogMessage(_, "ok", _, _, _, _, _)),
          ("com.example.d", LogMessage(_, "!!", _, _, _, _, _)),
          ("com.example", LogMessage(_, "start", _, _, _, _, _)),
          ("io.rbricks.slog.BasicLogging", LogMessage(_, "Logger with name \"com.other\" available and disabled", _, _, _, _, _))
        ) =>
      }
    }
  }
}
