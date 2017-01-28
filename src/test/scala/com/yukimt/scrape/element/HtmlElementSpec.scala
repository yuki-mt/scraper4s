package com.yukimt.scrape.element

import org.specs2.mutable.Specification
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.InvalidSelectorException

class HtmlElementSpec extends Specification{
  sequential
  implicit val driver = new HtmlUnitDriver(true)
  driver.get("http://localhost:3000/view")
  val parser = new Parser(driver)
  val element = parser.findElement("input[name='text']").get
  val ul = parser.findElement("ul").get

  "HtmlElement" should {
    "to element" in {
      var attr = Map("type"->"text", "value"->"default", "name"->"text")
      element.toElement === Element("input", attr, "")
    }

    "text, attr, tag name" in {
      ul.text === "list1list2list3"
      ul.attr("data-sample") === Some("ul-list")
      ul.attr("ddddata-sample") === None
      ul.tagName === "ul"
    }

    "parent" in {
      ul.parent.get.tagName === "body"
      val grand = 
        (for{
          parent <- ul.parent
          grandParent <- parent.parent
        } yield grandParent).get
      grand.tagName === "html"
      grand.parent === None
    }

    "children" in {
      ul.children.map(_.text) === Seq("list1", "list2", "list3")
      ul.children.head.children === Seq.empty
    }

    "siblings" in {
      ul.nextSibling.get.tagName === "a"
      ul.nextSibling(2).get.tagName === "form"
      ul.beforeSibling.get.tagName === "h1"
      ul.beforeSibling(2) === None
      ul.siblings.map(_.tagName) === Seq("h1", "a", "form", "script")
      val html = parser.findElement("html").get
      html.siblings === Seq.empty
    }

    "parent form" in {
      element.parentForm.get.attr("method") === Some("POST")
      parser.findElement("input[type='checkbox']").get.parentForm.get.attr("method") === Some("POST")
      element.parentForm.get.attr("method") === Some("POST")
      element.parent.get.parentForm === None
    }

    "asForm, asATag" in {
      element.asFormElement === None
      element.asATagElement === None
      element.parent.get.asFormElement.isDefined === true
      parser.findElement("a").get.asATagElement.isDefined === true
    }

    "append element" in {
      ul.appendElement("li", Map("class"->"new"), Some("new list"))
      val children = ul.children
      children.map(_.text) === Seq("list1", "list2", "list3", "new list")
      children.flatMap(_.attr("class")) === Seq("c", "new")
    }

    "typing" in {
      element.attr("value") === Some("default")
      element.typing(" custom")
      element.attr("value") === Some("default custom")
      element.clear
      element.typing("aaa")
      element.attr("value") === Some("aaa")

      //to textarea
      val textarea = parser.findElement("textarea").get
      textarea.typing("ss")
      textarea.attr("value") === Some("ss")
      
      //to not text tag
      val div = parser.findElement("div").get
      div.typing("ss")
      div.attr("value") === Some("")
    }

    "select" in {
      element.select("ddd") must throwA(new InvalidSelectorException(s"input tag cannot execute 'select' method"))
      val select = parser.findElement("select").get
      select.selectedElement.text === "a"
      select.select("c")
      select.selectedElement.text === "c"
    }

    "check" in {
      val checkbox = parser.findElement("input[type='checkbox']").get
      checkbox.checked === false
      checkbox.click
      checkbox.checked === true

      val radios = parser.findElements("input[type='radio']")
      radios.head.checked === true
      radios.last.checked === false
      radios.last.click
      radios.last.checked === true
      radios.head.checked === false
    }
  }
}
