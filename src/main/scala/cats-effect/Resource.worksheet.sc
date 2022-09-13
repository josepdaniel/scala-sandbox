import cats.effect.kernel.Ref
import cats.effect.Resource
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.*
import cats.effect.implicits.*

type Openness = Boolean
val OPEN: Openness = true
val CLOSED: Openness = false

trait ThingThatMightBeOpen {
  val thingThatMightBeOpen = Ref[IO].of(CLOSED)
}

def openTheThing() = {
  IO.println("Opening the thing") *> IO(
    new ThingThatMightBeOpen {}
  ).flatMap(thing =>
    thing.thingThatMightBeOpen
      .flatMap(t => t.updateAndGet(_ => OPEN))
      .map(_ => thing)
  )
}

def closeTheThing(thing: ThingThatMightBeOpen) = {
  IO.println("Closing the thing") *> thing.thingThatMightBeOpen.flatMap(t =>
    t.updateAndGet(_ => CLOSED)
  ) *> IO.unit
}

val myResource = Resource.make(openTheThing())(t => closeTheThing(t))
myResource.use(_ => IO.unit).unsafeRunSync()
// Opening the thing
// Closing the thing
