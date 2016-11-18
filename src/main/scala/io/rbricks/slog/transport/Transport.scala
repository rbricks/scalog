package io.rbricks.slog.transport

import io.rbricks.slog.LogMessage

trait Transport {
  def write(
      name: String,
      logMessage: LogMessage): Unit
}
