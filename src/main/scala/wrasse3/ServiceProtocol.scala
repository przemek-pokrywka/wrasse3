package wrasse3

case object Request

case class Response
case object OkResponse extends Response
case object ErrorResponse extends Response
