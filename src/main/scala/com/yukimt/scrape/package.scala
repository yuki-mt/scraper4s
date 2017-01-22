package com.yukimt

import org.openqa.selenium.Dimension

package object scrape {
  case class Window(id: String)

  case class ProxyServer(host: String, port: Int, username: String, password: String)
  
  case class ViewPoint(width: Int, height: Int){
    def toDemension = new Dimension(width, height)
  }
  object ViewPoint {
    val SmartPhone = ViewPoint(320, 568)
    val Tablet = ViewPoint(768, 1024)
    val PC = ViewPoint(1280, 726)
  }
}
