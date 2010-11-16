package wrasse3

import scala.actors.Actor._
import System.{currentTimeMillis => now}
import actors.{Actor, TIMEOUT}

class GateActor[R](sendRequest: => R,
                   errorResponse: R,
                   maxErrors: Int,
                   closePeriod: Int) extends Actor {

  def info(m: String) = println(this + " " + m)

  def stop() = try { exit() } catch { case _ => () }

  def beOpenUntilCrash {

    info("entering open state")
    var errors = 0

    loopWhile(errors < maxErrors) {
      react{
        case _ => {
          val r = sendRequest
          if (r == errorResponse) errors += 1 else errors = 0
          reply(r)
        }
      }
    }
  }

  def closeGateForSomeTime {

    info("entering closed state")
    val end = now + closePeriod

    loopWhile(now < end) {
      reactWithin(end - now) {
        case TIMEOUT => ()
        case _ => {
          info("gate is currently closed");
          reply(errorResponse)
        }
      }
    }
  }


  def act = loop {
    beOpenUntilCrash andThen closeGateForSomeTime
  }

}
