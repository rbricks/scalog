# ![rbricks scalog](https://raw.githubusercontent.com/rbricks/rbricks.github.io/master/logo/scalog.png)

A small (<500 loc), scala-oriented backend for slf4j.

Part of [rbricks](http://rbricks.io), a collection of composable, small-footprint libraries for scala.

[![Build Status](https://travis-ci.org/rbricks/scalog.svg?branch=master)](https://travis-ci.org/rbricks/scalog)

## Features

1. Extremely simple set-up.

  * In the application entry point, from typesafe config:

    ```scala
    object Main extends App {
      import com.typesafe.config.ConfigFactory
      import io.rbricks.scalog.LoggingBackend
      LoggingBackend.consoleFromConfig(
        ConfigFactory.load().getConfig("logging"))
      // ...
    }
    ```

  * or, in the application entry point, programmatically:
    ```scala
    object Main extends App {
      import io.rbricks.scalog._
      LoggingBackend.console(
        "io.finch" -> Level.Info,
        "com.example.thisapp" -> Level.Debug)
      // ...
    }
    ```

[MIT License](LICENSE.txt)
