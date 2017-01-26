package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver, JavascriptExecutor}

class FormElement(val element: WebElement) extends HtmlElementLike {
  def setUrl(newUrl: String)(implicit driver: WebDriver): Unit = {
    val code = s"arguments[0].setAttribute('action', '$newUrl');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }

  def url: Option[String] = attr("action")

  def submit(): Unit = element.submit
}
