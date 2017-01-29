package com.yukimt.scrape

import org.openqa.selenium.{WebElement, WebDriver}

package object element {
  type ElementMethod[S] = (WebElement, WebDriver) => Iterable[S]
  type ParserMethod = WebDriver => Iterable[HtmlElement]
}
