package io.rbricks.slog.mdc

import org.slf4j.spi.MDCAdapter

import scala.collection.JavaConverters._

class SlogMDCAdapter extends MDCAdapter {

  val threadLocal = new ThreadLocal[Map[String, String]]();

  def put(key: String, value: String): Unit = {
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }

    threadLocal.set(
      Option(threadLocal.get()).getOrElse(Map()) + (key -> value))
  }

  def remove(key: String): Unit = {
    if (key != null) {
      threadLocal.set(
        Option(threadLocal.get()).getOrElse(Map()) - key)
    }
  }

  def clear(): Unit = {
    threadLocal.set(Map())
  }

  def get(key: String): String = {
    Option(threadLocal.get()).getOrElse(Map()).get(key).getOrElse(null)
  }

  def getKeys(): java.util.Set[String] = {
    Option(threadLocal.get())
      .map(_.keys.toSet.asJava).getOrElse(null)
  }

  def getCopyOfContextMap(): java.util.Map[String, String] = {
    val tl = threadLocal.get()
    if (tl == null) {
      null
    } else {
      tl.asJava
    }
  }

  def setContextMap(contextMap: java.util.Map[String, String]): Unit = {
    threadLocal.set(contextMap.asScala.toMap)
  }
}
