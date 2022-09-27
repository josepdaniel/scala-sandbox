import cats.implicits.*
import cats.effect.implicits.*
import cats.effect.IO
import cats.effect.unsafe.implicits.global

val x: Option[Int] = None
val y: Option[Int] = Some(3)

x.traverse(IO.println(_))
y.traverse(IO.println(_))
