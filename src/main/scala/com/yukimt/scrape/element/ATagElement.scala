package com.yukimt.scrape
package element

import org.openqa.selenium.{WebElement, WebDriver, JavascriptExecutor}

class ATagElement(val element: WebElement) extends HtmlElementLike {
  def openInNewWindow()(implicit driver: WebDriver): Window = {
    val code = s"window.open('${getUrl}');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code)
    Window(driver.getWindowHandle)
  }

  def setUrl(newUrl: String)(implicit driver: WebDriver): Unit = {
    val code = s"arguments[0].setAttribute('href', '$newUrl');"
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }

  def getUrl(): Option[String] = getAttribute("href")
}
