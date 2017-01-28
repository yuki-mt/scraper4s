package com.yukimt.scrape.element

import org.specs2.mutable.Specification
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{By, WebElement, WebDriver}

class ParserSpec extends Specification{
  sequential
  val driver = new HtmlUnitDriver
  driver.get("http://localhost:3000/view")
  val parser = new Parser(driver)

  "Parser" should {
    "find Elements 'By'" in {
      parser.findElement(By.xpath("//h1")).get.text === "H1 tag here"
      parser.findElements(By.xpath("//li")).map(_.text) === Seq("list1", "list2", "list3")
    }
    "find Elements By CSS Query" in {
      parser.findElement(".c").get.text === "list1"
      parser.findElements("input[name='fruit']").flatMap(_.attr("value")) === Seq("a", "o", "l")
    }
  }
}
