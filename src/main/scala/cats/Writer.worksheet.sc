import cats._
import cats.kernel._
import cats.data._
import cats.implicits._

// The Writer[A, B] allows you to carry around an additional type A that can be used to store (for example) logs
val val10: Writer[Vector[String], Int] =
  Writer(Vector("Some job that computed 10"), 10)
// val10: Writer[Vector[String], Int] = WriterT((Vector("Some job that computed 10"), 10))

// A function that adds 20 to the input and appends its logs to whatever log container the caller wants (L[_])
// The appending is done with an implicit semigroup
def add20[L[_]: Applicative](someValue: Int): Writer[L[String], Int] = {
  // Some computation that generates logs
  val logs = Applicative[L].pure[String]("Adding 20")
  val computedOutput = 20 + someValue
  Writer(logs, computedOutput)
}

// Kind of annoying that we need to specify add20[Vector] here. Can't this be inferred from the type of val10?
val val30 = val10.flatMap(add20[Vector])
// val30: WriterT[Id, Vector[String], Int] = WriterT((Vector("Some job that computed 10", "Adding 20"), 30))

// Produces logs inside a vector
val val31 = add20[Vector](11)
// val31: Writer[Vector[String], Int] = WriterT((Vector("Adding 20"), 31))

// Produces logs inside a list
val val32 = add20[List](12)
// val32: Writer[List[String], Int] = WriterT((List("Adding 20"), 32))

// We can also encapsulate a function inside a writer, rather than a writer inside a function. The function is applied to another writer
// using an implicit `Apply` instance
val add23 = Writer[Vector[String], Int => Int](
  Vector("Adding 21"),
  (someValue: Int) => someValue + 23
)
// add23: WriterT[Id, Vector[String], Int => Int] = WriterT((Vector("Adding 21"), <function1>))

// Pay attention to the order of the outputs. The logs of `add23` come before the logs of `val10`.
val val33 = val10.ap(add23)
// val33: WriterT[Id, Vector[String], Int] = WriterT((Vector("Adding 21", "Some job that computed 10"), 33))
