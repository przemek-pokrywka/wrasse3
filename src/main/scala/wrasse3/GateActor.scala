package wrasse3

import scala.actors.Actor._
import System.{currentTimeMillis => now}
import actors.{Actor, TIMEOUT}

class GateActor(srv: Service, CLOSE_PERIOD: Int = 500) extends Actor {

  val maxErrors = 10
  val errorResponse = ErrorResponse
  private def sendRequest() = srv.hit()

  def act = loop{

    println("entering open state")
    var errors = 0

    loopWhile(errors < maxErrors) {
      react{
        case _ => {
          val r = sendRequest()
          if (r == errorResponse) errors += 1 else errors = 0
          reply(r)
        }
      }
    } andThen {

      println("entering closed state")
      val end = now + CLOSE_PERIOD

      loopWhile(now < end) {
        reactWithin(end - now) {
          case TIMEOUT => ()
          case _ => {
            println("gate is currently closed");
            reply(errorResponse)
          }
        }
      }

    }
  }

}
