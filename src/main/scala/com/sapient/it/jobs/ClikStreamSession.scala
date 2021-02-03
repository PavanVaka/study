package scala.com.sapient.it.jobs
import java.sql.Timestamp
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scala.collection.mutable.ListBuffer

object ClikStreamSession {
  def main(args: Array[String]): Unit = {
    val interimSessionThreshold=30
    val totalSessionTimeThreshold=120
    val spark = SparkSession
      .builder()
      .appName("Spark Hive Example")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._
    //reading the data from the file
    val readCsv = spark.read.option("header",false).csv("C:/assignment/src/main/resources/dev/clickstream.csv")
      .withColumnRenamed("_c0","timestamp").withColumnRenamed("_c1","userid")
      .withColumn("time_stamp",unix_timestamp($"timestamp", "yyyy-MM-dd'T'HH:mm:ss'Z'").cast(TimestampType))
      .drop("timestamp")
    readCsv.show()
    val partitionWindow = Window.partitionBy("userid").orderBy("time_stamp")
    val lagTest = lag($"time_stamp", 1, "0000-00-00 00:00:00").over(partitionWindow)
    val df_test=readCsv.select($"userid",$"time_stamp", ((unix_timestamp($"time_stamp")-unix_timestamp(lagTest))/60D cast "int") as "diff_val_with_previous")
    df_test.show()
    val distinctuserid=df_test.select($"userid").distinct.as[String].collect.toList
    val rankTest = rank().over(partitionWindow)
    val ddf = df_test.select($"*", rankTest as "rank")
    ddf.show()
    case class lastClick(userid:String,time_stamp:Timestamp,session:String)
    val rowList: ListBuffer[lastClick] = new ListBuffer()
    distinctuserid.foreach{x =>{
      val tempDf= ddf.filter($"userid" === x)
      tempDf.show()
      var cumulDiff:Int=0
      var session_index=1
      var startBatch=true
      var dp=0
      val len = tempDf.count.toInt
      for(i <- 1 until len+1){
        val r = tempDf.filter($"rank" === i).head()
        dp = r.getAs[Int]("diff_val_with_previous")
        cumulDiff += dp
        if(dp <= interimSessionThreshold && cumulDiff <= totalSessionTimeThreshold){
          startBatch=false
          rowList += lastClick(r.getAs[String]("userid"),
            r.getAs[Timestamp]("time_stamp"),
            r.getAs[String]("userid")+session_index)
        }
        else{
          session_index+=1
          cumulDiff = 0
          startBatch=true
          dp=0
          rowList += lastClick(r.getAs[String]("userid"),
            r.getAs[Timestamp]("time_stamp"),
            r.getAs[String]("userid")+session_index)
        }
      }
    }}
    var rddList=List[Row]()
    for (elem <- rowList.toList) {
      val row = Row(elem.productIterator.toSeq: _*)
      rddList=row::rddList
    }
    val seqFrame = spark.sparkContext.parallelize(rddList)
    val schemaRef = StructType(List(
      StructField("user", DataTypes.StringType) ,
      StructField("timestamp", DataTypes.TimestampType) ,
      StructField("session", DataTypes.StringType)))
    val dataFrame=spark.createDataFrame(seqFrame,schemaRef)
    dataFrame.show()
  }
  }
