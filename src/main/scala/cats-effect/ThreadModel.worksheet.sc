import scala.concurrent.ExecutionContext
import cats.effect.unsafe.IORuntime
import cats.effect.*
import cats.effect.unsafe.implicits.*
import concurrent.duration.*

val printThread = () => println(Thread.currentThread().getName())

printThread()

// Most stuff runs in the 'compute' pool
IO(printThread()).unsafeRunSync()

// Blocking IO can run in a 'blocking' pool
IO.blocking({
  Thread.sleep(300)
  printThread()
}).unsafeRunSync()

// We can shift execution onto a threadpool of our choice
Async[IO].evalOn(IO(printThread()), ExecutionContext.global).unsafeRunSync()

// evalOn will shift any continuations back onto the previous execution context

val x = Async[IO].executionContext.flatMap { compute =>
  for {
    _ <- Async[IO].evalOn(
      for {
        _ <- Async[IO].evalOn(IO(printThread()), compute) // io-compute-pool
        _ <- IO(printThread()) // scala-execution-context-global-pool
        _ <- IO.blocking(printThread()) // io-blocking-pool
        _ <- IO(printThread()) // scala-execution-context-global-pool
      } yield (),
      ExecutionContext.global
    )
    _ <- IO(printThread()) // io-compute-pool
  } yield ()
}
x.unsafeRunSync()
