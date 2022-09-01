import cats.effect.std.{Dispatcher, Queue}
import cats.effect.IO
import cats.effect.unsafe.implicits.*

// Say we are using a third party library with an impure interface that we need to implement.
// In our implementation we want `onMessage` to put the message into a queue.
abstract class ImpureInterface {
  def onMessage(msg: String): Unit

  def sendMessage(msg: String): Unit = {
    onMessage(msg)
  }
}

val result = for {
  queue <- Queue.unbounded[IO, String]

  // Implement the impure interface.

  impureInterface <- IO.delay(new ImpureInterface {
    // This won't actually do anything because the
    // result of queue.offer(msg) is an IO[Unit] which is discarded
    override def onMessage(msg: String): Unit =
      queue.offer(msg)
  })
  _ <- IO.delay(impureInterface.sendMessage("hello world"))
  value <- queue.tryTake
} yield value match {
  case Some(v) => s"Value found '$v'"
  case None    => "No value found"
}

// This unsafeRunSync is for demo purposes only. Should not be used in real life.
result.unsafeRunSync()

// So that didn't work. We should use a Dispatcher to perform the nasty side-effect
// The dispatcher will dispatch the effectful stuff to the existing scheduler that comes
// with an IOApp, so it's much cheaper that instantiating your own scheduler with an IO.unsafeRun*
val result2 = Dispatcher[IO].use { d =>
  for {
    queue <- Queue.unbounded[IO, String]

    impureInterface <- IO.delay(new ImpureInterface {
      override def onMessage(msg: String): Unit = {
        d.unsafeRunSync(queue.offer(msg))
      }
    })

    _ <- IO.delay(impureInterface.sendMessage("Hello"))
    value <- queue.tryTake
  } yield value match {
    case Some(v) => s"Value found '$v'"
    case None    => "No value found"
  }
}

// This unsafeRunSync is for demo purposes only. Should not be used in real life.
result2.unsafeRunSync()

// Be careful not to leak a dispatcher. Dispatcher.apply returns a Resource[F, Dispatcher].
// After the '.use' exits, the dispatcher is destroyed. If it is leaked, any attempts
// to submit an effect with a dispatcher throws an exception
val leaky = Dispatcher[IO].use { d =>
  for {
    something <- IO(3)
  } yield d
}

// leaky
//   .map(dispatcher => dispatcher.unsafeRunSync(IO.println("Hello world")))
//   .unsafeRunSync()
// Java exception
