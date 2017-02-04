#Browser in Scraper4s
General Information is [here](https://github.com/yuki-mt/scraper4s#browsers)

`PhantomBrowser` is used in following sample codes, but the sample codes works in all Browsers

## Initialization
### Basic Form
```
import com.yukimt.scrape.browser.PhantomBrowser

// Go to the yahoo.com page
val browser = new PhantomBrowser("https://www.yahoo.com")

// Ouptut "https://www.yahoo.com"
println(browser.currentUrl)

//close the window
browser.quit
```

### Set UserAgent, Custom HTTP Header, Basic Authentication
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.{UserAgent, BasicAuth, Device, BrowserType}

val browser = new PhantomBrowser(
	"https://www.yahoo.com",
	UserAgent(Device.Android, BrowserType.Firefox), // Set UserAgent
	Some(BasicAuth("my-username", "my-password"), // Set Basic Authentication username and password
	Map("X-My-Header", "Scalaaa") // Add custom HTTP Header to your HTTP Request
)
```

#### UserAgent
```
import com.yukimt.scrape.{UserAgent, BasicAuth, Device, BrowserType}

//You can get UserAgent with Device and BrowserType
val u1: UserAgent = UserAgent(Device.Windows, BrowserType.Edge)

//You can also use raw string
val u2: UserAgent = UserAgent("Mozilla/5.0 (iPhone; U; ru; CPU iPhone OS 4_2_1 like Mac OS X; ru) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148a Safari/6533.18.5")
```

[What Device and BrowserType are prepared](https://github.com/yuki-mt/scraper4s/blob/master/src/main/scala/com/yukimt/scrape/UserAgent.scala#L33)

## Basic Information (Title, URL, Body data)
```
import com.yukimt.scrape.browser.PhantomBrowser

val browser = new PhantomBrowser("https://www.yahoo.com")

val url: String = browser.currentUrl // return "https://www.yahoo.com")
val title: String = browser.title // return text of <title> tag
val body: String = browser.body // return whole html source code
```
 
## Cookie
```
import com.yukimt.scrape.browser.PhantomBrowser

val browser = new PhantomBrowser("https://www.yahoo.com")
browser.addCookie("key", "value")
browser.removeCookie("key")
browser.clearCookie // clear all cookies
val currentCookies: Map[String, String] = browser.cookies
val currentCookie: Option[String] = browser.cookie("key")
```

`addCookie` , `removeCookie`, `clearCookie` returns self instance, so can use like chain methods

e.g.

```
browser
	.addCookie("key", "value")
	.clearCookies
	.addCookie("key2", "value2")
	.addCookie("key3", "value3")
	.removeCookie("key2")
	
// output is Map("key3"->"value3")
println(browser.cookies)
``` 

## Javascript
```
import com.yukimt.scrape.browser.PhantomBrowser

val browser = new PhantomBrowser("https://www.yahoo.com")
browser.js("js code here") // return self instance

val result: Any = browser.getFromJs("rerturn 'I am in script tag';")

val results: Seq[Any] = browser.getListFromJs("return [3, 'dd', true];")

val json: Map[Strin, Any] = browser.getObjectFromJs("return {key1: 'value', key2: 33};")
```

## Wait

- `waitForElement` : wait for an element to appear (not have to be displayed)
- `waitForTitle` : wait for \<title\> tag contains a certain string

```
import com.yukimt.scrape.browser.PhantomBrowser

val browser = new PhantomBrowser("https://www.yahoo.com")

val cssQuery = "script" // can be "#my-id", "ul li", etc.

val result = browser
	// wait until <script> tag appears (timeout is 10 seconds)
	.waitForElement(cssQuery, 10)
	// after waiting, execute javascript
	.getFromJs("return myVariable;")

// wait until text in <title> contains "my-title" (timeout is 5 seconds)
browser.waitForTitle("my-title", 5)

// after waiting, execute javascript
browser.js("myVariable += 10;")
```

## Get HTML Element
### Methods to get and controll HTML Element
- `parse` : return self instance
- `extractElement` : return Element case class
- `extractElements` : return Iterable[Element]
- `tryExtractElement` : return Option[Element]

[How to use Parser and HtmlElement class in detail](https://github.com/yuki-mt/scraper4s#get-htmlelement)

### Parse method
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod._


val browser = new PhantomBrowser("http://sample.com")
val url = browser.parse{parser =>
	(parser >> css("a[href='/search']")).click
}.waitForTitle("new page", 3).currentUrl

//output "http://sample.com/search"
println(url)
```

### Extract element method
`extractElement`, `extractElements`, and `tryExtractElement` convert from HtmlElement (class) to Element (case class) to prevent manipulating HTML element outside of extract methods.

(HtmlElement can trigger events such as 'click')

(Element is just case class containing tagName, innerText, and attributes)

```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod._
import com.yukimt.scrape.element.ElementMethod._
import com.yukimt.scrape.element.{Element, HtmlElement}

val browser = new PhantomBrowser("http://sample.com")
val e: Element = browser.extractElement{parser =>
	val aTag: HtmlElement = parser >> css("a[href='/search']")
	aTag // convert from HtmlElement to Element case class
}

//output "a"
println(e.tag)

val e2: Option[Element] = browser.extractElement{parser =>
	val aTag: Option[HtmlElement] = parser >?> css("a[href='/search']")
	aTag // convert from HtmlElement to Element case class
}

val e3: Iterable[Element] = browser.extractElement{parser =>
	val lists: Seq[HtmlElement] = parser >>> css("ul li")
	lists // convert from HtmlElement to Element case class
}
```

## History
```
import com.yukimt.scrape.browser.PhantomBrowser

val browser = new PhantomBrowser("http://sample.com")

/*
 * browser.parse(...) // go to some other pages
 */

browser.back // go back by 1 page
browser.forward // go forward by 1 page

browser.back(3) // go back by 3 page
browser.forward(2) // go forward by 2 page
```

`back`, `forward` methods reutrn self instance so you can do like the following

```
import com.yukimt.scrape.browser.PhantomBrowser

val browser = new PhantomBrowser("http://sample.com")

/*
 * browser.parse(...) // go to some other pages
 */
 
val url = browser.back(2).forward.currentUrl
```

## Controll Window

- `extractWindow` : return Window case class
- `extractWindows` : return Iterable[Window]
- `tryExtractWindow` : return Option[Window]

```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod._
import com.yukimt.scrape.element.ElementMethod._
import com.yukimt.scrape.Window
import com.yukimt.scrape.element.{ATagElement, HtmlElement}

val browser = new PhantomBrowser("http://sample.com")

val w1: Window = browser.currentWindow

val w2: Window = browser.extractWindow{parser =>
	val aTag: ATagElement = (parser >> css("a[href='/search']")) >> asATag
	val newWindow: Window = aTag.openInNewWindow
	newWindow
}

val url1 = browser.currentUrl // return "http://sample.com"
val url2 = browser.switch(w2).currentUrl // return "http://sample.com/search"

val url3 = browser.getFromWindow(w1){ win =>
    // code in this block is executed in Window 'w1'
	win.currentUrl
}
// url3 == url1
// still browser.currentWindow == w2

//'withWindow' returns self instance
val url4 = browser.withWindow(w1){ win =>
    // code in this block is executed in Window 'w1'
	win.currentUrl
}.currentUrl
// url4 == url2
// still browser.currentWindow == w2

// depulicate the current window
val w3 = browser.depulicateWindow()
// still browser.currentWindow == w2
// browser.currentUrl == browser.getFromWindow(w3)(_.currentUrl)

// If parameter is true, depulicate the current window and move to the new window
val w4 = browser.switch(w1).depulicateWindow(true) 
// browser.currentWindow == w4
// browser.currentUrl == browser.getFromWindow(w1)(_.currentUrl)

// js code is executed in all windows
// return self instance
browser.inAllWindows(win => win.js("console.log('yeah');")

// urls shoule be like Seq("http://sample.com", "http://sample.com", "http://sample.com/search", "http://sample.com/search")
val urls: Seq[String] = browser.getFromAllWindows(win => win.currentUrl)
```
