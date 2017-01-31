package com.yukimt.scrape
package browser

import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import org.json4s.JObject
import org.json4s.jackson.JsonMethods
import scala.concurrent.duration._
import element.ParserMethod._
import element.ElementMethod._

class UnitBrowserSpec extends Specification with NoTimeConversions{
  sequential

  "UnitBrowser" should {
    "set headers" in {
      val browser = new UnitBrowser(
        "http://localhost:3000",
        userAgent = UserAgent(Device.Mac, BrowserType.Firefox),
        customHeaders = Map("X-My-Header" -> "hogefuga")
      )

      val result = JsonMethods.parse(browser.body.replaceAll("<.+>", "")).asInstanceOf[JObject].values
      val headers = result("headers").asInstanceOf[Map[String, String]]
      
      headers("user-agent") === "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0"
      headers("x-my-header") === "hogefuga"
    }

    "get response header" in {
      val browser = new UnitBrowser("http://localhost:3000/notfound")
      browser.responseHeaders("Content-Type") === "text/html; charset=utf-8"
      browser.statusCode === 404
    }

    "execute javascript" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      browser.getFromJs("return navigator.cookieEnabled;") === true
      browser.getListFromJs("return ['2', 'dd'];") === Seq("2", "dd")
      browser.getObjectFromJs("return {a: '234', b:23};") === Map("a"->"234", "b"->23)
      val result = browser.js("myVariable += 10;").getFromJs("return myVariable;")
      result === 130
    }
    
    "cookie" in {
      val browser = new UnitBrowser("http://localhost:3000")

      browser.addCookie("hoge", "fuga")
      browser.addCookie("scrape", "4s")
      browser.cookies === Map("hoge"->"fuga", "scrape" -> "4s")
      browser.getFromJs("return document.cookie;") === "hoge=fuga; scrape=4s"
      browser.cookie("hoge") === Some("fuga")
      browser.cookie("hakushu") === None
      browser.clearCookie.cookies === Map.empty
    }

    "get title and url" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      browser.title === "Express Sample Title"
      browser.currentUrl === "http://localhost:3000/view"
    }

    "history" in {
      val url = "http://localhost:3000/"
      val viewUrl = url + "view"
      val browser = new UnitBrowser(viewUrl)
      browser.currentUrl === viewUrl
      browser.parse(p => (p >> css("a")).click).currentUrl === url
      browser.back.currentUrl === viewUrl
      browser.forward.currentUrl === url
    }

    "extract" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      val res = browser.extractElements{ p: element.Parser =>
        (p >> css("ul")) >>> children
      }
      res.map(_.text) === Seq("list1", "list2", "list3")
    }

    "get from all window" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      val res = browser.parse{ p =>
        val aTag = (p >> css("a")) >> asATag
        aTag.openInNewWindow
        aTag.openInNewWindow
      }.getFromAllWindows(_.title)
      res.filter(_ == "Express Sample Title").length === 1
      res.filter(_.isEmpty).length === 2
    }

    "get from window" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      val title = "Express Sample Title"
      val window = browser.extractWindow{ p =>
        val aTag = (p >> css("a")) >> asATag
        aTag.openInNewWindow
      }
      browser.title === title
      browser.getFromWindow(window)(_.title) === ""
      browser.title === title
    }

    "switch window" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      val title = "Express Sample Title"
      val window = browser.extractWindow{ p =>
        val aTag = (p >> css("a")) >> asATag
        aTag.openInNewWindow
      }
      browser.title === title
      browser.switch(window).title === ""
      browser.title === ""
    }

    "duplicate window" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      val currentWindow = browser.currentWindow
      val window = browser.depulicateWindow()
      currentWindow !== window
      browser.title === browser.getFromWindow(window)(_.title)
      browser.body === browser.getFromWindow(window)(_.body)
    }

//    "close alert" in {
//      val browser = new UnitBrowser("http://localhost:3000/view")
//      browser.isAlertPresent === false
//      browser.getFromjs("return msg;") === "nothing"
//      browser.parse(p => (p >>> css("button")).find(b => (b >> innertext) == "confirm").foreach(_.click))
//      browser.isAlertPresent === true
//      browser.getFromJs("return msg;") === "nothing"
//      browser.closeAlert.isAlertPresent === false
//      browser.currentUrl === "http://localhost:3000/"
//    }
  }
}
