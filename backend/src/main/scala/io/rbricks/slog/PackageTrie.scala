package io.rbricks.scalog

/**
 * A trie-like structure that uses package name segments as key segments.
 * I.e. "com.example.package" is stored under ["com" -> "example" -> "package"]
 */
case class PackageTrie[V](v: Option[V], nodes: Map[String, PackageTrie[V]]) {
  def get(k: String): Option[V] = get(k.split("\\."))

  private def get(xs: Seq[String]): Option[V] = {
    xs match {
      case Seq(k, ks @ _*) => nodes.get(k).flatMap(_.get(ks))
      case Seq() => v
    }
  }

  def getAllOnPath(k: String): Seq[V] = getAllOnPath(k.split("\\."))

  private def getAllOnPath(xs: Seq[String]): Seq[V] = {
    v.toSeq ++ (xs match {
      case Seq(k, ks @ _*) => nodes.get(k).toSeq.flatMap(_.getAllOnPath(ks))
      case Seq() => Seq()
    })
  }
}

object PackageTrie {
  def apply[V](items: Seq[(String, V)]) = stepDown(items.map { case (k, v) => (k.split("\\.").toSeq -> v) })

  private def stepDown[V](items: Seq[(Seq[String], V)]): PackageTrie[V] = {
    val groups = items.map { case (xs, v) =>
      xs match {
        case Seq(head, rest @ _*) => (Some(head), rest, v)
        case Seq() => (None, Seq(), v)
      }
    }.groupBy { case (h, _, _) => h }
    val nodes = groups.collect { case (Some(pfx), vs) =>
      (pfx, stepDown(vs.map { case (_, xs, v) => (xs, v) }))
    }
    val head = groups.collectFirst { case (None, vs) =>
      vs.headOption.map { case (_, _, v) => v }
    }.flatten

    PackageTrie(head, nodes)
  }

}


