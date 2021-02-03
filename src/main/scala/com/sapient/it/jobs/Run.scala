package scala.com.sapient.it.jobs
/*
*Implicit Classes
Implicit classes allow implicit conversations with class’s primary constructor when the class is in scope. Implicit class is a class marked with ‘implicit’ keyword. This feature is introduced in Scala 2.10.

Syntax − The following is the syntax for implicit classes. Here implicit class is always in the object scope where all method definitions are allowed because implicit class cannot be a top level class.

Syntax
object <object name> {
   implicit class <class name>(<Variable>: Data type) {
      def <method>(): Unit =
      * }
}
Note −
Implicit classes must be defined inside another class/object/trait (not in top level).
Implicit classes may only take one non –implicit argument in their constructor.
Implicit classes may not be any method, member or object in scope with the same name as the implicit class*/
object Run {
  implicit class IntTimes(x:Int){
    def times [A](f: =>A):Unit={
      def loop(current:Int):Unit=
      if(current>0){
        f
        loop(current-1)
      }
      loop(x)
    }
  }

}
