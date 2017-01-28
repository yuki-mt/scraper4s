package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver, JavascriptExecutor}

class FormElement(val element: WebElement) extends HtmlElementLike {
  def setUri(newUri: String)(implicit driver: WebDriver): Unit = {
    val code = s"arguments[0].setAttribute('action', '$newUri');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }

  def url: Option[String] = attr("action")

  def submit(): Unit = element.submit
  def addFormData(name: String, value: String)(implicit driver: WebDriver) = {
    appendElement("input", Map("name"->name, "value"->value, "type"->"hidden"), None)
  }
}
