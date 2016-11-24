package io.rbricks.scalog.transport

import io.rbricks.scalog.LogMessage

trait Transport {
  def write(
      name: String,
      logMessage: LogMessage): Unit
}
