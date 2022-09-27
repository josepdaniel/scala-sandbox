import cats.effect.*
import cats.effect.unsafe.implicits.*
import concurrent.duration.*

val throwSomething = IO.sleep(1.second) *> IO.println(
  Thread.currentThread().getName()
) *> IO.raiseError(Throwable("Oops"))
val doSomething = IO.println(Thread.currentThread().getName() + " - Hello")

val x = for {
  fiber2 <- doSomething.start
  fiber1 <- throwSomething.handleError(_ => println("oops")).start
  out1 <- fiber1.join
  out2 <- fiber2.join
} yield ()

x.start.unsafeRunSync()
