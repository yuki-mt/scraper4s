package com.yukimt.scrape.element

import org.specs2.mutable.Specification
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.json4s.JObject
import org.json4s.jackson.JsonMethods

class FormElementSpec extends Specification{
  sequential
  implicit val driver = new HtmlUnitDriver(true)
  driver.get("http://localhost:3000/view")
  val parser = new Parser(driver)
  var element = parser.findElement("form").get.asFormElement.get

  "FormElement" should {
    "set query string" in {
      element.setQueryString(Map("a"->"b", "c"->"d"))
      element.url === Some("/form?a=b&c=d")
    }

    "submit" in {
      driver.getTitle === "Express Sample Title"
      element.submit
      driver.getTitle === ""
    }

    "add form data" in {
      driver.navigate.back
      element = parser.findElement("form").get.asFormElement.get
      driver.getTitle === "Express Sample Title"
      element.addFormData("abc", "def")
      element.submit
      driver.getTitle === ""
      val result = JsonMethods.parse(driver.getPageSource.replaceAll("<.+>", "")).asInstanceOf[JObject].values.asInstanceOf[Map[String, String]]
      result === Map("text"->"default","textarea"->"","fruit"->"a","select"->"select1","abc"->"def")
    }

    "set uri" in {
      driver.navigate.back
      element = parser.findElement("form").get.asFormElement.get
      element.setUri("/abc")
      element.url === Some("/abc")
    }
  }
}
