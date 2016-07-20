package validators

import java.net.URL

/**
  * @author Nikolay Yashchenko
  */
object UrlValidator {
  def isValid(url: String) = {
    try {
      new URL(url)
      true
    } catch {
      case malformed: Exception => false
    }
  }
}
