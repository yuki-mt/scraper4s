# Scraper4s: Scraping Tool in Scala

Wrapper of Selenuim WebDriver

## Installation

in buid.sbt

```
resolvers += "scraper4s" at "https://yuki-mt.github.io/scraper4s"
libraryDependencies += "com.yukimt" % "scraper4s_2.11" % "1.0"
```

If you use PhantomBrowser,

```
$ npm install phantom phantomjs -g
```

## Quick Examples
### Form, Screenshot, UserAgent
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.FormElement
import com.yukimt.scrape.element.ElementMethod._
import com.yukimt.scrape.element.ParserMethod._
import com.yukimt.scrape.{UserAgent, Device, BrowserType, ViewPoint}

object Boot extends App {
  //Go to login page
  val browser = new PhantomBrowser(
    "https://trello.com/login",
    UserAgent(Device.IPhone, BrowserType.Safari),
    customHeaders = Map("X-My-Header"->"Yeah") // not necessary
  )

  browser.parse{ parser =>
    // get textbox (<input> tag) for username
    val username = parser >> css("#user")
    
    //type "my-name"
    username.typing("my-name")
    
    //get textbox for password and type "my-password"
    (parser >> css("#password")).typing("my-password")
    
    //get form tag that contains username textbox, and submit
    (username >> parentForm).submit
  }
  
  //wait until the A tag appears (timeout is 10 seconds)
  browser.waitForElement("a[class='quiet-button u-float-left']", 10)
    // take a screenshot
    .takeScreenshot("/tmp/screenshots/test.png", ViewPoint.SmartPhone)
    // close the browser
    .quit
}

```

### Use multiple windows, extract elements
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ElementMethod._
import com.yukimt.scrape.element.ParserMethod._
import com.yukimt.scrape.{UserAgent, Device, BrowserType}

object Boot extends App {
  // go to Yahoo top page
  val browser = new PhantomBrowser("https://www.yahoo.com/")

  browser.parse{ parser =>
  	//search with "Scala" keyword
    val searchBox = parser >> css("#uh-search-box")
    searchBox.typing("Scala")
    (searchBox >> parentForm).submit
    
    // wait until innerText of <title> tag contains "Scala"
  }.waitForTitle("Scala", 10).parse{ parser => 
    // get first 10 search result <a> tags
    val links = (parser >>> css("a.ac-algo")).map(_ >> asATag).take(10)
    
    val firstLink = links.head
    val otherLinks = links.drop(1)
 
    // open the 9 links (search result) in a new window
    otherLinks.foreach(_.openInNewWindow)
    
    //open the first link in the current window
    firstLink.click
  }.waitForElement("h1", 10) // wait until h1 tag shows up

  // get innerText of <h1> tag in each window if exists
  val h1 = browser.getFromAllWindows{ browser =>
    browser.tryExtractElement(_ >?> css("h1")).map(_.text)
  }.flatten
  
  //Output shoul be like Seq("Scala - Official Site", "Scala Tutorial", ..)
  println(h1)
  
  browser.quit
}
```

## Component List of Scraper4s (details of each compoent are following)
- Browser
	- UnitBrowser
	- PhantomBrowser
- Parser
- Element
	- HtmlElement
		- FormElement
		- ATagElement
	- Element

## Browser
Be careful. This is mutable.
### Common Functionalities
- Wait for an element to appear
- Wait for title to contain a certain string
- Set custom request header
- Controll multiple windows
- Extract HTML Element (as Element case class)
- Edit cookies
- Pass Basic Authentication
- Change UserAgent
- Execute Javascript and get result

[Detail of Browser trait](https://github.com/yuki-mt/scraper4s/tree/master/docs/browser.md)


### UnitBrowser (extends Browser trait)
Based on HtmlUnitDriver in Selenium Web Driver
#### pros
- Faster
- Able to get Response Header (such as Status Code)

#### cons
- Does not support screenshots
- Does not work in some page due to ScriptException

#### How to get response header
```
import com.yukimt.scrape.browser.UnitBrowser

val browser = new UnitBrowser("http://....")
val headers: Map[String, String] = browser.responseHeaders
val statusCode: Int = browser.statusCode
```

### PhantomBrowser (extends Browser trait)
Based on PhantomJSDriver in Selenium Web Driver
#### pros
- Supports screenshots

#### cons
- Need to install PhantomJS in advance
- Not able to get Response Header
- A bit slower

#### How to take a screenshot
```
import com.yukimt.scrape.browser.PhantomBrowser

val browser = new PhantomBrowser("http://....")

//take a screenshot (width: 700px, hight: 500px)
//file path needs to be absolute path
browser.takeScreenshot("file path", ViewPoint(700, 500))
```

#### ViewPoint
```
import com.yukimt.scrape.ViewPoint

//You can set width and height (unit is px)
val v: ViewPoint = ViewPoint(320, 480)

//You can also use prepared ViewPoint
val sp: ViewPoint = ViewPoint.SmartPhone
val tablet: ViewPoint = ViewPoint.Tablet
val pc: ViewPoint = ViewPoint.PC
```

## Get HtmlElement
### Basic Operations

- `>> [ParserMethod or ElementMethod]` : get the first element
- `>?> [ParserMethod or ElementMethod]` : get the first element as Option[HtmlElement]
- `>>> [ParserMethod or ElementMethod]` : get the all elements

#### e.g.
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod._
import com.yukimt.scrape.element.HtmlElement

val browser = new PhantomBrowser("http://...")
browser.parse{ parser =>
  // If not found, throw an exception
  val e1: HtmlElement = parser >> css("ul li")
  
  // If not found, return None
  val e2: Option[HtmlElement] = parser >?> css("ul li")
  
  // return all "ul li" elements
  val e2: Seq[HtmlElement] = parser >>> css("ul li")
}
```

### Parser gets HtmlElemt
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod._
import org.openqa.selenium.By
import com.yukimt.scrape.element.HtmlElement

val browser = new PhantomBrowser("http://...")
browser.parse{ parser =>
  val e1: HtmlElement = parser >> css("css query here")
  val e2: HtmlElement = parser >> by(By.id("id-name"))
}
```
[How to use 'By'](https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/By.html)

### HtmlElement gets HtmlElement 
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod.css
import com.yukimt.scrape.element.ElementMethod._
import com.yukimt.scrape.element.{HtmlElement, Element}

val browser = new PhantomBrowser("http://...")
val parent: Element = browser.extactElement{ parser =>
  val e: HtmlElement = parser >> css("css query here")
  val elements: Seq[HtmlElement] = e >>> find("css query here") 
  val children = e >>> children
  val firstChild = e >> children
  val parent: HtmlElement e >> parent
  parent
}

val tagName: String = parent.tag
val attributes: Map[String, String] = parent.attributes
val innerText: String = parent.text
```

[Detail of HtmlElement, Element](https://github.com/yuki-mt/scraper4s/tree/master/docs/element.md)

## Contributing
Always welcome for your contribution

## License & Authors
Author:: @yuki-mt
License:: MIT
