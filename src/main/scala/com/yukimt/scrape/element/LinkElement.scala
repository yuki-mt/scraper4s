package com.yukimt.scrape
package element

trait LinkElement extends HtmlElementLike {
  def setUri(newUri: String): Unit

  def setQueryString(param: Map[String, String]): Unit = {
    val queryString = param.map{
      case (key, value) => s"$key=$value"
    }.mkString("&")
    tryFirst(ElementMethod.url).foreach{ url =>
      val connector = 
        if (url contains "?") "&"
        else "?"
      val newUrl = url + connector + queryString
      setUri(newUrl)
    }
  }
}
