import cats.*

// Semigroup - a typeclass representing things that can be 'combined', e.g. two lists
val x = Semigroup[List[Int]].combine(List(1), List(2))
// x: List[Int] = List(1, 2)

// It's useful for when we don't know ahead of time what we are combining
def combineStuff[F[A], A](x: F[A], y: F[A])(implicit
    s: Semigroup[F[A]]
): F[A] = {
  Semigroup[F[A]].combine(x, y)
}

// The compiler will supply us with an implementation for any F which has an implicit Semigroup in scope
combineStuff(List(1, 2, 3), List(4, 5, 6))
// res0: List[Int] = List(1, 2, 3, 4, 5, 6)
combineStuff(Vector(1, 2, 3), Vector(4, 5, 6))
// res1: Vector[Int] = Vector(1, 2, 3, 4, 5, 6)
combineStuff(Id(1), Id(2))
// res2: Id[Int] = 3
