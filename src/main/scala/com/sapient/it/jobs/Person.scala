package scala.com.sapient.it.jobs
class Person(name: String, age: Int)
{
  // Definging canEqual method
  def canEqual(a: Any) = a.isInstanceOf[Person]
  // Defining equals method with override keyword
  override def equals(that: Any): Boolean =
    that match
    {
      case that: Person => that.canEqual(this) && this.hashCode == that.hashCode
      case _ => false
    }
  // Defining hashcode method
  override def hashCode: Int = {
    val prime = 31
    var result = 1
    result = prime * result + age;
    result = prime * result + (if (name == null) 0 else name.hashCode)
    return result
  }
  override def toString ={
    val key=name+":"+age
    s"$key"
  }
}