package models

import play.api.libs.json._

/**
  * @author Nikolay Yashchenko
  * @since
  */
case class Link(var id: Long,
                val url: String,
                val shortUrl: String)

object Link {
  implicit val writes = new Writes[Link] {
    override def writes(o: Link): JsValue =
      Json.obj(
        "id" -> o.id,
        "url" -> o.url,
        "shortUrl" -> o.shortUrl
      )
  }
}
