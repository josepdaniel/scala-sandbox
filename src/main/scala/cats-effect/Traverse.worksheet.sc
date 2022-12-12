import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.implicits.*
import cats.implicits.*
import scala.annotation.tailrec

object TraverseUntil {
  def traverseUntil[A, B, E](
      items: List[A]
  )(f: A => IO[Either[E, B]]): IO[Either[E, List[B]]] = {

    def go(i: List[A])(acc: List[B]): IO[Either[E, List[B]]] = {
      i match {
        case Nil => IO.pure(Right(acc))
        case head :: tail =>
          f(head).flatMap {
            case Left(err) => IO.pure(Left(err))
            case Right(b)  => go(tail)(acc.appended(b))
          }
      }
    }
    go(items)(List.empty)
  }
}

val y = TraverseUntil
  .traverseUntil(List(1, 2, 3))(i =>
    if i > 2 then IO.pure(Left("you fucked it"))
    else IO.pure(Right(i))
  )
  .unsafeRunSync()
