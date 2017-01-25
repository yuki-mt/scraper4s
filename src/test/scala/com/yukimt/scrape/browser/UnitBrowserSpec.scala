package com.yukimt.scrape
package browser

import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import org.json4s.JObject
import org.json4s.jackson.JsonMethods
import scala.concurrent.duration._

//TODO: window, wait, history, addHeader, proxy test is not done yet
class UnitBrowserSpec extends Specification with NoTimeConversions{
  sequential

  "UnitBrowser" should {
    "set headers" in {
      val browser = new UnitBrowser(
        "http://localhost:3000",
        userAgent = new UserAgent(Device.Mac, BrowserType.Firefox),
        customHeaders = Map("X-My-Header" -> "hogefuga")
      )

      val result = JsonMethods.parse(browser.getBody.replaceAll("<.+>", "")).asInstanceOf[JObject].values
      val headers = result("headers").asInstanceOf[Map[String, String]]
      
      headers("user-agent") === "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0"
      headers("x-my-header") === "hogefuga"
    }

    "get response header" in {
      val browser = new UnitBrowser("http://localhost:3000/notfound")
      browser.getResponseHeader()("Content-Type") === "text/html; charset=utf-8"
      browser.getStatusCode === Some(404)
    }

    "execute javascript" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      browser.getJsExecutionResult("return navigator.cookieEnabled;") === true
      val result = browser.executeJs("myVariable += 10;").getJsExecutionResult("return myVariable;")
      result === 130
    }
    
    "cookie" in {
      val browser = new UnitBrowser("http://localhost:3000")

      browser.addCookie("hoge", "fuga")
      browser.addCookie("scrape", "4s")
      browser.getCookies === Map("hoge"->"fuga", "scrape" -> "4s")
      browser.getJsExecutionResult("return document.cookie;") === "hoge=fuga; scrape=4s"
      browser.getCookie("hoge") === Some("fuga")
      browser.getCookie("hakushu") === None
      browser.clearCookie.getCookies === Map.empty
    }

    "get title and url" in {
      val browser = new UnitBrowser("http://localhost:3000/view")
      browser.getTitle === "Express Sample Title"
      browser.getCurrentUrl === "http://localhost:3000/view"
    }
  }
}
