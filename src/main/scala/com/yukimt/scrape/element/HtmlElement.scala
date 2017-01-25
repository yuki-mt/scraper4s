package com.yukimt.scrape.element

import org.openqa.selenium.{By, WebElement, WebDriver, JavascriptExecutor}
import collection.JavaConversions._

object Implicit {
  implicit def webElementToHtmlElement(e: WebElement):HtmlElement = new HtmlElement(e)
}
trait HtmlElementLike {
  import Implicit._

  def element: WebElement

  def parent: Option[HtmlElement] = Option(
    element.findElement(By.xpath(".//parent::node()"))
  )
  def parentForm: Option[FormElement] = Option(
    parent.flatMap{ p =>
      if(p.tagName == "form") Some(p) 
      else parantForm
    }
  )
  def children: Seq[HtmlElement] = {
    element.findElements(By.xpath(".//*")).map(e => e:HtmlElement)
  }
  def siblings: Seq[HtmlElement] = {
    Seq.concat(
      element.findElements(By.xpath("preceding-sibling::*")),
      element.findElements(By.xpath(s"following-sibling::*"))
    ).map(e => e:HtmlElement)
  }
  def siblings(filter: By): Seq[HtmlElement] = {
    Seq.concat(
      element.findElements(By.xpath("preceding-sibling::*")),
      element.findElements(By.xpath(s"following-sibling::*"))
    ).map(e => e:HtmlElement)
  }
  def nextSibling: Option[HtmlElement] = nextSibling(1)
  def nextSibling(n:Int): Option[HtmlElement] = Option(
    element.findElement(By.xpath(s"following-sibling::*[$n]"))
  )
  def beforeSibling: Option[HtmlElement] = beforeSibling(1)
  def beforeSibling(n: Int): Option[HtmlElement] = Option(
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

  def text: String = element.getText
  def attribute(key: String): Option[String] = Option(element.getAttribute(key))
  def tagName: String = element.getTagName
  def insert(value: String) = element.sendKeys(value)
  def click() = element.click
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
