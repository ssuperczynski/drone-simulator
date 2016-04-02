import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

case class Person(name: String, age: Int)

object CsvParser {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._

    val people = sc.textFile("/tmp/people.txt").map(_.split(",")).map(p => Person(p(0), p(1).trim.toInt)).toDF()
    people.registerTempTable("people")

    val teenagers = sqlContext.sql("SELECT name, age FROM people WHERE age >= 13 AND age <= 19")

    teenagers.foreach(println(_))

    val group = people.groupBy("age").count().show()

    println(group)
  }
}
