package io.rbricks.scalog

import com.typesafe.config.ConfigFactory

import utest._

object EnabledFromConfigTests extends TestSuite {
  val tests = this {
    'fromConfig {
      val config = ConfigFactory.parseString("""
        logging {
          com.example {
            'level = warn
            something = info
            inner {
              else = debug
            }
            other {
              'level = error
            }
          }
        }
      """)
      val enabledLevels = PackageTrie(
        typesafeconfig.enabledLevelsFromConfig(config.getConfig("logging")))
      assert(enabledLevels.getAllOnPath("com.example.something").lastOption == Some(Level.Info))
      assert(enabledLevels.getAllOnPath("com.example").lastOption == Some(Level.Warn))
      assert(enabledLevels.getAllOnPath("com.example.inner").lastOption == Some(Level.Warn))
      assert(enabledLevels.getAllOnPath("com.example.inner.else").lastOption == Some(Level.Debug))
      assert(enabledLevels.getAllOnPath("com.example.other.else").lastOption == Some(Level.Error))
    }
  }
}
