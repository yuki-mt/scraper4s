package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver, JavascriptExecutor}
import collection.JavaConversions._

class ATagElement(val element: WebElement) extends LinkElement {
  def openInNewWindow(implicit driver: WebDriver): Window = {
    val oldWindows = driver.getWindowHandles
    val code = s"window.open('$url');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code)
    val newId = (driver.getWindowHandles -- oldWindows).head
    Window(newId)
  }

  def setUri(newUri: String)(implicit driver: WebDriver): Unit = {
    val code = s"arguments[0].setAttribute('href', '$newUri');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }

  def url: Option[String] = attr("href")
}
