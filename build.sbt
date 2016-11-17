name := "slog-backend"
organization := "io.rbricks"

scalaVersion := "2.11.8"
crossScalaVersions := Seq("2.11.8", "2.12.0")
version := "0.1-SNAPSHOT"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8")

libraryDependencies ++= Seq(
  "org.slf4j"      %  "slf4j-api"              % "1.7.21",
  "com.lihaoyi"    %% "utest"                  % "0.4.3"       % "test"
)

testFrameworks += new TestFramework("utest.runner.Framework")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

publishMavenStyle := true

pomExtra in Global := {
  <url>http://github.com/rbricks/slog</url>
  <scm>
    <connection>scm:git:github.com/rbricks/slog.git</connection>
    <developerConnection>scm:git:git@github.com:rbricks/slog.git</developerConnection>
    <url>github.com/rbricks/slog</url>
  </scm>
  <developers>
    <developer>
      <id>utaal</id>
      <name>Andrea Lattuada</name>
      <url>http://github.com/utaal</url>
    </developer>
  </developers>
}
