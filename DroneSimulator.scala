import scala.collection.mutable
import scala.util.Random
import scala.collection.mutable.ArrayBuffer
import java.io._

class IPDroneGenerator(var sessionCount: Int, var sessionLength: Int) {
  var sessions = mutable.Map[String, Int]()

  def get_ip = {
    sessionGc()
    sessionCreate()
    val ip = sessions.keys.toSeq(Random.nextInt(sessions.size))
    sessions(ip) += 1
    ip
  }

  private def sessionCreate() = {
    while (sessions.size < sessionCount) {
      sessions(randomIp) = 0
    }
  }

  private def sessionGc() = {
    for ((ip, count) <- sessions) {
      if (count >= sessionLength) sessions.remove(ip)
    }
  }

  private def randomIp: String = {
    val random = Random
    var octets = ArrayBuffer[Int]()
    octets += random.nextInt(223) + 1
    (1 to 3).foreach { _ => octets += random.nextInt(255) }
    octets.mkString(".")
  }
}

class LogGenerator(val ipGenObj: IPDroneGenerator, var messagesCount: Int = 0) {
  val LAT_LONG = mutable.Map(
    "52.40, 16.91, 300m" -> 30,
    "53.40, 16.94, 301m" -> 30,
    "54.40, 16.95, 302m" -> 40
  )

  val random = Random

  def writeMps(dest: FileWriter, mps: Int) = {
    val sleepTime = 1000 / mps
    while (messagesCount < 20000000) {
      write(dest)
      Thread.sleep(sleepTime)
      messagesCount += 1
      println(s"Messages Count: $messagesCount")
    }
  }

  def write(dest: FileWriter) = {
    val ip = ipGenObj.get_ip
    val ll = pickWeightedKey(LAT_LONG)
    val format = new java.text.SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z")
    val date = format.format(new java.util.Date())
    try {
      val bw = new BufferedWriter(dest)
      // lat, long, height(m), temp(C), wind(m/s), humidity, air-polution
      bw.write(s"$ip - - [$date] ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} ${randomNr(1, 30)} \n")
      bw.flush()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  private def randomNr(from: Int, to: Int):Double = {
    val rnd = new scala.util.Random
    val range = from to to
    range(rnd.nextInt(range.length))
  }

  private def pickWeightedKey(map: mutable.Map[String, Int]): String = {
    var total = 0
    map.values.foreach { weight => total += weight }
    val rand = Random.nextInt(total)
    var running = 0
    for ((key, weight) <- map) {
      if (rand >= running && rand < (running + weight)) {
        return key
      }
      running += weight
    }
    map.keys.head
  }
}

object RandomDroneGen extends App {
  val usage =
    """
	Usage: random_gen <file_path> <mps>
		file_path - path of the file where to write the log events to
		mps - messages to generate per second
	Random Drone flight log events generator, simulates generating log events with random_ip(s)
    	"""

  if (args.length != 2) {
    System.err.println(usage)
    System.exit(1)
  }

  val outputFile = new File(args(0))
  val messagesPerSec = args(1).toInt
  val outputFileWriter = new FileWriter(outputFile)

  sys.addShutdownHook {
    outputFileWriter.close()
  }

  try {
    println("Drones starting")
    Thread.sleep(1000)
    new LogGenerator(new IPDroneGenerator(100, 10)).writeMps(outputFileWriter, messagesPerSec)
  } catch {
    case e: Exception => e.printStackTrace()
  } finally {
    println("Drones landing")
    outputFileWriter.close()
  }
}

//RandomDroneGen.main(args)