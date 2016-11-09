package io.rbricks.nozzle.logging

trait Transport {
  def write(
      name: String,
      logMessage: LogMessage): Unit
}
