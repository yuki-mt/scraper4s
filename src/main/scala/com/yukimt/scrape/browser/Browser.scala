package com.yukimt.scrape
package browser

import collection.JavaConversions._
import scala.concurrent.duration.FiniteDuration
import org.openqa.selenium.{WebDriver, Cookie, By, JavascriptExecutor}
import com.yukimt.scrape.element.{Parser, Element, HtmlElement}
import org.json4s.JObject
import org.json4s.jackson.JsonMethods

trait Browser[S] {
  implicit def browserToS(b: Browser[S]): S = b.asInstanceOf[S]

  protected val driver: WebDriver // make it protected because WebDriver is mutable
  lazy val parser = new Parser(driver)
  def url: String
  def userAgent: UserAgent
  def customHeaders: Map[String, String]
  
  /************Cookie***********/
  def addCookie(key: String, value: String): S 
  def removeCookie(key: String): S 
  def clearCookie: S 
  def cookies:Map[String, String] 
  def cookie(key: String): Option[String] 
  
  /************Javascript***********/
  def js(code: String): S = {
    getFromJs(code)
    this
  }
  def getFromJs(code: String): Any = {
    driver.asInstanceOf[JavascriptExecutor].executeScript(code)
  }
  def getListFromJs(code: String): Seq[Any] = {
    driver.asInstanceOf[JavascriptExecutor].executeScript(code).asInstanceOf[java.util.ArrayList[Any]]
  }
  def getObjectFromJs(code: String): Map[String, Any] = {
    driver.asInstanceOf[JavascriptExecutor].executeScript(code)
      .asInstanceOf[java.util.Map[String, Any]].toMap
  }

  /************Parse***********/
  def parse(f: Parser => Any): S = {
    implicit val _driver = driver
    f(parser)
    this
  }
  def tryExtractElement(f: Parser => Option[HtmlElement]): Option[Element] = {
    f(parser).map(_.toElement)
  }
  def extractElement(f: Parser => HtmlElement): Element = {
    f(parser).toElement
  }
  def extractElements(f: Parser => Iterable[HtmlElement]): Iterable[Element] = {
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

  /************Popup***********/
  def isAlertPresent = {
    try {
      driver.switchTo.alert
      driver.switchTo.defaultContent
      true
    } catch {
      case e: Throwable => false
    }
  }
  def closeAlert = {
    if(isAlertPresent) {
      driver.switchTo.alert.accept
      driver.switchTo.defaultContent
    }
    this
  }
  def acceptConfirm = closeAlert 
  def declineConfirm = {
    if(isAlertPresent) {
      driver.switchTo.alert.dismiss
      driver.switchTo.defaultContent
    }
    this
  }
  def typeToPrompt(msg: String) = {
    if(isAlertPresent) {
      driver.switchTo.alert.sendKeys(msg)
      driver.switchTo.defaultContent
    }
    this
  }

  def title = driver.getTitle
  def body = driver.getPageSource
  def currentUrl = driver.getCurrentUrl
  def quit = driver.quit
}
