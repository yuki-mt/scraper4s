package com.yukimt.scrape
package browser

import collection.JavaConversions._
import org.openqa.selenium.WebDriver
import scala.concurrent.duration.FiniteDuration
import org.openqa.selenium.{Cookie, By, JavascriptExecutor}
import org.openqa.selenium.support.ui.{WebDriverWait, ExpectedConditions}
import java.util.UUID

trait Browser {
  protected def driver: WebDriver // make it protected because WebDriver is mutable
  def url: String
  def proxy: Option[ProxyServer]
  def timeout: FiniteDuration
  def userAgent: UserAgent
  def customHeaders: Map[String, String]

  /************Set up***********/
  protected def setUserAgent: Unit
  protected def setCustomHeaders: Unit
  driver.manage.timeouts.implicitlyWait(getValue(timeout), timeout.unit)
  setCustomHeaders
  setUserAgent
  driver.get(url)
  
  /************Header***********/
  def addHeader(key: String, value: String): Browser

  /************Cookie***********/
  def addCookie(key: String, value: String): Browser = {
    driver.manage.addCookie(new Cookie(key, value))
    this
  }
  def removeCookie(key: String): Browser = {
    val c = driver.manage.getCookieNamed("key")
    driver.manage.deleteCookie(c)
    this
  }
  def clearCookie(): Browser = {
    driver.manage.deleteAllCookies
    this
  }
  def getCookies():Map[String, String] = {
    driver.manage.getCookies.map(c => c.getName -> c.getValue).toMap
  }
  def getCookie(key: String): Option[String] = {
    Option(driver.manage.getCookieNamed(key)).map(_.getValue)
  }
  
  /************wait***********/
  def waitUntilLoaded(path: By, maxTimeout: FiniteDuration):Browser = {
    new WebDriverWait(driver, maxTimeout.toSeconds)
      .until(ExpectedConditions.presenceOfElementLocated(path))
    this
  }
  def wait(timeout: FiniteDuration): Browser = {
    driver.manage.timeouts.pageLoadTimeout(getValue(timeout), timeout.unit)
    this
  }

  /************Window***********/
  def inAllWindows(f: Browser => Any): Browser = {
    getFromAllWindows(f)
    this
  }
  def getFromAllWindows[T](f: Browser => T): Seq[T] = {
    val currentWindow = driver.getWindowHandle
    //FIXME: wish f is executed asyncronously in each window
    val result = driver.getWindowHandles.toSeq.map{ window =>
      driver.switchTo.window(window)
      f(this)
    }
    driver.switchTo.window(currentWindow)
    result
  }
  def withWindow(window: Window)(f: Browser => Any): Browser = {
    getFromWindow(window)(f)
    this
  }
  def getFromWindow[T](window: Window)(f: Browser => T): T = {
    val currentWindow = driver.getWindowHandle
    driver.switchTo.window(window.id)
    val result = f(this)
    driver.switchTo.window(currentWindow)
    result
  }
  def switchWindow(window: Window): Browser = {
    driver.switchTo.window(window.id)
    this
  }
  def getCurrentWindow(): Window = {
    Window(driver.getWindowHandle)
  }
  def depulicateCurrentWindow(): Window = {
    val url = getCurrentUrl
    executeJs(s"window.open('$url');")
    getCurrentWindow
  }

  /************Javascript***********/
  def executeJs(code: String): Browser = {
    driver.asInstanceOf[JavascriptExecutor].executeScript(code)
    this
  }

  /************Histroy***********/
  def back(n: Int = 1): Browser = {
    (1 to n).foreach(_ => driver.navigate.back)
    this
  }
  def forward(n: Int = 1): Browser = {
    (1 to n).foreach(_ => driver.navigate.forward)
    this
  }
  
  /************Response Header***********/
  def getResponseHeader(): Map[String, String]
  def getStatusCode():Option[Int]


  def takeScreenshot(path: String, viewpoint: ViewPoint): Browser

  def getTitle() = driver.getTitle
  def getBody() = driver.getPageSource
  def getCurrentUrl() = driver.getCurrentUrl
  def quit() = driver.quit

  protected def getValue(duration: FiniteDuration): Int = {
    duration.toString.split(' ').head.toInt
  }
}
