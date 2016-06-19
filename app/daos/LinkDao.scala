package daos

import java.sql.Statement
import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.Link
import play.api.db.Database

/**
  * @author Nikolay Yashchenko
  * @since
  */
@ImplementedBy(classOf[LinkDaoImpl])
trait LinkDao {
  def read(shortUrl: String): Link
  def save(link: Link): Link
}

class LinkDaoImpl @Inject()(db: Database) extends LinkDao {

  def read(shortUrl: String): Link = {
    db.withConnection { conn =>
      val statement = conn.prepareStatement("SELECT * FROM Link WHERE shortUrl = ?")
      statement.setString(1, shortUrl)
      val rs = statement.executeQuery()
      if (rs.next()) Link(rs.getLong(1), rs.getString(2), rs.getString(3))
      else null
    }
  }

  def save(link: Link): Link = {
    db.withConnection { conn =>
      val statement = conn.prepareStatement(
        "INSERT INTO Link (url, shortUrl) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS
      )
      statement.setString(1, link.url)
      statement.setString(2, link.shortUrl)
      statement.executeUpdate()
      val generatedKeys = statement.getGeneratedKeys
      if (generatedKeys.next) Link(generatedKeys.getLong(1), link.url, link.shortUrl)
      else link
    }
  }
}
