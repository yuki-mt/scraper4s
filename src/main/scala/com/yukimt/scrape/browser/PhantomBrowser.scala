package com.yukimt.scrape
package browser

import scala.concurrent.duration._
import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import collection.JavaConversions._
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import org.openqa.selenium.OutputType
import org.apache.commons.io.FileUtils

class PhantomBrowser(
  val url: String,
  val proxy: Option[ProxyServer] = None,
  val timeout: FiniteDuration = 10 seconds,
  val userAgent: UserAgent = new UserAgent(Device.Mac, BrowserType.Chrome),
  val customHeaders: Map[String, String] = Map.empty) extends Browser {


  /************Set up***********/
  val cap = DesiredCapabilities.phantomjs
  customHeaders.foreach{
    case (key, value) =>
      cap.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + key, value)
  }
  cap.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "User-Agent", userAgent)
  proxy.foreach{p =>
    val arg = Seq(s"--proxy=${p.host}:${p.port}", s"--proxy-auth=${p.username}:${p.password}", "--proxy-type=http")
    cap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, arg)
  }
  protected val driver = new PhantomJSDriver(cap)
  driver.manage.timeouts.implicitlyWait(getValue(timeout), timeout.unit)
  driver.get(url)

  /************Response Header***********/
  def getResponseHeader() = {
    throw new RuntimeException("'getResponseHeader' is not implemented in UnitBrowser")
  }
  def getStatusCode() = {
    throw new RuntimeException("'getStatusCode' is not implemented in UnitBrowser")
  }


  def takeScreenshot(path: String, viewpoint: ViewPoint) = {
    driver.manage.window.setSize(viewpoint.toDemension)
    val file = driver.getScreenshotAs(OutputType.FILE)
    FileUtils.copyFile(file, new File(path))
    this
  }
}
