package wrasse3

import scala.actors.Actor._
import System.{currentTimeMillis => now}
import actors.TIMEOUT

/**
 * Created by IntelliJ IDEA.
 * User: przemek
 * Date: 09.11.10
 * Time: 18:02
 * To change this template use File | Settings | File Templates.
 */

object GateActor {

  def create(srv: Service, CLOSE_PERIOD: Int = 500) = actor{

    var subsequentErrors = 0
    var end: Long = 0
    def tenErrorsARow = subsequentErrors >= 10

    loop{
      println("entering open state")
      loopWhile(!tenErrorsARow) {
        react{
          case _ => {
            val r = srv.serve()
            r match {
              case ErrorResponse => subsequentErrors += 1
              case _ => subsequentErrors = 0
            }
            reply(r)
          }
        }
      } andThen {
        subsequentErrors = 0
        end = now + CLOSE_PERIOD
        println("entering closed state")
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

}
