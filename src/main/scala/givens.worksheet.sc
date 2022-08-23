case class Box[T](val x: T)

trait Functor[F[_]] {
  def map[A, B](f: F[A])(func: A => B): F[B]
}

given Functor[Box] with {
  def map[A, B](f: Box[A])(func: A => B): Box[B] = {
    Box(func(f.x))
  }
}

def doSomethingWithABox[A, B](
    box: Box[A]
)(func: A => B)(using f: Functor[Box]) = {
  f.map(box)(func)
}

val box = Box(3)
doSomethingWithABox(box)(x => x + 1)
