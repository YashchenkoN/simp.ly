package controllers

import javax.inject._

import models.Link
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import play.api.mvc._
import services.LinkService

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class LinkController @Inject()(linkService: LinkService) extends Controller {

  def redirect(shortUrl: String) = Action {
    val linkOption: Option[Link] = linkService.read(shortUrl)
    if (linkOption.nonEmpty) Redirect(linkOption.map("http://" + _.url).orNull)
    else Redirect("/")
  }

  def saveLink = Action { request =>
    var link: Link = request.body.asJson.map(json => {
      val id: Long = (json \ "id").asOpt[Long].getOrElse(0)
      val url: String = (json \ "url").asOpt[String].map(prepare).orNull
      Link(id, url, url)
    }).orNull
    link = linkService.save(link)
    Ok(Json.toJson(link))
  }

  private def prepare(url: String): String = {
    val result = new StringBuilder(url)
    if (!StringUtils.endsWith(url, "/")) result append "/"
    if (StringUtils.startsWith(url, "https://")) result.delete(0, 7)
    else if (StringUtils.startsWith(url, "http://")) result.delete(0, 6)
    result toString
  }

}
