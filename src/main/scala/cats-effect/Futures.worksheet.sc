import scala.util.Random
import scala.concurrent.Future
import cats.effect.*
import cats.effect.unsafe.implicits.*
import concurrent.duration.*
import concurrent.ExecutionContext
import concurrent.ExecutionContext.global

given ExecutionContext = global

def printThread() = println(Thread.currentThread().getName())

val io = IO.fromFuture(IO(Future({
  Thread.sleep(2000)
  print(Random.nextInt)
  printThread()
})))

io.unsafeRunSync()
