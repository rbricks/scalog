package io.rbricks.slog.mdc

import utest._

import org.slf4j.MDC

object MDCTests extends TestSuite {
  val tests = this {
    'putget {
      MDC.put("a", "b")
      assert(MDC.get("a") == "b")
    }
  }
}
