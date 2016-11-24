package io.rbricks.scalog.contextpropagation

import scala.concurrent.{ Future }
import scala.concurrent.ExecutionContext.Implicits.global

import utest._

import org.slf4j.MDC

object ContextPropagationTests extends TestSuite {
  def doSth: Future[Int] = {
    Future.successful(()).flatMap { _ =>
      Future {
        MDC.get("id").toInt
      }
    }
  }
  val tests = this {
    'futures {
      val futures: Seq[Future[Unit]] = (for (i <- 1 to 10) yield {
        MDC.put("id", i.toString)
        //println(s"?? -> ${MDC.get("id")}")
        Future {
          //println(i)
          i
        }.flatMap { fi =>
          doSth.map(x => (fi, x))
        }
      }).map { f =>
        f.map { case (fi, x) =>
          assert(fi == x)
        }
      }
      Future.successful(()).flatMap { _ =>
        Future.sequence(futures).map(_ => assert(true))
      }
    }
  }
}
