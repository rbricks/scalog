package io.rbricks.scalog.format

package object helpers {
  implicit class OptionContext(val sc: StringContext) extends AnyVal {
    def so(args: Any*): String = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      var buf = new StringBuffer(strings.next)
      while(strings.hasNext) {
        buf.append(expressions.next match {
          case Some(x) => x.toString
          case None => ""
          case x => x.toString
        })
        buf.append(strings.next)
      }
      buf.toString
    }
  }
}
