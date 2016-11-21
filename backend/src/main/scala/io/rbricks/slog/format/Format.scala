package io.rbricks.slog

package object format {
  type Format[Out] = Function2[String, LogMessage, Out]
}

