package wrasse3

import scala.actors.Actor._
import actors.TIMEOUT


/**
 * Created by IntelliJ IDEA.
 * User: przemek
 * Date: 09.11.10
 * Time: 18:02
 * To change this template use File | Settings | File Templates.
 */

object GateActor {

  def create(srv: Service) = actor {

    var subsequentErrors: Int = 0

    def tenErrorsARow = subsequentErrors >= 10

    val start = System.currentTimeMillis

    loopWhile(!tenErrorsARow && System.currentTimeMillis - start < 3000) {
      reactWithin(999) {
        case Request => {
          val response: Response = srv.serve()
          if (response == ErrorResponse)
            subsequentErrors += 1
          else
            subsequentErrors = 0
          reply(response)
        }
        case TIMEOUT => ()
        case x => throw new IllegalArgumentException("what was it: " + x)
      }
    }



    /*


       send to gsm
        react {
           case gsmResp => {
             sendBackToCustomer
           }
        }







     */


  }

}
