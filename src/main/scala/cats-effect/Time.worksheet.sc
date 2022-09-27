import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.implicits.*

val now = IO.realTime.unsafeRunSync()
