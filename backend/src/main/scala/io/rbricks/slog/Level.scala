package io.rbricks.scalog

sealed abstract trait Level { protected[scalog] val value: Int }

object Level {
  case object Trace extends Level { protected[scalog] val value = 1 }
  case object Debug extends Level { protected[scalog] val value = 2 }
  case object Info  extends Level { protected[scalog] val value = 3 }
  case object Warn  extends Level { protected[scalog] val value = 4 }
  case object Error extends Level { protected[scalog] val value = 5 }

  implicit object `Ordering for Level` extends Ordering[Level] {
    def compare(a: Level, b: Level) = a.value - b.value
  }
}

case object Disabled extends Level { protected[scalog] val value = 6 }
