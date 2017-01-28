package com.yukimt.scrape.element

case class Element(
  tag: String,
  attributes: Map[String, String],
  text: String
  )
