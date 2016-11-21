package bench

import org.openjdk.jmh.annotations.{Benchmark, State, Scope}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

@State(Scope.Benchmark)
class FutureCascade {

  def futures(): Future[Int] = (1 to 100).foldLeft(Future.successful(1)) { (f, _) =>
    f.flatMap { x => Future { x + 1 } }
  }

  @Benchmark
  def hundredFlatMaps(): Unit = {
    Await.result(
      futures(),
      1 second)
  }

}
