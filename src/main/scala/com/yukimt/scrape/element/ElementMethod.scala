package com.yukimt.scrape.element

import org.openqa.selenium.{By, WebElement, WebDriver, JavascriptExecutor}
import collection.JavaConversions._
import org.openqa.selenium.support.ui.Select
import org.json4s.JObject
import org.json4s.jackson.JsonMethods

object ElementMethod{
  def find(cssQuery: String):ElementMethod[HtmlElement] = {
    val method = (query: String, element: WebElement, driver: WebDriver) => 
      element.findElements(By.cssSelector(cssQuery)).map(e => new HtmlElement(e, driver))
    method(cssQuery, _, _)
  }

  val parent:ElementMethod[HtmlElement] = (element: WebElement, driver: WebDriver) => 
    Seq(new HtmlElement(element.findElement(By.xpath(".//parent::node()")), driver))

  val children:ElementMethod[HtmlElement] = (element: WebElement, driver: WebDriver) => 
    element.findElements(By.xpath(".//*")).map(e => new HtmlElement(e, driver))

  val siblings:ElementMethod[HtmlElement] = (element: WebElement, driver: WebDriver) => {
    Seq.concat(
      element.findElements(By.xpath("preceding-sibling::*")),
      element.findElements(By.xpath("following-sibling::*"))
    ).map(e => new HtmlElement(e, driver))
  }

  def nextSibling:ElementMethod[HtmlElement] = nextSibling(1)
  def nextSibling(n:Int):ElementMethod[HtmlElement] = {
    val method = (n: Int, element:WebElement, driver: WebDriver) =>
      Seq(new HtmlElement(element.findElement(By.xpath(s"following-sibling::*[$n]")), driver))
    method(n, _, _)
  }

  def beforeSibling:ElementMethod[HtmlElement] = beforeSibling(1)
  def beforeSibling(n: Int):ElementMethod[HtmlElement] = {
    val method = (n: Int, element:WebElement, driver: WebDriver) =>
      Seq(new HtmlElement(element.findElement(By.xpath(s"preceding-sibling::*[$n]")), driver))
    method(n, _, _)
  } 

  def asForm:ElementMethod[FormElement] = (element: WebElement, driver: WebDriver) =>{
    if (element.getTagName == "form") Seq(new FormElement(element, driver))
    else Seq.empty
  }

  def asATag:ElementMethod[ATagElement] = (element: WebElement, driver: WebDriver) =>{
    if (element.getTagName == "a") Seq(new ATagElement(element, driver))
    else Seq.empty
  }

  val parentForm:ElementMethod[FormElement] = (element: WebElement, driver: WebDriver) =>
    tryParantForm(element).map(e => new FormElement(e, driver)).toSeq

  private def tryParantForm(element: WebElement):Option[WebElement] = {
    try {
      val parent = element.findElement(By.xpath(".//parent::node()"))
      if(parent.getTagName == "form") Some(parent)
      else tryParantForm(parent)
    } catch {
      case _:Throwable => None
    }
  }

  def attr(key: String):ElementMethod[String] = {
    val method = (name: String, element: WebElement, driver: WebDriver) => Seq(element.getAttribute(name))
    method(key, _, _)
  }

  val selectedElement:ElementMethod[HtmlElement] = 
    (element: WebElement, driver: WebDriver) => 
      Seq(new HtmlElement(new Select(element).getFirstSelectedOption, driver))

  val innerText:ElementMethod[String] = 
    (element: WebElement, driver: WebDriver) => Seq(element.getText)

  val tagName:ElementMethod[String] = 
    (element: WebElement, driver: WebDriver) => Seq(element.getTagName)

  val url:ElementMethod[String] = 
    (element: WebElement, driver: WebDriver) => {
      Seq(element.getTagName match {
        case "form" => element.getAttribute("action")
        case "a" => element.getAttribute("href")
        case _ => throw new Exception("Only 'form' and 'a' tag support 'url' method")
      })
    }
}
