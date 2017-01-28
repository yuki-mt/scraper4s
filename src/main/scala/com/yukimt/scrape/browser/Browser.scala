package com.yukimt.scrape
package browser

import collection.JavaConversions._
import scala.concurrent.duration.FiniteDuration
import org.openqa.selenium.{WebDriver, Cookie, By, JavascriptExecutor}
import org.openqa.selenium.support.ui.{WebDriverWait, ExpectedConditions}
import com.yukimt.scrape.element.{Parser, Element, HtmlElement}

trait Browser[S] {
  implicit def browserToS(b: Browser[S]): S = b.asInstanceOf[S]

  protected val driver: WebDriver // make it protected because WebDriver is mutable
  lazy val parser = new Parser(driver)
  def url: String
  def proxy: Option[ProxyServer]
  def timeout: FiniteDuration
  def userAgent: UserAgent
  def customHeaders: Map[String, String]
  
  /************Cookie***********/
  def addCookie(key: String, value: String): S = {
    driver.manage.addCookie(new Cookie(key, value))
    this
  }
  def removeCookie(key: String): S = {
    val c = driver.manage.getCookieNamed("key")
    driver.manage.deleteCookie(c)
    this
  }
  def clearCookie: S = {
    driver.manage.deleteAllCookies
    this
  }
  def cookies:Map[String, String] = {
    driver.manage.getCookies.map(c => c.getName -> c.getValue).toMap
  }
  def cookie(key: String): Option[String] = {
    Option(driver.manage.getCookieNamed(key)).map(_.getValue)
  }
  
  /************wait***********/
  def waitUntilLoaded(path: By, maxTimeout: FiniteDuration): S = {
    new WebDriverWait(driver, maxTimeout.toSeconds)
      .until(ExpectedConditions.presenceOfElementLocated(path))
    this
  }
  def wait(timeout: FiniteDuration): S = {
    driver.manage.timeouts.pageLoadTimeout(getValue(timeout), timeout.unit)
    this
  }

  /************Javascript***********/
  def executeJs(code: String): S = {
    executeJsWithResult(code)
    this
  }
  
  def executeJsWithResult(code: String): Any = {
    driver.asInstanceOf[JavascriptExecutor].executeScript(code)
  }

  /************Histroy***********/
  def back: S = back(1)
  def back(n: Int): S = {
    (1 to n).foreach(_ => driver.navigate.back)
    this
  }
  def forward(n: Int): S = {
    (1 to n).foreach(_ => driver.navigate.forward)
    this
  }
  def forward: S = forward(1)
  
  /************Parse***********/
  def parse(f: Parser => Any): S = {
    f(parser)
    this
  }
  def extract(f: Parser => HtmlElement): Element = {
    implicit val _driver = driver
    f(parser).toElement
  }
  def extract(f: Parser => Iterable[HtmlElement]): Iterable[Element] = {
    implicit val _driver = driver
    f(parser).map(_.toElement)
  }

  def title = driver.getTitle
  def body = driver.getPageSource
  def currentUrl = driver.getCurrentUrl
  def quit = driver.quit

  protected def getValue(duration: FiniteDuration): Int = {
    duration.toString.split(' ').head.toInt
  }
}
