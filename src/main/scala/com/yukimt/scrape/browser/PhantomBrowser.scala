package com.yukimt.scrape
package browser

import scala.concurrent.duration._
import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import org.openqa.selenium.OutputType
import org.apache.commons.io.FileUtils
import java.net.{URLEncoder, URLDecoder}

trait PhantomBrowserLike extends Browser[PhantomBrowser] {
  val codingKey = "utf-8"

  /************Cookie***********/
  override def addCookie(key: String, value: String) = {
    val encodedKey = URLEncoder.encode(key, codingKey)
    val encodedValue = URLEncoder.encode(value, codingKey)
    js(s"document.cookie = '$encodedKey=$encodedValue';")
  }
  override def removeCookie(key: String) = {
    val encodedKey = URLEncoder.encode(key, codingKey)
    js(s"document.cookie = '$encodedKey=; max-age=0;';")
  }
  override def clearCookie = {
    js("document.cookie.split('; ').forEach(function(c) { document.cookie = c.replace(/=.*/, '=; max-age=0;');});")
  }
  override def cookies:Map[String, String] = {
    getFromJs("return document.cookie;").toString.split("; ").collect {
      case c if c.nonEmpty =>
        val split = c.split("=")
        URLDecoder.decode(split.head, codingKey) -> URLDecoder.decode(split.last, codingKey)
    }.toMap
  }
  override def cookie(key: String): Option[String] = {
    val encodedKey = URLEncoder.encode(key, codingKey)
    val code = s"var cook = (document.cookie + ';').match(/$encodedKey=(.+?);/);" + 
      "if(cook) return decodeURIComponent(cook[1]); else cook;"
    Option(getFromJs(code)).map(_.toString)
  }

  /************Set up***********/
  val cap = DesiredCapabilities.phantomjs
  customHeaders.foreach{
    case (key, value) =>
      cap.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + key, value)
  }
  cap.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "User-Agent", userAgent.toString)
  val arg = Seq("--proxy=192.168.33.11:22", "--proxy-auth=vagrant:vagrant", "--proxy-type=http")
  cap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, arg)
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
  val userAgent: UserAgent = new UserAgent(Device.Mac, BrowserType.Chrome),
  val customHeaders: Map[String, String] = Map.empty)
  extends PhantomBrowserLike with WindowManager[PhantomBrowserLike, PhantomBrowser]
