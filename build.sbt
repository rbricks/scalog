import com.typesafe.sbt.SbtAspectj.AspectjKeys.{ compileOnly, weaverOptions, verbose }

lazy val baseSettings = Seq(
  organization := "io.rbricks",
  scalaVersion := "2.12.0",
  crossScalaVersions := Seq("2.11.8", "2.12.0"),
  version := "0.1-SNAPSHOT"
)

lazy val commonSettings = baseSettings ++ Seq(
  scalacOptions := Seq(
    "-unchecked",
    "-deprecation",
    "-encoding",
    "utf8"),
  libraryDependencies ++= Seq(
    "com.lihaoyi"    %% "utest"                  % "0.4.4"       % "test",
    "com.lihaoyi"    %% "pprint"                 % "0.4.4"       % "test"
  ),
  testFrameworks += new TestFramework("utest.runner.Framework")
)

val mdc = (project in file("mdc"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "scalog-mdc",
    libraryDependencies ++= Seq(
      "org.slf4j"      %  "slf4j-api"              % "1.7.21"
    )
  )

val backend = (project in file("backend"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "scalog-backend",
    libraryDependencies ++= Seq(
      "org.slf4j"      %  "slf4j-api"              % "1.7.21",
      "com.typesafe"   %  "config"                 % "1.3.1"       % "provided"
    )
  )
  .dependsOn(mdc % "provided")

val contextpropagation = (project in file("contextpropagation"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(aspectjSettings)
  .settings(
    name := "scalog-contextpropagation",
    libraryDependencies ++= Seq(
      "org.slf4j"      % "slf4j-api"               % "1.7.21"
    ),
    compileOnly in Aspectj := true,
    products in Compile <++= products in Aspectj,
    fork in Test := true,
    javaOptions in Test <++= weaverOptions in Aspectj
  )
  .dependsOn(mdc % "test")

val contextpropagationBenchWoven = (project in file("contextpropagation/bench/woven"))
  .enablePlugins(JmhPlugin)
  .settings(commonSettings)
  .settings(
    aspectjSettings,
    javaOptions in run <++= weaverOptions in Aspectj
  )
  .dependsOn(contextpropagation, mdc)

val contextpropagationBenchVanilla = (project in file("contextpropagation/bench/vanilla"))
  .settings(commonSettings)
  .enablePlugins(JmhPlugin)
  .dependsOn(mdc)

val example = (project in file("example"))
  .settings(noPublishSettings)
  .settings(
    scalaVersion := "2.12.0",
    crossScalaVersions := Seq("2.11.8", "2.12.0"),
    libraryDependencies ++= Seq(
      "org.slf4j"      % "slf4j-api"               % "1.7.21",
      "com.typesafe"   % "config"                  % "1.3.1",
      "org.aspectj"    % "aspectjweaver"           % "1.8.4"
    ),
    packAutoSettings,
    aspectjSettings,
    javaOptions in run <++= weaverOptions in Aspectj,
    fork in run := true,
    packJvmOpts := Map("main" -> Seq("-javaagent:'${PROG_HOME}/lib/aspectjweaver-1.8.4.jar'"))
  )
  .dependsOn(backend, contextpropagation, mdc)

val root = (project in file("."))
  .settings(baseSettings)
  .aggregate(backend, mdc, contextpropagation, example)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  publishMavenStyle := true,
  pomExtra in Global := {
    <url>http://github.com/rbricks/scalog</url>
    <scm>
      <connection>scm:git:github.com/rbricks/scalog.git</connection>
      <developerConnection>scm:git:git@github.com:rbricks/scalog.git</developerConnection>
      <url>github.com/rbricks/scalog</url>
    </scm>
    <developers>
      <developer>
        <id>utaal</id>
        <name>Andrea Lattuada</name>
        <url>http://github.com/utaal</url>
      </developer>
    </developers>
  }
)
