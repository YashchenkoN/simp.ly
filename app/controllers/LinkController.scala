package controllers

import javax.inject._

import dtos.{CommonResponse, CommonResponseWithData, LinkDto}
import io.netty.handler.codec.http.HttpResponseStatus
import models.Link
import play.api.libs.json._
import play.api.mvc._
import services.LinkService
import validators.UrlValidator

import scala.language.postfixOps


@Singleton
class LinkController @Inject()(linkService: LinkService) extends Controller {

  def redirect(shortUrl: String) = Action {
    val linkOption: Option[Link] = linkService.read(shortUrl)
    if (linkOption.nonEmpty) Redirect(linkOption.map(_.url).orNull)
    else Redirect("/")
  }

  def saveLink = Action { request =>
    var link: Link = request.body.asJson.map(json => {
      val url: String = (json \ "url").asOpt[String].orNull
      val shortUrl: String = (json \ "shortUrl").asOpt[String].orNull
      Link(0L, url, shortUrl)
    }).orNull
    if (link != null && UrlValidator.isValid(link.url)) {
      link = linkService.save(link)
      Ok(Json.toJson(CommonResponseWithData(LinkDto(link.url, link.shortUrl), HttpResponseStatus.OK.code(), "OK")))
    } else {
      BadRequest(Json.toJson(CommonResponse(HttpResponseStatus.BAD_REQUEST.code(), "Invalid URL")))
    }
  }

  implicit val writesLd: Writes[LinkDto] = Json.writes[LinkDto]
  implicit val writesCr: Writes[CommonResponse] = Json.writes[CommonResponse]

  implicit def searchResultsWrites[T](implicit fmt: Writes[T]): Writes[CommonResponseWithData[T]] =
    new Writes[CommonResponseWithData[T]] {
      def writes(ts: CommonResponseWithData[T]) = Json.obj(
        "status" -> ts.status,
        "message" -> ts.message,
        "data" -> ts.data
      )
    }
}
