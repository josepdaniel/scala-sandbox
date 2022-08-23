import cats.Eval
import cats.implicits.*
import scala.util.Random

// Eval - control when a computation takes place

// Now - computed eagerly then memoized
val now = Eval.now(Random.nextInt())
// now: Eval[Int] = Now(1915440924)
now.value
// res0: Int = 1915440924
now.value
// res1: Int = 1915440924

// Later - computed lazily then memoized
val later = Eval.later(Random.nextInt())
// later: Eval[Int] = cats.Later@7f032631
later.value
// res2: Int = 1348349705
later.value
// res3: Int = 1348349705

// Always - computed lazily, every time
val always = Eval.always(Random.nextInt())
// always: Eval[Int] = cats.Always@4d58df90
always.value
// res4: Int = 1860059346
always.value
// res5: Int = -797451398

// maps and flatmaps do not consume stack frames
def unsafeFactorialFactory(n: BigInt): () => BigInt = {
  var x: () => BigInt = () => 1

  for { i <- BigInt(1).to(n) } {
    x = x.map(_ * i)
  }
  x
}
// unsafeFactorialFactory(150000)() // stackoverflow

def safeFactorialFactory(n: BigInt): () => BigInt = {
  var x = Eval.now(BigInt(1))

  for { i <- BigInt(1).to(n) } {
    x = x.map(_ * i)
  }
  () => x.value
}
safeFactorialFactory(150000)()
// res6: BigInt = 3189397646307349544087431388655648440056804700...

// Defer - take an instance of `Eval` and defer its computation
val deferred = Eval.defer(Eval.now(println("computing")))
// deferred: Eval[Unit] = cats.Eval$$anon$5@5346ad89
deferred.value
// computing

// It's useful for stack-safe 'recursion'.
def unsafeFactorial(n: BigInt): BigInt = {
  if (n == 1) {
    n
  }
  n * unsafeFactorial(n - 1)
}
// unsafeFactorial(150000) // stackOverflowError

// Instead of allocating a stack frame for each call to `safeFactorial` it creates a big recursive structure
// with function pointers on the heap which can be iteratively traversed
def safeFactorial(n: BigInt): Eval[BigInt] = {
  if (n == 1) {
    Eval.now(1)
  } else {
    Eval.defer(safeFactorial(n - 1).map(_ * n))
  }
}
safeFactorial(150000).value
// res8: BigInt = 3189397646307349544087431388655648440056804700...

// Probably looks something like this

// class Defer[A](a: => Eval[A]) extends Eval {
//   val thunk = () => a
//   def value: A = {
//     var x = thunk()
//     while (x.isInstanceOf[Defer[A]]) {
//       x = x.thunk()
//     }
//     return x.value
//   }
// }
