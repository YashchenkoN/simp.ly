package services

import java.nio.charset.StandardCharsets
import javax.inject.Inject

import com.google.common.hash.Hashing
import com.google.inject.ImplementedBy
import daos.LinkDao
import models.Link

/**
  * @author Nikolay Yashchenko
  * @since
  */
@ImplementedBy(classOf[LinkServiceImpl])
trait LinkService {
  def read(shortUrl: String): Option[Link]
  def save(link: Link): Link
  def simplify(link: Link): Link
  def checkOrSimplify(link: Link): Link
}

class LinkServiceImpl @Inject()(linkDao: LinkDao) extends LinkService {

  def read(shortUrl: String): Option[Link] =
    Option.apply(linkDao.read(shortUrl))

  def save(link: Link): Link =
    linkDao.save(if (link.shortUrl == null) simplify(link) else checkOrSimplify(link))

  def simplify(link: Link): Link = {
    require(link.url != null)
    val builder = new StringBuilder
    builder append Hashing.murmur3_32().hashString(link.url, StandardCharsets.UTF_8).toString
    Link(link.id, link.url, builder.toString)
  }

  def checkOrSimplify(link: Link): Link = {
    if (linkDao.read(link.shortUrl) != null) simplify(link)
    else link
  }
}