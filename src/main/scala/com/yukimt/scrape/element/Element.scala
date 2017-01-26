package com.yukimt.scrape.element

import org.openqa.selenium.{By, WebElement, WebDriver, JavascriptExecutor}
import collection.JavaConversions._

trait ElementLike[T] {
  implicit def webElementToHtmlElement(e: WebElement):T

  def element: WebElement

  def parent: Option[T] = Option(
    element.findElement(By.xpath(".//parent::node()"))
  )

  def children: Seq[T] = {
    element.findElements(By.xpath(".//*")).map(e => e:T)
  }
  def siblings: Seq[T] = {
    Seq.concat(
      element.findElements(By.xpath("preceding-sibling::*")),
      element.findElements(By.xpath(s"following-sibling::*"))
    ).map(e => e:T)
  }
  def siblings(filter: By): Seq[T] = {
    Seq.concat(
      element.findElements(By.xpath("preceding-sibling::*")),
      element.findElements(By.xpath(s"following-sibling::*"))
    ).map(e => e:T)
  }
  def nextSibling: Option[T] = nextSibling(1)
  def nextSibling(n:Int): Option[T] = Option(
    element.findElement(By.xpath(s"following-sibling::*[$n]"))
  )
  def beforeSibling: Option[T] = beforeSibling(1)
  def beforeSibling(n: Int): Option[T] = Option(
    element.findElement(By.xpath(s"preceding-sibling::*[$n]"))
  ) 
  

  def text: String = element.getText
  def attr(key: String): Option[String] = Option(element.getAttribute(key))
  def tagName: String = element.getTagName
}

class Element(val element: WebElement) extends ElementLike[Element] {
  implicit def webElementToHtmlElement(e: WebElement):Element = new Element(e)
}
