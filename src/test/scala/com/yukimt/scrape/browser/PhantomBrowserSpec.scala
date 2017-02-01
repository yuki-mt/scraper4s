package com.yukimt.scrape
package browser

import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import org.json4s.JObject
import org.json4s.jackson.JsonMethods
import scala.concurrent.duration._
import element.ParserMethod._
import element.ElementMethod._

//TODO: proxy test is not done yet
class PhantomBrowserSpec extends Specification with NoTimeConversions{
  sequential

  "PhantomBrowser" should {
    "set headers" in {
      val browser = new PhantomBrowser(
        "http://localhost:3000",
        userAgent = UserAgent(Device.Mac, BrowserType.Firefox),
        customHeaders = Map("X-My-Header" -> "hogefuga")
      )

      val result = JsonMethods.parse(browser.body.replaceAll("<.+?>", "")).asInstanceOf[JObject].values
      val headers = result("headers").asInstanceOf[Map[String, String]]
      
      headers("user-agent") === "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0"
      headers("x-my-header") === "hogefuga"
    }

    "execute javascript" in {
      val browser = new PhantomBrowser("http://localhost:3000/view")
      browser.getFromJs("return navigator.cookieEnabled;") === true
      val result = browser.js("myVariable += 10;").getFromJs("return myVariable;")
      result === 130
    }
    
    "cookie" in {
      val browser = new PhantomBrowser("http://localhost:3000")

      browser.addCookie("hoge", "fuga")
      browser.addCookie("scrape", "4s")
      browser.cookies === Map("hoge"->"fuga", "scrape" -> "4s")
      browser.getFromJs("return document.cookie;") === "hoge=fuga; scrape=4s"
      browser.cookie("hoge") === Some("fuga")
      browser.cookie("hakushu") === None
      browser.clearCookie.cookies === Map.empty
    }

    "get title and url" in {
      val browser = new PhantomBrowser("http://localhost:3000/view")
      browser.title === "Express Sample Title"
      browser.currentUrl === "http://localhost:3000/view"
    }

    "history" in {
      val url = "http://localhost:3000/"
      val viewUrl = url + "view"
      val browser = new PhantomBrowser(viewUrl)
      browser.currentUrl === viewUrl
      browser.parse(p => (p >> css("a")).click).currentUrl === url
      browser.back.currentUrl === viewUrl
      browser.forward.currentUrl === url
    }

    "extract" in {
      val browser = new PhantomBrowser("http://localhost:3000/view")
      val res = browser.extractElements{ p: element.Parser =>
        (p >> css("ul")) >>> children
      }
      res.map(_.text) === Seq("list1", "list2", "list3")
    }

    "get from all window" in {
      val browser = new PhantomBrowser("http://localhost:3000/view")
      val res = browser.parse{ p =>
        val aTag = (p >> css("a")) >> asATag
        aTag.openInNewWindow
        aTag.openInNewWindow
      }.getFromAllWindows(_.title)
      res.filter(_ == "Express Sample Title").length === 1
      res.filter(_.isEmpty).length === 2
    }

    "get from window" in {
      val browser = new PhantomBrowser("http://localhost:3000/view")
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
      val browser = new PhantomBrowser("http://localhost:3000/view")
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
      val browser = new PhantomBrowser("http://localhost:3000/view")
      val currentWindow = browser.currentWindow
      val window = browser.depulicateWindow()
      currentWindow !== window
      browser.title === browser.getFromWindow(window)(_.title)
      browser.body === browser.getFromWindow(window)(_.body)
    }

//    "take screenshot" in {
//      val browser = new PhantomBrowser("http://localhost:3000/view")
//      browser.takeScreenshot("~/test.png", ViewPoint.PC)
//      1 === 1
//    }
  }
}
