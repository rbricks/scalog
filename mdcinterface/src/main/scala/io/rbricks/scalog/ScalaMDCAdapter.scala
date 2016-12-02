package io.rbricks.scalog

/**
 * scalog-optimized MDC adapter interface
 */
trait ScalaMDCAdapter {
  def propertyMap: Option[Map[String, String]]
}
