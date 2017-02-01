package com.yukimt

import org.openqa.selenium.Dimension
import java.util.Base64
import java.nio.charset.StandardCharsets

package object scrape {
  case class Window(id: String)
  case class BasicAuth(username: String, password: String){
    val key = "Authorization"
    def encode = "Basic " + Base64.getEncoder.encodeToString(s"$username:$password".getBytes(StandardCharsets.UTF_8))
  }

  case class ViewPoint(width: Int, height: Int){
    def toDemension = new Dimension(width, height)
  }
  object ViewPoint {
    val SmartPhone = ViewPoint(320, 568)
    val Tablet = ViewPoint(768, 1024)
    val PC = ViewPoint(1280, 726)
  }
}
