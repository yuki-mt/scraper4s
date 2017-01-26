package com.yukimt.scrape
package browser

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.gargoylesoftware.htmlunit.{WebClient, ProxyConfig, DefaultCredentialsProvider}
import collection.JavaConversions._

/**
 * Not expected to use directly. This class will be used through UnitBrowser class
 */
private[browser] class FixedHtmlUnitDriver(proxy: Option[ProxyServer]) extends HtmlUnitDriver(true) {
  private var _code: Int = 0
  private var _headers: Map[String, String] = Map.empty

  def setHeader(name: String, value: String) = getWebClient.addRequestHeader(name, value)

  override def get(url: String) = {
    super.get(url)

    val r = getWebClient.getCurrentWindow.getEnclosedPage.getWebResponse
    _code = r.getStatusCode
    _headers = r.getResponseHeaders.map(h => (h.getName -> h.getValue)).toMap
  }
  
  override def modifyWebClient(client: WebClient) = {
    proxy.foreach{p =>
      setHTTPProxy(p.host, p.port, null)
      val cp = client.getCredentialsProvider.asInstanceOf[DefaultCredentialsProvider]
      cp.addCredentials(p.username, p.password)
    }
    client
  }

  def statusCode = _code

  def header(key: String) = _headers.get(key)
  def headers = _headers
}
