package com.yukimt.scrape.element

import org.specs2.mutable.Specification
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{By, WebElement, WebDriver}
import ParserMethod._
import ElementMethod._

class ParserSpec extends Specification{
  sequential
  val driver = new HtmlUnitDriver
  driver.get("http://localhost:3000/view")
  val parser = new Parser(driver)

  "Parser" should {
    "find Elements 'By'" in {
      (parser >> by(By.xpath("//h1"))) >> innerText === "H1 tag here"
      (parser >>> by(By.xpath("//li"))).map(_ >> innerText) === Seq("list1", "list2", "list3")
    }
    "find Elements By CSS Query" in {
      (parser >> css(".c")) >> innerText === "list1"
      (parser >>> css("input[name='fruit']")).map(_ >> attr("value")) === Seq("a", "o", "l")
    }
  }
}
