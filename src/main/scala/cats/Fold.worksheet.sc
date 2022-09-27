import cats.implicits.*
import cats.effect.implicits.*
import cats.effect.IO
import cats.effect.unsafe.implicits.global

val x = Map("hello" -> 1, "world" -> 2)

x.foldLeft(0)((acc, count) => acc + count._2)
