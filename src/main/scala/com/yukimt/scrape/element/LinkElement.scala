package com.yukimt.scrape
package element

trait LinkElement extends HtmlElementLike {
  def setUri(newUri: String): Unit
  def url: Option[String]

  def setQueryString(param: Map[String, String]): Unit = {
    val queryString = param.map{
      case (key, value) => s"$key=$value"
    }.mkString("&")
    url.foreach{ _url =>
      val connector = 
        if (_url contains "?") "&"
        else "?"
      val newUrl = _url + connector + queryString
      setUri(newUrl)
    }
  }
}
