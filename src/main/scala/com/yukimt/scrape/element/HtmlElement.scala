package com.yukimt.scrape.element

import org.openqa.selenium.{By, WebElement, WebDriver, JavascriptExecutor}
import collection.JavaConversions._

object Implicit {
  implicit def webElementToHtmlElement(e: WebElement):HtmlElement = new HtmlElement(e)
}
trait HtmlElementLike {
  import Implicit._

  def element: WebElement

  def getParent(): Option[HtmlElement] = Option(
    element.findElement(By.xpath(".//parent::node()"))
  )
  def getChildren(): Seq[HtmlElement] = {
    element.findElements(By.xpath(".//*")).map(e => e:HtmlElement)
  }
  def getChildren(filter: By): Seq[HtmlElement] = {
    element.findElements(By.xpath(".//*")).map(e => e:HtmlElement)

  }
  def getSiblings(): Seq[HtmlElementLike] = {
    Seq.concat(
      element.findElements(By.xpath("preceding-sibling::*")),
      element.findElements(By.xpath(s"following-sibling::*"))
    ).map(e => e:HtmlElement)
  }
  def getSiblings(filter: By): Seq[HtmlElement] = {
    Seq.concat(
      element.findElements(By.xpath("preceding-sibling::*")),
      element.findElements(By.xpath(s"following-sibling::*"))
    ).map(e => e:HtmlElement)
  }
  def getNextSibling(n: Int = 1): Option[HtmlElement] = Option(
    element.findElement(By.xpath(s"following-sibling::*[$n]"))
  )
  def getBeforeSibling(n: Int = 1): Option[HtmlElement] = Option(
    element.findElement(By.xpath(s"preceding-sibling::*[$n]"))
  ) 
  
  def appendElement(tag: String, attrs: Map[String, String], text: Option[String])(implicit driver: WebDriver): Unit = {
    var attrCode = attrs.map{
      case (key, value) =>
        s"newElement.setAttribute('$key', '$value');"
    }.mkString("\n|  ")
    var textCode = text.fold("")(t => s"newElement.innerHtml = '$t';")
    val code = 
      s"""(function(element){
         |  var newElement = document.createElement("$tag");
         |  $attrCode
         |  $textCode
         |  element.appendChild(newElement);
         |}(arguments[0])""".stripMargin
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }

  def getText(): String = element.getText
  def getAttribute(key: String): Option[String] = Option(element.getAttribute(key))
  def getTagName(): String = element.getTagName
  def insert(value: String) = element.sendKeys(value)
  def click() = element.click
  def submit() = element.submit
}

class HtmlElement(val element: WebElement) extends HtmlElementLike {
  def asFormElement: Option[FormElement] = {
    if (element.getTagName == "form") Some(new FormElement(element))
    else None
  }

  def asATagElement: Option[ATagElement] = {
    if (element.getTagName == "a") Some(new ATagElement(element))
    else None
  }
}
