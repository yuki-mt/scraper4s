package com.yukimt.scrape
package element

import org.openqa.selenium.{By, WebDriver}
import collection.JavaConversions._
import Implicit._

class Parser(driver: WebDriver) {
  implicit val _driver = driver
  def findElement(by: By): Option[HtmlElement] = {
    Option(driver.findElement(by))
  }
  def findElements(by: By): Seq[HtmlElement] = {
    driver.findElements(by).map(e => e:HtmlElement)
  }
  def findElement(cssQuery: String): Option[HtmlElement] = {
    Option(driver.findElement(By.cssSelector(cssQuery)))
  }
  def findElements(cssQuery: String): Seq[HtmlElement] = {
    driver.findElements(By.cssSelector(cssQuery)).map(e => e:HtmlElement)
  }
}
