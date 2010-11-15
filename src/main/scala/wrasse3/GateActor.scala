package wrasse3

import scala.actors.Actor._
import System.{currentTimeMillis => now}
import actors.{Actor, TIMEOUT}

class GateActor(srv: Service, CLOSE_PERIOD: Int = 500) extends Actor {

  var errors = 0
  var end: Long = 0

  def act = loop{

    println("entering open state")

    loopWhile(errors < 10) {
      react{
        case _ => {
          val r = srv.hit()
          r match {
            case ErrorResponse => errors += 1
            case _ => errors = 0
          }
          reply(r)
        }
      }
    } andThen {

      println("entering closed state")
      errors = 0
      end = now + CLOSE_PERIOD

      loopWhile(now < end) {
        reactWithin(end - now) {
          case TIMEOUT => ()
          case _ => {
            println("gate is currently closed");
            reply(ErrorResponse)
          }
        }
      }

    }
  }

}
