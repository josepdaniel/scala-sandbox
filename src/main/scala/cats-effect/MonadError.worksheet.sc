import cats.effect.kernel.Resource.apply
import cats.implicits.*
import cats.effect.implicits.*
import cats.effect.*
import cats.effect.unsafe.implicits.*
import concurrent.duration.*

val x = Option.empty

val y = x.handleError(_ => -1)

val z = Left(3)

val resource = Resource.eval(IO(3))

implicit class RetryHelper[A](io: IO[A]) {
  def retry(attempts: Int, sleep: FiniteDuration): IO[A] = {
    def retryLoop(times: Int): IO[A] = {
      io.recoverWith { case ex =>
        if (times != 0) {
          IO.println("Retrying") *> IO.sleep(sleep) *> retryLoop(
            times - 1
          )
        } else {
          IO.raiseError(ex)
        }
      }
    }
    retryLoop(attempts)
  }
}

resource
  .use(number => {
    for {
      _ <- IO.raiseError(Throwable("fuck u"))
    } yield ()
  })
  .handleErrorWith(t => IO.println("fuck"))
