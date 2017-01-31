package com.yukimt.scrape
package element

import org.openqa.selenium.{By, WebDriver}
import collection.JavaConversions._

class Parser(driver: WebDriver) {
  def getFirst(method:ParserMethod): HtmlElement = method(driver).head
  def tryFirst(method:ParserMethod): Option[HtmlElement] = {
    try {
      method(driver).headOption
    } catch {
      case e: Throwable =>
        println(e.getMessage)
        None
    }
  }
  def getAll(method:ParserMethod): Iterable[HtmlElement] = method(driver)
  def >>(method:ParserMethod) = getFirst(method)
  def >?>(method:ParserMethod) = tryFirst(method)
  def >>>(method:ParserMethod) = getAll(method)
}

object ParserMethod{
  def by(b: By): ParserMethod = {
    val method = (_b: By, driver: WebDriver) =>
      driver.findElements(_b).map(e => new HtmlElement(e, driver))
    method(b, _)
  }

  def css(cssQuery: String): ParserMethod = {
    val method = (query: String, driver: WebDriver) =>
      driver.findElements(By.cssSelector(query)).map(e => new HtmlElement(e, driver))
    method(cssQuery, _)
  }
}
