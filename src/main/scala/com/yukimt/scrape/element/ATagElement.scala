package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver, JavascriptExecutor}
import collection.JavaConversions._

class ATagElement(protected val element: WebElement, protected val driver: WebDriver)
  extends LinkElement {

  def openInNewWindow: Window = {
    val url = getFirst(ElementMethod.url)
    val oldWindows = driver.getWindowHandles
    val code = s"window.open('$url');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code)
    val newId = (driver.getWindowHandles -- oldWindows).head
    Window(newId)
  }

  def setUri(newUri: String): Unit = {
    val code = s"arguments[0].setAttribute('href', '$newUri');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }
}
