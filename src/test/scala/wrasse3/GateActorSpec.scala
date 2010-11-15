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
  val gate = GateActor.create(service)

  def hitService(): Option[Any] = gate !? (1000, Request)

  "gate actor should be transparent if no errors occur" in {
    service.hit() returns OkResponse

    val response = hitService()

    response must beSome(OkResponse)
  }

  "after 10 calls, service.hit must not be called" in {
    service.hit() returns ErrorResponse

    (1 to 11). foreach (_ => hitService())

    there was 10.times(service).hit()
  }

  "after configured timeout, actor forwards requests again" in {
    service.hit() returns ErrorResponse

    (1 to 11).foreach (_ => hitService())

    Thread.sleep(2000)

    hitService()

    there was 11.times(service).hit()
  }
}


