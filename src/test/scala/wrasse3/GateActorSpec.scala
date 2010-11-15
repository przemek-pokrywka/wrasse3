package wrasse3

import org.specs.Specification
import org.specs.mock.Mockito

class GateActorSpec extends Specification with Mockito {

  val service = mock[Service]
  val gate = new GateActor(
    sendRequest = service.hit(),
    errorResponse = ErrorResponse,
    maxErrors = 10,
    closePeriod = 500)

  gate.start()

  def hitService(): Option[Any] = gate !? (100, Request)

  implicit def toRepeatingInt(n: Int) = new {
    def *(body: => Any) = (1 to n).foreach(_ => body)
  }

  "gate actor should be transparent if no errors occur" in {
    service.hit() returns OkResponse

    val response = hitService()

    response must beSome(OkResponse)
  }

  "after 10 calls, service.hit must not be called" in {
    service.hit() returns ErrorResponse

    10 * hitService() // that should close the gate
    hitService() // should not be counted, as gate is closed now

    there was 10.times(service).hit()
  }

  "after configured timeout, actor forwards requests again" in {
    service.hit() returns ErrorResponse

    10 * hitService() // that should close the gate

    Thread.sleep(600) // let's wait until gate is open again

    hitService() // now gate should be open, so hit should be counted

    there was 11.times(service).hit()
  }
}
