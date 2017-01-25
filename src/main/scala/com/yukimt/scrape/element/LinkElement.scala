package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver}

trait LinkElement extends HtmlElementLike {
  def setUrl(newUrl: String)(implicit driver: WebDriver): Unit
  def url: Option[String]

  def setQueryString(param: Map[String, String])(implicit driver: WebDriver): Unit = {
    val queryString = param.map{
      case (key, value) => s"$key=$value"
    }.mkString("&")
    url.foreach{ _url =>
      val connector = 
        if (_url contains "?") "&"
        else "?"
      val newUrl = _url + connector + queryString
      setUrl(newUrl)
    }
  }
}
