package com.yukimt.scrape.element

import org.openqa.selenium.{By, WebElement, WebDriver, JavascriptExecutor}
import collection.JavaConversions._
import org.json4s.JObject
import org.json4s.jackson.JsonMethods

trait HtmlElementLike {
  protected def element: WebElement
  protected def driver: WebDriver

  def getFirst[S](method:ElementMethod[S]): S = method(element, driver).head
  def tryFirst[S](method:ElementMethod[S]): Option[S] = {
    try {
      Option(method(element, driver).head)
    } catch {
      case e: Throwable =>
        println(e.getMessage)
        None
    }
  }
  def getAll[S](method:ElementMethod[S]): Iterable[S] = {
    try {
      method(element, driver)
    } catch {
      case e:Throwable =>
        println(e.getMessage)
        Seq.empty
    }
  }
  def >>[S](method:ElementMethod[S]): S = getFirst(method)
  def >?>[S](method:ElementMethod[S]): Option[S] = tryFirst(method)
  def >>>[S](method:ElementMethod[S]): Iterable[S] = getAll(method)

  //about form parts
  def checked = tryFirst(ElementMethod.attr("checked")).contains("true")
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
    Element(element.getTagName, attributes, element.getText)
  }
  

  def click() = element.click
}


class HtmlElement(protected val element: WebElement, protected val driver: WebDriver)
  extends HtmlElementLike with InputElement
