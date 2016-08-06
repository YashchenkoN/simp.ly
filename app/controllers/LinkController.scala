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

  private val COOKIE_NAME: String = "links"
  private val COOKIE_VALUES_SEPARATOR = "."

  def redirect(shortUrl: String) = Action {
    val linkOption: Option[Link] = linkService.read(shortUrl)
    if (linkOption.nonEmpty) {
      linkService.incrementViews(linkOption.get)
      Redirect(linkOption.map(_.url).orNull)
    } else Redirect("/")
  }

  def getFromCookies = Action { request =>
    val cookies: Option[Cookie] = request.cookies.get(COOKIE_NAME)
    var result: Set[LinkDto] = Set()
    if (cookies.nonEmpty) {
      val cookie: Cookie = cookies.get
      val linkIds: Array[String] = cookie.value.split("\\" + COOKIE_VALUES_SEPARATOR)
      for (i <- linkIds.indices) {
        val curLink = linkService.read(linkIds(i).toLong)
        if (curLink.nonEmpty) {
          val link: Link = curLink.get
          result = result + LinkDto(link.url, link.shortUrl, link.views)
        }
      }
    }
    Ok(Json.toJson(CommonResponseWithData(result.toArray, HttpResponseStatus.OK.code(), "OK")))
  }

  def saveLink = Action { request =>
    var link: Link = request.body.asJson.map(json => {
      val url: String = (json \ "url").asOpt[String].orNull
      val shortUrl: String = (json \ "shortUrl").asOpt[String].orNull
      Link(0L, url, shortUrl, 0)
    }).orNull
    if (link != null && UrlValidator.isValid(link.url)) {
      link = linkService.save(link)

      val cookieOption: Option[Cookie] = request.cookies.get(COOKIE_NAME)
      var linksCookie: Cookie = null
      if (cookieOption.nonEmpty) {
        linksCookie = Cookie(COOKIE_NAME, cookieOption.get.value + COOKIE_VALUES_SEPARATOR + link.id.toString)
      } else {
        linksCookie = Cookie(COOKIE_NAME, link.id.toString)
      }

      Ok(Json.toJson(
        CommonResponseWithData(LinkDto(link.url, link.shortUrl, link.views), HttpResponseStatus.OK.code(), "OK"))
      ).withCookies(linksCookie)
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
