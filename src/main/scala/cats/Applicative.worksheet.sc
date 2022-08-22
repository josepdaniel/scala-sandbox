import cats._
import cats.implicits._

// Applicative provides a `pure` method to lift a value into the type-constructor
val listWith3: List[Int] = Applicative[List].pure(3)
// listWith3: List[Int] = List(3)
val optionWith4: Option[Int] = Applicative[Option].pure(4)
// optionWith4: Option[Int] = Some(4)

// Applicative also provides an `ap` method which takes a function inside a context
// and applies it to a value inside the same kind of context
val maybePlus2: Option[Int => Int] = Applicative[Option].pure((v: Int) => v + 2)
// maybePlus2: Option[Int => Int] = Some(<function1>)
val maybe3: Option[Int] = Some(3)
// maybe3: Option[Int] = Some(3)

// ap: (f: F[A=>B])(v: F[A]) => F[B]
val maybe5 = Applicative[Option].ap(maybePlus2)(maybe3)
// maybe5: Option[Int] = Some(5)
