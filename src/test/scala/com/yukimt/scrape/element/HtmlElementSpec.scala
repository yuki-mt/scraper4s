package com.yukimt.scrape.element

import org.specs2.mutable.Specification
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.InvalidSelectorException
import ParserMethod._
import ElementMethod._

class HtmlElementSpec extends Specification{
  sequential
  implicit val driver = new HtmlUnitDriver(true)
  driver.get("http://localhost:3000/view")
  val parser = new Parser(driver)
  val element = parser >> css("input[name='text']")
  val ul = parser >> css("ul")

  "HtmlElement" should {
    "to element" in {
      var attr = Map("type"->"text", "value"->"default", "name"->"text")
      element.toElement === Element("input", attr, "")
    }

    "text, attr, tag name" in {
      ul >> innerText === "list1list2list3"
      ul >?> attr("data-sample") === Some("ul-list")
      ul >?> attr("ddddata-sample") === None
      ul >> tagName === "ul"
    }

    "parent" in {
      val p = ul >> parent
      p >> tagName === "body"
      val grand = p >> parent
      grand >> tagName === "html"
      grand >?> parent === None
    }

    "children" in {
      (ul >>> children).map(_ >> innerText) === Seq("list1", "list2", "list3")
      (ul >> children) >>> children === Seq.empty
    }

    "siblings" in {
      (ul >> nextSibling) >> tagName === "a"
      (ul >> nextSibling(2)) >> tagName === "form"
      (ul >> beforeSibling) >> tagName === "h1"
      ul >?> beforeSibling(2) === None
      (ul >>> siblings).map(_ >> tagName) === Seq("h1", "a", "form", "script")
      val html = parser >> css("html")
      html >>> siblings === Seq.empty
    }

    "parent form" in {
      (element >> parentForm) >> attr("method") === "POST"
      ((parser >> css("input[type='checkbox']")) >> parentForm) >> attr("method") === "POST"
      (element >> parent) >?> parentForm === None
    }

    "asForm, asATag" in {
      element >?> asForm === None
      element >?> asATag === None
      ((element >> parent) >?> asForm).isDefined === true
      ((parser >> css("a")) >?> asATag).isDefined === true
    }

    "append element" in {
      ul.appendElement("li", Map("class"->"new"), Some("new list"))
      val c = ul >>> children
      c.map(_ >> innerText) === Seq("list1", "list2", "list3", "new list")
      c.flatMap(_ >?> attr("class")) === Seq("c", "new")
    }

    "typing" in {
      element >> attr("value") === "default"
      element.typing(" custom")
      element >> attr("value") === "default custom"
      element.clear
      element.typing("aaa")
      element >> attr("value") === "aaa"

      //to textarea
      val textarea = parser >> css("textarea")
      textarea.typing("ss")
      textarea >> attr("value") === "ss"
      
      //to not text tag
      val div = parser >> css("div")
      div.typing("ss")
      div >> attr("value") === ""
    }

    "select" in {
      element.select("ddd") must throwA(new InvalidSelectorException(s"input tag is not supported 'select' method"))
      val select = parser >> css("select")
      (select >> selectedElement) >> innerText === "a"
      select.select("c")
      (select >> selectedElement) >> innerText === "c"
    }

    "check" in {
      val checkbox = parser >> css("input[type='checkbox']")
      checkbox.checked === false
      checkbox.click
      checkbox.checked === true

      val radios = parser >>> css("input[type='radio']")
      radios.head.checked === true
      radios.last.checked === false
      radios.last.click
      radios.last.checked === true
      radios.head.checked === false
    }
  }
}
