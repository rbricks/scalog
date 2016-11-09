package io.rbricks.slog

trait Transport {
  def write(
      name: String,
      logMessage: LogMessage): Unit
}
