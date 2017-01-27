package com.yukimt.scrape
package browser

import collection.JavaConversions._
import scala.concurrent.duration.FiniteDuration
import org.openqa.selenium.{WebDriver, Cookie, By, JavascriptExecutor}
import org.openqa.selenium.support.ui.{WebDriverWait, ExpectedConditions}
import java.util.UUID
import com.yukimt.scrape.element.{Parser, Element, HtmlElement}

trait WindowManager {
  def inAllWindows(f: Browser[S] => Any): S = {
    getFromAllWindows(f)
    this
  }
  def getFromAllWindows[T](f: Browser[S] => T): Seq[T] = {
    val currentWindow = driver.getWindowHandle
    //FIXME: wish f is executed asyncronously in each window
    val result = driver.getWindowHandles.toSeq.map{ window =>
      driver.switchTo.window(window)
      f(this)
    }
    driver.switchTo.window(currentWindow)
    result
  }
  def withWindow(window: Window)(f: Browser[S] => Any): S = {
    getFromWindow(window)(f)
    this
  }
  def getFromWindow[T](window: Window)(f: Browser[S] => T): T = {
    val currentWindow = driver.getWindowHandle
    driver.switchTo.window(window.id)
    val result = f(this)
    driver.switchTo.window(currentWindow)
    result
  }
  def switchWindow(window: Window): S = {
    driver.switchTo.window(window.id)
    this
  }
  def getCurrentWindow(): Window = {
    Window(driver.getWindowHandle)
  }
  def depulicateCurrentWindow(): Window = {
    val url = getCurrentUrl
    executeJs(s"window.open('$url');")
    getCurrentWindow
  }
}
