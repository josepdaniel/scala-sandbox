// 'given' and 'using' is the scala3 way of putting things in implicit scope
// It can be used to define type classes (e.g. functor)

case class Box[T](val x: T)

trait Functor[F[_]] {
  def map[A, B](f: F[A])(func: A => B): F[B]
}

// Define a functor instance for box
given Functor[Box] with {
  def map[A, B](f: Box[A])(func: A => B): Box[B] = {
    Box(func(f.x))
  }
}

// We can define extension methods for any type that has a functor instance
extension [F[_], A, B](f: F[A])(using functor: Functor[F]) {
  def map(func: A => B) = functor.map(f)(func)
}

val box = Box(3)
box.map(x => x + 2)

val greenBox = Box("green")
greenBox.map(_ => "red")
