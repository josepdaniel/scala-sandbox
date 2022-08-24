import cats.data.State

// State[S, A] is a monad that carries around a 'State' type S and a 'Result' type A
// It is essentially a type alias for
// S => (S, A)
// They can be threaded together with map and flatmap with the state getting passed to the next computation
//
// E.g. random-number generator

import scala.util.Random

case class CoupleOfRandomNumbers(val i: Long, val j: Long)

// Side-effects, mutatations, gross
Random.setSeed(10)
val a = CoupleOfRandomNumbers(Random.nextLong(), Random.nextLong)
a.i
a.j

val nextLong = State[Long, Long] { x =>
  val next = x * 6364136223846793005L + 1442695040888963407L
  (next, x)
}

// Returns a State[Long, CoupleOfRandomNumbers]
val coupleOfRandomNumbers = for {
  i <- nextLong
  j <- nextLong
} yield CoupleOfRandomNumbers(i, j)

val b = coupleOfRandomNumbers.run(1).value._2
b.i
b.j
