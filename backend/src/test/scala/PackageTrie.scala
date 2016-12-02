package io.rbricks.scalog

import utest._

object PackageTrieTests extends TestSuite {
  val tests = this {
    'Trie {
      val trie = PackageTrie(Seq(
        "a" -> "a",
        "a.b" -> "b",
        "a.c" -> "c",
        "a.c.d.e" -> "e"
      ))

      'get {
        * - assert(trie.get("a") == Some("a"))
        * - assert(trie.get("") == None)
        * - assert(trie.get("a.b") == Some("b"))
        * - assert(trie.get("a.c") == Some("c"))
        * - assert(trie.get("a.f") == None)
        * - assert(trie.get("a.c.d") == None)
        * - assert(trie.get("a.c.d.e") == Some("e"))
        * - assert(trie.get("b") == None)
      }

      'getAllOnPath {
        * - assert(trie.getAllOnPath("a") == Seq("a"))
        * - assert(trie.getAllOnPath("a.c") == Seq("a", "c"))
        * - assert(trie.getAllOnPath("a.c.d.e") == Seq("a", "c", "e"))
      }
    }

    'TrieWithEmpty {
      val trie = PackageTrie(Seq(
        "" -> "a"
      ))

      'getAllOnPath {
        * - assert(trie.getAllOnPath("b.c.d") == Seq("a"))
      }
    }
  }
}
