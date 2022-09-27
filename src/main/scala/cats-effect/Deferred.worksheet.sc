import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.implicits.*
import concurrent.duration.*

def complete[A](d: Deferred[IO, A], v: A) = {
  IO.blocking(
    IO(Thread.sleep(2000)) *> IO.println(Thread.currentThread().getName()) *> d
      .complete(v)
  ).flatten
}

val y = for {
  x <- Deferred[IO, Int]
  out <- x.get
    .timeout(3.seconds)
    .start // get is a 'semantically' blocking op (the fiber will wait). `start` puts it in the background
  _ <- complete(x, 9).start
  res <- out.join
} yield res

val z = y.unsafeRunSync()
