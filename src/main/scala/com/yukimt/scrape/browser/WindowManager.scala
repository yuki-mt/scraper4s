package com.yukimt.scrape
package browser

import collection.JavaConversions._

trait WindowManager[S] {
  self: Browser[S] =>

  def inAllWindows(f: S => Any): S = {
    getFromAllWindows(f)
    this
  }

  def getFromAllWindows[T](f: S => T): Seq[T] = {
    val currentWindow = driver.getWindowHandle
    //FIXME: wish f is executed asyncronously in each window
    val result = driver.getWindowHandles.toSeq.map{ window =>
      driver.switchTo.window(window)
      f(this)
    }
    driver.switchTo.window(currentWindow)
    result
  }

  def withWindow(window: Window)(f: S => Any): S = {
    getFromWindow(window)(f)
    this
  }

  def getFromWindow[T](window: Window)(f: S => T): T = {
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

  def currentWindow: Window = {
    Window(driver.getWindowHandle)
  }

  def depulicateCurrentWindow: Window = {
    executeJs(s"window.open('$currentUrl');")
    currentWindow
  }
}
