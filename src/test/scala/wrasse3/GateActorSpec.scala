package wrasse3

import org.specs.Specification
import org.specs.mock.Mockito
import actors.Actor

/**
 * Created by IntelliJ IDEA.
 * User: przemek
 * Date: 08.11.10
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */

class GateActorSpec extends Specification with Mockito {

  val service = mock[Service]
  val gate = new GateActor(service)

  gate.start()

  def hitService(): Option[Any] = gate !? (1000, Request)
  implicit def toRepeatingInt(n: Int) = new { def * (body: => Any) = { (1 to n).foreach( _ => body) } }

  "gate actor should be transparent if no errors occur" in {
    service.hit() returns OkResponse

    val response = hitService()

    response must beSome(OkResponse)
  }

  "after 10 calls, service.hit must not be called" in {
    service.hit() returns ErrorResponse

    11 * hitService()

    there was 10.times(service).hit()
  }

  "after configured timeout, actor forwards requests again" in {
    service.hit() returns ErrorResponse

    10 * hitService()

    Thread.sleep(2000)

    hitService()

    there was 11.times(service).hit()
  }
}


