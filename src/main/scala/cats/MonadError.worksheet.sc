import cats.implicits.*

val x = Option.empty

val y = x.handleError(_ => -1)
