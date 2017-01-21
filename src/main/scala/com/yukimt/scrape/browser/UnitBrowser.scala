package com.yukimt.scrape
package browser

import collection.JavaConversions._
import org.openqa.selenium.WebDriver
import scala.concurrent.duration.FiniteDuration
import org.openqa.selenium.{Cookie, By, JavascriptExecutor}
import org.openqa.selenium.support.ui.{WebDriverWait, ExpectedConditions}
import org.openqa.selenium.htmlunit.HtmlUnitDriver

class UnitBrowser(
  val url: String,
  val proxy: Option[ProxyServer],
  val timeout: FiniteDuration,
  val userAgent: UserAgent,
  val customHeaders: Map[String, String]) extends Browser {

  protected val driver = new FixedHtmlUnitDriver(proxy)

  protected def setUserAgent = {
    driver.setHeader("User-Agent", userAgent.toString)
  }
  protected def setCustomHeaders = {
    customHeaders.foreach{
      case (key, value) =>
        driver.setHeader(key, value)
    }
  }
  
  def addHeader(key: String, value: String) = {
    driver.setHeader(key, value)
    this
  }

  /************Response Header***********/
  def getResponseHeader() = driver.headers
  def getStatusCode() = driver.statusCode

  def takeScreenshot(path: String, viewpoint: ViewPoint) = {
    throw new RuntimeException("'takeScreenshot' is not implemented in UnitBrowser")
  }
}
