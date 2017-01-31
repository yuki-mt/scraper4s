package com.yukimt.scrape
package browser

import scala.concurrent.duration._
import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import org.openqa.selenium.OutputType
import org.apache.commons.io.FileUtils

trait PhantomBrowserLike extends Browser[PhantomBrowser] {
  /************Set up***********/
  val cap = DesiredCapabilities.phantomjs
  customHeaders.foreach{
    case (key, value) =>
      cap.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + key, value)
  }
  cap.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "User-Agent", userAgent.toString)
  proxy.foreach{p =>
    val arg = Seq(s"--proxy=${p.host}:${p.port}", s"--proxy-auth=${p.username}:${p.password}", "--proxy-type=http")
    cap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, arg)
  }
  protected val driver = new PhantomJSDriver(cap)
  driver.get(url)

  def takeScreenshot(path: String, viewpoint: ViewPoint) = {
    driver.manage.window.setSize(viewpoint.toDemension)
    val file = driver.getScreenshotAs(OutputType.FILE)
    FileUtils.copyFile(file, new File(path))
    this
  }
}

class PhantomBrowser(
  val url: String,
  val proxy: Option[ProxyServer] = None,
  val userAgent: UserAgent = new UserAgent(Device.Mac, BrowserType.Chrome),
  val customHeaders: Map[String, String] = Map.empty)
  extends PhantomBrowserLike with WindowManager[PhantomBrowserLike, PhantomBrowser]
