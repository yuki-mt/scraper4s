package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver, JavascriptExecutor}

class FormElement(val element: WebElement) extends HtmlElementLike {
  def setUrl(newUrl: String)(implicit driver: WebDriver): Unit = {
    val code = s"arguments[0].setAttribute('action', '$newUrl');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }

  def url: Option[String] = attr("action")

  def setQueryString(param: Map[String, String])(implicit driver: WebDriver): Unit = {
    val queryString = param.map{
      case (key, value) => s"$key=$value"
    }.mkString("&")
    val connector = 
      if (url contains "?") "&"
      else "?"
    val newUrl = url + connector + queryString
    setUrl(newUrl)
  }

  def submit(): Unit = element.submit
}
