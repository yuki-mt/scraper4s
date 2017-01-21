package com.yukimt

package object scrape {
  case class Window(id: String)

  case class ProxyServer(host: String, port: Int, username: String, password: String)
  case class ViewPoint()
}
