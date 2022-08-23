// A typeclass is a collection of functional programming design patterns. Typeclasses represent abstractions over
// *computations* that can be done on *things*.

// For example, a Functor is an abstraction that represents things that can be *mapped*.

// A List can be mapped. 'map' is the computation, 'list' is the thing.
List(1, 2, 3).map(x => x + 2)

// So can an Option
Option.apply(3).map(x => x + 2)

// Functor is the name we give to 'things that can be mapped'. What if we want to make another type mappable?
case class Box[A](val x: A)

// This fails because it has no 'map' method defined.
// Box(3).map(x => x + 1)

// In OOP, we might make Box inherit from some 'abstract mappable' parent type. But this gets tedious.
// What if we also want to make it flat-mappable? Do we then make Box inherit from flat-mappable, and every other
// base class we want to provide functionality from? Or we could wrap every type we might want to map on
// inside a mappable wrapper class that does the implementation? In FP, we prefer to go with 'ad-hoc' polymorphism - decouple
// the definition of `map` from the types that are mappable.

trait Mappable[F[_]] {
  def map[A, B](f: F[A])(func: A => B): F[B]
}

// Now we can make any type mappable by implementing a Mappable instance for it and chucking it in implicit scope
implicit object MappableForBox extends Mappable[Box] {
  def map[A, B](f: Box[A])(func: A => B): Box[B] = {
    Box(func(f.x))
  }
}

// Summon an implicit instance of Mappable[Box] and use it
val mappableForBox = implicitly[Mappable[Box]]
mappableForBox.map(Box(3))(x => x + 4)

// We can even stick a nice 'map' method on any object that has a Mappable instance defined
implicit class MappableSyntax[F[_], A](x: F[A])(implicit
    mappable: Mappable[F]
) {

  def map[B](func: A => B): F[B] = {
    mappable.map(x)(func)
  }
}

Box(3).map(x => x + 3)
Box('A').map(x => x.toLower)

// We can create functions that accept any type for which a Mappable instance is defined
def turnSomethingIntoOnes[F[_]: Mappable, A](
    f: F[A]
) = { f.map(x => 1) }

turnSomethingIntoOnes(Box(3))

// Here, let's just hijack cats and create a Mappable for any instance where cats has already defined a functor
import cats.Functor
implicit def createMappableForAnyFunctor[F[_]: Functor]: Mappable[F] = {
  new Mappable[F] {
    def map[A, B](f: F[A])(func: A => B): F[B] = Functor[F].map(f)(func)
  }
}

// The compiler will use createMappableForAnyFunctor to supply a mappable for ANY functor we ask for
turnSomethingIntoOnes(List(1, 2, 3))
turnSomethingIntoOnes(Option("Definitely not 1"))

// In OOP, we tell the compiler we have a map method for an object by letting it inherit from something that has an abstract
// 'map' method. def doSomethingWithAMappable(a: Mappable). This will fail to compile if 'a' is not a Mappable or one of its
// subclasses.

// In FP, we tell the compiler the same thing by telling it to summon an instance of a Mappable from implicit scope.
// def doSomethingWithAMappable[A: Mappable](a: A). This will fail to compile if 'a' does not
// have a 'Mappable' instance defined for it in implicit scope.

// The advantage is that we can define the 'map' method in an ad-hoc manner on any existing type without having to edit library
// code or wrap library types in our own types.

// OOP
// val x = SomeUsefulLibraryClass()
// val y = WrapWithMappableClass(x) - supply a .map method by wrapping 'x' in our own class that implements map
// def doSomethingThatRequiresMappingBehaviour(x) {
//      x.map(doSomethingWithTheStuffInsideX)
// }
// doSomethignThatRequiresMappingBehaviour(y)
// If we want to access any other functionality of x, we have to wrap all its methods in the wrapper class, or reach inside
// the wrapper class to get the x out.

// FP
// val x = SomeUsefulLibraryClass()
// supply a `.map` method by defining a Mappable[F] with a def map(f: X[A])(func: A => B): X[B])
// implicit mappableForX: Mappable[X] = new Mappable[X]{...}
// def doSomethingThatRequiresMappingBehaviour[SomeUsefulLibraryClass: Mappable](x){
//      x.map(doSomethingWithTheStuffInsideX)
// }
