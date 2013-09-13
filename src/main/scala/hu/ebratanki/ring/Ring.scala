package hu.ebratanki.ring

import akka.actor._
import com.typesafe.config.ConfigFactory

/**
 * The Ring task's solution (Joe Armstrong: Programming Erlang, 1st ed, p157)
 * 
 * See https://github.com/gergelyfabian/erlang-tests/blob/master/ring.erl for
 * the Erlang variant.
 */
object Ring {
  import Element._
  val config = ConfigFactory.load()

  val system = ActorSystem("Ring", config)
  var firstElement: Option[ActorRef] = None

  def main(args: Array[String]) = {
    if (args.length < 2) {
      println("Wrong number of command line arguments.")
      println("Usage: java -jar jarfile className [no of ring elements] [number of messages to send]")
      System.exit(1)
    }

    try {
      val elementNo = args(0).toInt
      val messageNo = args(1).toInt
      firstElement = Some(system.actorOf(Element(elementNo)))
      firstElement.foreach {_ ! Message(messageNo, "This is my message")}
    } catch {
      case e: java.lang.NumberFormatException =>
        println("Wrong number of ring elements.")
        System.exit(1)
    }
  }
}

object Element {
  /**
   * What message and how many more times to send
   */
  case class Message(messageNo: Int, text: String)

  /**
   * Create the Ring's nth element
   * 
   * @param n Number of elements left (with the current one)
   */
  def apply(n: Int): Props = Props(new Element(n))
}

/**
 * One Ring element
 * 
 * @param n Which element it is, counting backwords.
 */
class Element(val n: Int) extends Actor with ActorLogging {
  import Element._
  
  // Create the Ring's next element.
  val nextElement: Option[ActorRef] = if (n > 1) {
    Some(context.system.actorOf(Element(n - 1)))
  } else {
    None
  }

  def receive = {
    case message @ Message(messageNo, text) =>
      //log.info("Got message. n: {}, messageNo: {}, text: {}", n, messageNo, text)
      
      // Check whether we reached the last element.
      if (n > 1) {
        nextElement.foreach {_ ! message}
      } else {
        log.info("Finished sending this round of messages: {}.", messageNo)
        // For the last element check whether we had the last round of messages.
        if (messageNo > 1) {
          Ring.firstElement.foreach {_ ! Message(messageNo - 1, text)}
        } else {
          log.info("That's all, shutting down.")
          context.system.shutdown()
        }
      }
  }
}
