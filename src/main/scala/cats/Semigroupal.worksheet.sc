import cats.*
import cats.kernel.*
import cats.data.*
import cats.implicits.*
import cats.syntax.all.*

// mapN
(Option(3), Option(4)).mapN((a, b) => a + b)

(Option(3), Option(4)).tupled

Option((1, 2)).unzip
