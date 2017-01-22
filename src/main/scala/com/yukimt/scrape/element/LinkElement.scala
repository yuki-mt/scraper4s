package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver}

trait LinkElement extends HtmlElementLike {
  def setUrl(newUrl: String)(implicit driver: WebDriver): Unit
  def getUrl(): Option[String]

  def setQueryString(param: Map[String, String])(implicit driver: WebDriver): Unit = {
    val queryString = param.map{
      case (key, value) => s"$key=$value"
    }.mkString("&")
    getUrl.foreach{ url =>
      val connector = 
        if (url contains "?") "&"
        else "?"
      val newUrl = url + connector + queryString
      setUrl(newUrl)
    }
  }
}
