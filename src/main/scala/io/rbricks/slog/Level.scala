package io.rbricks.slog

sealed abstract trait Level { protected[slog] val value: Int }

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
