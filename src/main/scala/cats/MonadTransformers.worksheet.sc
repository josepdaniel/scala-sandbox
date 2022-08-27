import cats.data.EitherT
import cats.implicits.*
import cats.*
import cats.data.OptionT
// Imagine we have a database lookup function that returns a user that may or may not exist

case class User(id: String, name: String);

// User may not exist
def getUserById(id: String): Option[User] = {

  id match {
    case "123" => Applicative[Option].pure(User("123", "Johnny"))
    case "456" => Applicative[Option].pure(User("456", "Alice"))
    case _     => None
  }

}

// Might fail due to network
def doDatabaseLookup(id: String): Either[String, Option[User]] = {
  id match {
    case "789" => Left("Network error")
    case _     => Right(getUserById(id))
  }
}

// How do we get the name of a user? It's pretty ugly
for {
  optUser <- doDatabaseLookup("123")
} yield for {
  user <- optUser
} yield user.name

// Cats provides OptionT[F[_], A] which is an alias for F[Option[A]]. For example, we can have an OptionT[List, A]
val x: OptionT[List, Int] = OptionT(
  List(
    Option(3),
    Option.empty,
    Option(43)
  )
)

// // With a single `.map` method we can access the inner thing
x.map(i => i + 1)

// Going back to the original example, instead of returning Either[String, Option[User]], we can return
// an OptionT[Either[String, _], User]
// The `[T] =>> Either[String, T]` expression is a way of writing `type Thingy[T] = Either[String, T]` anonymously
def doDatabaseLookupTransformer[F](
    id: String
): OptionT[[T] =>> Either[String, T], User] = {
  OptionT(doDatabaseLookup("123"))
}

var name = "Bobby"
// Cheeky impurity
doDatabaseLookupTransformer("123").map(user => name = user.name)
println(name)
