import scala.util.Random
import scala.collection.mutable.ArrayBuffer
import java.io._

class LogGenerator() {
  val random = Random

  def randomIp: String = {
    var octets = ArrayBuffer[Int]()
    octets += random.nextInt(223) + 1
    (1 to 3).foreach { _ => octets += random.nextInt(255) }
    octets.mkString(".")
  }

  def writeMps(mps: Int) = {
    val sleepTime = 1000 / mps
    while (true) {
      write()
      Thread.sleep(sleepTime)
    }
  }

  def write() = {
    val format = new java.text.SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z")
    val date = format.format(new java.util.Date())
    try {
      // lat, long, height(m), temp(C), wind(m/s), humidity, air-polution
      println(s"$randomIp - - [$date] ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)}")
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  private def randomNr(from: Int, to: Int):Double = {
    val range = from to to
    range(random.nextInt(range.length))
  }
}

object RandomDroneGen extends App {
  try {
    println("Drones starting")
    Thread.sleep(1000)
    new LogGenerator().writeMps(args(0).toInt)
  } catch {
    case e: Exception => e.printStackTrace()
  } finally {
    println("Drones landing")
  }
}

RandomDroneGen.main(args)