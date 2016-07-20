package dtos

/**
  * @author Nikolay Yashchenko
  * @since
  */
case class CommonResponse(status: Int, message: String)

case class CommonResponseWithData[T](data: T, status: Int, message: String)
