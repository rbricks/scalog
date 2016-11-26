addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.8.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.10.1")

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.2.17")

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.4")

addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.3")
