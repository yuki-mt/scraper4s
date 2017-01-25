package com.yukimt.scrape
package browser

import scala.concurrent.duration._

class UnitBrowser(
  val url: String,
  val proxy: Option[ProxyServer] = None,
  val timeout: FiniteDuration = 10 seconds,
  val userAgent: UserAgent = new UserAgent(Device.Mac, BrowserType.Chrome),
  val customHeaders: Map[String, String] = Map.empty) extends Browser {

  protected val driver = new FixedHtmlUnitDriver(proxy)

  /************Set up***********/
  driver.manage.timeouts.implicitlyWait(getValue(timeout), timeout.unit)
  customHeaders.foreach{
    case (key, value) =>
      driver.setHeader(key, value)
  }
  driver.setHeader("User-Agent", userAgent.toString)
  driver.get(url)


  /************Response Header***********/
  def getResponseHeader() = driver.headers
  def getStatusCode() = driver.statusCode


  def takeScreenshot(path: String, viewpoint: ViewPoint) = {
    throw new RuntimeException("'takeScreenshot' is not implemented in UnitBrowser")
  }
}
