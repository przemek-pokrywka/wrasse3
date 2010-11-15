package wrasse3

import scala.actors.Actor._
import System.{currentTimeMillis => now}
import actors.{Actor, TIMEOUT}

/**
 * Created by IntelliJ IDEA.
 * User: przemek
 * Date: 09.11.10
 * Time: 18:02
 * To change this template use File | Settings | File Templates.
 */

class GateActor(srv: Service, CLOSE_PERIOD: Int = 500) extends Actor {

  var subsequentErrors = 0
  var end: Long = 0

  def tenErrorsARow = subsequentErrors >= 10

  def act = loop{

    println("entering open state")

    loopWhile(!tenErrorsARow) {
      react{
        case _ => {
          val r = srv.hit()
          r match {
            case ErrorResponse => subsequentErrors += 1
            case _ => subsequentErrors = 0
          }
          reply(r)
        }
      }
    } andThen {

      println("entering closed state")
      subsequentErrors = 0
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
