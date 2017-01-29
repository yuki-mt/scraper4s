package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver, JavascriptExecutor}

class FormElement(protected val element: WebElement, protected val driver: WebDriver)extends LinkElement {

  def setUri(newUri: String): Unit = {
    val code = s"arguments[0].setAttribute('action', '$newUri');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }

  def submit(): Unit = element.submit
  def addFormData(name: String, value: String) = {
    appendElement("input", Map("name"->name, "value"->value, "type"->"hidden"), None)
  }
}
