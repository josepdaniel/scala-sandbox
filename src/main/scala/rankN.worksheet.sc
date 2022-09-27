def returnPolymorphic[A](a: A) = {

  def iAmPolyMorphic[B](b: B) = {
    println(b)
    println(a)
  }

  iAmPolyMorphic
}

returnPolymorphic(3)("hello")
