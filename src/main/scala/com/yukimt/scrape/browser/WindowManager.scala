package com.yukimt.scrape
package browser

import collection.JavaConversions._

trait WindowManager[S, T <: S] {
  self: Browser[T] =>

  def inAllWindows(f: S => Any): T = {
    getFromAllWindows(f)
    this
  }

  def getFromAllWindows[U](f: S => U): Seq[U] = {
    val currentWindow = driver.getWindowHandle
    //FIXME: wish f is executed asyncronously in each window
    val result = driver.getWindowHandles.toSeq.map{ window =>
      driver.switchTo.window(window)
      f(this)
    }
    driver.switchTo.window(currentWindow)
    result
  }

  def withWindow(window: Window)(f: S => Any): T = {
    getFromWindow(window)(f)
    this
  }

  def getFromWindow[U](window: Window)(f: S => U): U = {
    val currentWindow = driver.getWindowHandle
    driver.switchTo.window(window.id)
    val result = f(this)
    driver.switchTo.window(currentWindow)
    result
  }

  def switch(window: Window): T = {
    driver.switchTo.window(window.id)
    this
  }

  def currentWindow: Window = {
    Window(driver.getWindowHandle)
  }

  def depulicateWindow(swtichToNewWindow: Boolean = false): Window = {
    val oldWindows = driver.getWindowHandles
    js(s"window.open('$currentUrl');")
    val newWindow = Window((driver.getWindowHandles -- oldWindows).head)
    if(swtichToNewWindow)
      switch(newWindow)
    newWindow
  }

  /************Histroy***********/
  def back: T = back(1)
  def back(n: Int): T = {
    (1 to n).foreach(_ => driver.navigate.back)
    this
  }
  def forward(n: Int): T = {
    (1 to n).foreach(_ => driver.navigate.forward)
    this
  }
  def forward: T = forward(1)
}
