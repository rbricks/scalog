package io.rbricks.slog

import io.rbricks.slog._

import scala.collection.JavaConversions._

package object typesafeconfig {

  case class InvalidConfig(key: String, v: String) extends Exception(
    s"Invalid value ${ "\"" + v + "\"" } for key ${ "\"" + key + "\"" }")

  private val stringLevel = Map(
    "trace" -> Level.Trace,
    "debug" -> Level.Debug,
    "info"  -> Level.Info ,
    "warn"  -> Level.Warn ,
    "error" -> Level.Error
  )

  private object PackageLevelKey {
    def unapply(s: String): Option[String] = {
      val key = "(.*)(\\.\"'level\")".r
      s match {
        case key(k, _) => Some(k)
        case otherwise => Some(otherwise)
      }
    }
  }
    
  def enabledLevelsFromConfig(config: com.typesafe.config.Config): Seq[(String, Level)] = {
    val vals = config.entrySet.toSeq.map(x => (x.getKey -> x.getValue))
    vals.map { case (PackageLevelKey(k), v) =>
      val lvl = (v.unwrapped match {
        case s: String => Some(s)
        case _ => None
      }).flatMap(stringLevel.get)
      .getOrElse {
        throw InvalidConfig(k, v.unwrapped.toString)
      }
      k.replace("\"", "") -> lvl
    }
  }
}
