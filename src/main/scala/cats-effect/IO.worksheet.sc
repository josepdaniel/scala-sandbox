import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.implicits.*
import cats.implicits.*
import java.util.UUID

val randomNumber: IO[UUID] = IO.defer(IO.delay(UUID.randomUUID()))

val x = randomNumber
val y = randomNumber

(x, y).tupled.unsafeRunSync()
