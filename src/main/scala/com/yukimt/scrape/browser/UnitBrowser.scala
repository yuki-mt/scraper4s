package com.yukimt.scrape
package browser

import scala.concurrent.duration._

trait UnitBrowserLike extends Browser[UnitBrowser] {

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
  def responseHeaders = driver.headers
  def statusCode = driver.statusCode
}

class UnitBrowser(
  val url: String,
  val proxy: Option[ProxyServer] = None,
  val timeout: FiniteDuration = 10 seconds,
  val userAgent: UserAgent = new UserAgent(Device.Mac, BrowserType.Chrome),
  val customHeaders: Map[String, String] = Map.empty)
  extends UnitBrowserLike with WindowManager[UnitBrowserLike, UnitBrowser]
