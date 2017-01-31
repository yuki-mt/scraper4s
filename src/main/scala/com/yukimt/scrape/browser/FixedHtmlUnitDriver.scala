package com.yukimt.scrape
package browser

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import collection.JavaConversions._

/**
 * Not expected to use directly. This class will be used through UnitBrowser class
 */
private[browser] class FixedHtmlUnitDriver extends HtmlUnitDriver(true) {
  private var _code: Int = 0
  private var _headers: Map[String, String] = Map.empty

  def setHeader(name: String, value: String) = getWebClient.addRequestHeader(name, value)

  override def get(url: String) = {
    super.get(url)

    val r = getWebClient.getCurrentWindow.getEnclosedPage.getWebResponse
    _code = r.getStatusCode
    _headers = r.getResponseHeaders.map(h => (h.getName -> h.getValue)).toMap
  }
  
  def statusCode = _code

  def header(key: String) = _headers.get(key)
  def headers = _headers
}
