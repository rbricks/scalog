package io.rbricks.scalog.typesafeconfig

case class InvalidConfig(key: String, v: String) extends Exception(
  s"Invalid value ${ "\"" + v + "\"" } for key ${ "\"" + key + "\"" }")
