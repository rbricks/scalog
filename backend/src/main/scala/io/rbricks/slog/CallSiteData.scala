package io.rbricks.scalog

object CallSiteData {
  import scala.collection.JavaConversions._
  def extract(t: Throwable, invokingClass: String): List[StackTraceElement] = {
    t.getStackTrace().view.dropWhile { frame => 
      val className = frame.getClassName();
      className == invokingClass ||
      className == "org.apache.log4j.Category" ||
      className.startsWith("org.slf4j.Logger") ||
      className.startsWith("io.rbricks.scalog")
    }.toList
  }
}

