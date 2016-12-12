## Context propagation

The `scalog-contextpropagation` module contains _aspectj_ aspect to instrument scala's `Future` to propagate the MDC context through future combinators, so that you can keep track of a request handled asynchronously.

*WARNING* This module is experimental, and instruments critical bytecode from the scala standard library which may lead to unexpected behaviour or instability. It can be easily removed if it causes trouble, but use it in producion at your own risk. Help with the instrumentation code and testing is greatly appreciated. Thanks!

To use it, add the dependency to `build.sbt`,

```
libraryDependencies += "io.rbricks" %% "scalog-contextpropagation" % "0.2.0"
```

Additionally, in development, you need the _sbt-aspectj_ plugin. In, `project/plugins.sbt` add

```
addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.10.1")
```

and, in `build.sbt`:

```
aspectjSettings
javaOptions in run <++= com.typesafe.sbt.SbtAspectj.AspectjKeys.weaverOptions in Aspectj
```

For production, when the app is run independently from `sbt`, make sure that the jvm is invoked with an up-to-date _aspectj_ weaver agent:

```
java -javaagent:path/to/aspectj-weaver.jar app.jar
```
