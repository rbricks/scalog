name := "logging"
organization := "io.rbricks"

scalaVersion := "2.11.8"
version := "0.1-SNAPSHOT"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8")

libraryDependencies ++= Seq(
  "org.slf4j"      %  "slf4j-api"              % "1.7.7"
)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
