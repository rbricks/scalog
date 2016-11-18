package io.rbricks.slog

sealed abstract trait Level { protected[slog] val value: Int }

object Level {
  case object Trace extends Level { protected[slog] val value = 1 }
  case object Debug extends Level { protected[slog] val value = 2 }
  case object Info  extends Level { protected[slog] val value = 3 }
  case object Warn  extends Level { protected[slog] val value = 4 }
  case object Error extends Level { protected[slog] val value = 5 }

  implicit object `Ordering for Level` extends Ordering[Level] {
    def compare(a: Level, b: Level) = a.value - b.value
  }
}

case object Disabled extends Level { protected[slog] val value = 6 }
