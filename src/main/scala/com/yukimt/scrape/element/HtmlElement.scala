package com.yukimt.scrape.element

import org.openqa.selenium.{By, WebElement, WebDriver, JavascriptExecutor}
import collection.JavaConversions._

object Implicit {
  implicit def webElementToHtmlElement(e: WebElement):HtmlElement = new HtmlElement(e)
}
/**
 * add writable functionalities
 */
trait HtmlElementLike extends ElementLike[HtmlElement]{
  implicit def webElementToHtmlElement(e: WebElement):HtmlElement = Implicit.webElementToHtmlElement(e)

  def parentForm: Option[FormElement] = 
    tryToGetParantForm(this).flatMap(_.asFormElement)

  protected def tryToGetParantForm(e: HtmlElementLike):Option[HtmlElement] = {
    this.parent.flatMap{ p =>
      if(p.tagName == "form") Some(p) 
      else tryToGetParantForm(p)
    }
  }

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
  
  def asFormElement: Option[FormElement] = {
    if (element.getTagName == "form") Some(new FormElement(element))
    else None
  }

  def asATagElement: Option[ATagElement] = {
    if (element.getTagName == "a") Some(new ATagElement(element))
    else None
  }

  def toElement = new Element(element)

  def insert(value: String) = element.sendKeys(value)
  def click() = element.click
}

class HtmlElement(val element: WebElement) extends HtmlElementLike
