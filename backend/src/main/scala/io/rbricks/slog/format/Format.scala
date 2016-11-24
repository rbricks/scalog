package io.rbricks.scalog

package object format {
  type Format[Out] = Function2[String, LogMessage, Out]
}

