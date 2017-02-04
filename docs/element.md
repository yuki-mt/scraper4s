# HtmlElement, Element in Screaper4s
General Information is [here](https://github.com/yuki-mt/scraper4s#get-htmlelement)

## ElementMethod
get various data from HTML Element

```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod.css
import com.yukimt.scrape.element.HtmlElement
import com.yukimt.scrape.element.ElementMethod._

val browser = new PhantomBrowser("http://...")
browser.parse{ parser =>
	val e: HtmlElement = parser >> css("input")
	
	// get attributes
	val type: String = e >> attr("type")
	val name: Option[String] = e >?> attr("name")
	
	//find element inside of 'form'
	val form: HtmlElement = parser >> css("form")
	val cssQuery = "input[type='text']"
	val textboxes: Seq[HtmlElement] = form >>> find(cssQuery)
	
	//get parant, children, siblings
	val cs: Seq[HtmlElement] = form >>> children
	val p: HtmlElement = form >> parent
	val ss: Seq[HtmlElement] = form >>> siblings
	val nextElement: HtmlElement = form >> nextSiblings
	val nextElementBy2: HtmlElement = form >> nextSiblings(2)
	val beforeElement: HtmlElement = form >> beforeSiblings
	val beforeElementBy3: HtmlElement = form >> beforeSiblings(3)
	
	// get <form> Element that has 'e' element
	val f: FormElement = e >> parentForm
	
	// convert HtmlElement to FormElement
	// (detail of FormElement is below)
	val ff: FormElement = form >> asForm
	
	// convert HtmlElement to ATagElement
	// (detail of FormElement is below)
	val a: ATagElement = (e >> find("a")) >> asATag
	
	//get selected <option> element
	val option: HtmlElement = (parser >> css("select")) >> selectedElement
	
	//get my-theme from <h1>my-theme</h1>
	val t: String = (parser >> css("h1")) >> innerText
	
	//get tag name
	val tag: String = (parser >> css("h1")) >> tagName // return "h1"
	
	//get url (only for <a> or <form>)
	val url = form >> url
}
```

## Change State of HtmlElement
in `select` part, assume there is \<select\> tag like 

```
<select>
	<option>a</option>
	<option>b</option>
	<option>c</option>
</select>
```

```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod.css
import com.yukimt.scrape.element.HtmlElement
import com.yukimt.scrape.element.ElementMethod._

val browser = new PhantomBrowser("http://...")
browser.parse{ parser =>
	val e: HtmlElement = parser >> css("input")
	
	//click the element
	e.click
	
	//type a word
	e.typing("a word")
	// e >> attr("value") == "a word"
	
	// clear all word
	e.clear 
	// e >> attr("value") == ""
	
	//select element
	val s: HtmlElement = parser >> css("select")
	s.select("c") // the thrid one is selected
	s.select(1) // 1 is index, so the second one is selected
	
	//this is for checkbox or radio button
	e.check //check the checkbox or radio button
	val checked: Boolean = e.checked // return if the element is checked
	
	e.setAttribute("class", "my-class")
	// e >> attr("class") == "my-class"
}
```

## FormElement
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod.css
import com.yukimt.scrape.element.FormElement
import com.yukimt.scrape.element.ElementMethod._

val browser = new PhantomBrowser("http://...")
browser.parse{ parser =>
	val form: FormElement = (parser >> css("form")) >> asForm
	
	//append <input type='hidden' name='my-name' value='my-value'/>
	form.addFormData("my-name", "my-value")
	
	// add '?a=b&c=d' to the current 'action' attributes value
	form.setQueryString(Map("a"->"b", "c"->"d"))
	
	//overwrite 'action' attributes
	form.setUrl("http://....")
	
	//submit the form
	form.submit
}
```

## ATagElement
```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod.css
import com.yukimt.scrape.element.ATagElement
import com.yukimt.scrape.element.ElementMethod._
import com.yukimt.scrape.Window

val browser = new PhantomBrowser("http://...")
browser.parse{ parser =>
	val a: ATagElement = (parser >> css("a")) >> asATag
	
	// add '?a=b&c=d' to the current 'href' attributes value
	a.setQueryString(Map("a"->"b", "c"->"d"))
	
	//overwrite 'href' attributes
	a.setUrl("http://....")
	
	//open the link in the current window
	a.click
	
	//open the link in the current window
	val w: Window = a.openInNewWindow
}
```

## Element case class vs. HtmlElement
HtmlElement can do a lot such as changing state or opening a new window.

If you can do them outside of `parse` or `extractElement` method, you need to be careful about chaging state of the browser in anywhere, so `extractElement` returns `Element` case class, not `HtmlElement`

```
import com.yukimt.scrape.browser.PhantomBrowser
import com.yukimt.scrape.element.ParserMethod.css
import com.yukimt.scrape.element.{HtmlElement, Element}

val browser = new PhantomBrowser("http://...")
val e: Element = browser.extractElement{ parser =>
	// assume there is <h1 class='my-class', data-id='1'>my-title</h1>
	val h: HtmlElement = parser >> css("h1")
	h // convert from HtmlElement into Element
}

/*
 * These are all you can do in Element case class
 */
val tag: String = e.tag // return "h1"
val attrs: Map[String, String] = e.attributes // return Map("class"->"my-class", "data-id"->"1")
val t: String = e.text // return "my-title"
```