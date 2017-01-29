package com.yukimt.scrape
package browser

import collection.JavaConversions._
import scala.concurrent.duration.FiniteDuration
import org.openqa.selenium.{WebDriver, Cookie, By, JavascriptExecutor}
import com.yukimt.scrape.element.{Parser, Element, HtmlElement}

trait Browser[S] {
  implicit def browserToS(b: Browser[S]): S = b.asInstanceOf[S]

  protected val driver: WebDriver // make it protected because WebDriver is mutable
  lazy val parser = new Parser(driver)
  def url: String
  def proxy: Option[ProxyServer]
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
  
  /************Javascript***********/
  def executeJs(code: String): S = {
    executeJsWithResult(code)
    this
  }
  
  def executeJsWithResult(code: String): Any = {
    driver.asInstanceOf[JavascriptExecutor].executeScript(code)
  }

  /************Parse***********/
  def parse(f: Parser => Any): S = {
    implicit val _driver = driver
    f(parser)
    this
  }
  def tryExtract(f: Parser => Option[HtmlElement]): Option[Element] = {
    f(parser).map(_.toElement)
  }
  def extract(f: Parser => HtmlElement): Element = {
    f(parser).toElement
  }
  def extracts(f: Parser => Iterable[HtmlElement]): Iterable[Element] = {
    f(parser).map(_.toElement)
  }
  def tryExtractWindow(f: Parser => Option[Window]):Option[Window] = {
    f(parser)
  }
  def extractWindow(f: Parser => Window):Window = {
    f(parser)
  }
  def extractWindows(f: Parser => Iterable[Window]): Iterable[Window] = {
    f(parser)
  }

  def title = driver.getTitle
  def body = driver.getPageSource
  def currentUrl = driver.getCurrentUrl
  def quit = driver.quit
}
