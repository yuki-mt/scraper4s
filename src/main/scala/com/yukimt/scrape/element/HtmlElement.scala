package com.yukimt.scrape.element

import org.openqa.selenium.{By, WebElement, WebDriver, JavascriptExecutor}
import org.openqa.selenium.{NoSuchElementException, InvalidSelectorException}
import collection.JavaConversions._
import org.openqa.selenium.support.ui.Select
import org.json4s.JObject
import org.json4s.jackson.JsonMethods

object Implicit {
  implicit def webElementToHtmlElement(e: WebElement)(implicit driver: WebDriver):HtmlElement = new HtmlElement(e)
}
trait HtmlElementLike {
  import Implicit._
  protected def element: WebElement
  protected implicit def driver: WebDriver
  def parent: Option[HtmlElement] = {
    try {
      Some(element.findElement(By.xpath(".//parent::node()")))
    } catch {
      case e:InvalidSelectorException => None
    }
  }

  def children: Seq[HtmlElement] = {
    element.findElements(By.xpath(".//*")).map(e => e:HtmlElement)
  }
  def siblings: Seq[HtmlElement] = {
    Seq.concat(
      element.findElements(By.xpath("preceding-sibling::*")),
      element.findElements(By.xpath("following-sibling::*"))
    ).map(e => e:HtmlElement)
  }
  def nextSibling: Option[HtmlElement] = nextSibling(1)
  def nextSibling(n:Int): Option[HtmlElement] = {
    try {
      Some(element.findElement(By.xpath(s"following-sibling::*[$n]")))
    } catch {
      case e: NoSuchElementException => None
    }
  }

  def beforeSibling: Option[HtmlElement] = beforeSibling(1)
  def beforeSibling(n: Int): Option[HtmlElement] = {
    try {
      Some(element.findElement(By.xpath(s"preceding-sibling::*[$n]")))
    } catch {
      case e: NoSuchElementException => None
    }
  } 

  //about form parts
  def checked = attr("checked").contains("true")
  protected def selectTag = {
    if(tagName != "select")
      throw new InvalidSelectorException(s"$tagName tag cannot execute 'select' method")
    new Select(element)
  }
  def selectedElement = new HtmlElement(selectTag.getFirstSelectedOption)

  def text: String = element.getText
  def attr(key: String): Option[String] = Option(element.getAttribute(key))
  def tagName: String = element.getTagName

  def appendElement(tag: String, attrs: Map[String, String], text: Option[String]): Unit = {
    var attrCode = attrs.map{
      case (key, value) =>
        s"newElement.setAttribute('$key', '$value');"
    }.mkString("\n|  ")
    var textCode = text.fold("")(t => s"newElement.innerHTML = '$t';")
    val code = 
      s"""var newElement = document.createElement("$tag");
         |$attrCode
         |$textCode
         |arguments[0].appendChild(newElement);""".stripMargin
    driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
  }
  
  def setAttribute(name: String, value: String): Unit = {
    val code = s"arguments[0].setAttribute('$name', '$value');"
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

  def toElement = {
    val code = 
      s"""var element = arguments[0].cloneNode(true);
         |var items = {};
         |var att = element.attributes;
         |for(var i = 0; i < att.length; i++){
         |  if(att[i].value)
         |    items[att[i].name + ""] = att[i].value + "";
         |}
         |return JSON.stringify(items);""".stripMargin
    val result = driver.asInstanceOf[JavascriptExecutor].executeScript(code, element)
    val attributes = JsonMethods.parse(result.toString).asInstanceOf[JObject].values.asInstanceOf[Map[String, String]]
    Element(tagName, attributes, text)
  }
  

  def click() = element.click
}

class HtmlElement(protected val element: WebElement)
  (protected implicit val driver: WebDriver)
  extends HtmlElementLike with InputElement
