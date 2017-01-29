package com.yukimt.scrape.element

import org.openqa.selenium.InvalidSelectorException
import org.openqa.selenium.support.ui.Select

trait InputElement {
  self: HtmlElementLike =>

  //type to textbox, textarea, ...
  def typing(value: String) = element.sendKeys(value)
  //clear value of textbox, texarea, ...
  def clear() = element.clear

  //check checkbox or radio button
  def check() = click

  protected def selectTag = {
    if(element.getTagName != "select")
      throw new InvalidSelectorException(s"${element.getTagName} tag is not supported 'select' method")
    new Select(element)
  }
  //select in select tag
  def select(text: String) = selectTag.selectByVisibleText(text)
  def select(index: Int) = selectTag.selectByIndex(index)
}
