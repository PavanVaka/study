package scala.com.sapient.it.jobs
import scala.collection.immutable.HashMap
object DuplicateRemove {
  def main(args: Array[String]): Unit = {
    val dataMap= HashMap(new Person("Rajesh",21)->"London",
                         new Person("Suresh",28)->"California",
                         new Person("Sam",26)->"Delhi",
                         new Person("Rajesh",21)->"Grugoan",
                         new Person("Manish",29)->"Bangaluru")
    //println(hashMap2)
    dataMap.foreach
    {
      case (key, value) => println (key.toString+ " -> " + value)
    }
}}
