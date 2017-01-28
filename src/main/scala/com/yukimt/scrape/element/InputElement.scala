package com.yukimt.scrape.element

import org.openqa.selenium.InvalidSelectorException

trait InputElement {
  self: HtmlElementLike =>

  def parentForm: Option[FormElement] = 
    tryToGetParantForm(this).flatMap(_.asFormElement)

  protected def tryToGetParantForm(e: HtmlElementLike):Option[HtmlElement] = {
    e.parent.flatMap{ p =>
      if(p.tagName == "form") Some(p) 
      else tryToGetParantForm(p)
    }
  }

  //type to textbox, textarea, ...
  def typing(value: String) = element.sendKeys(value)
  //clear value of textbox, texarea, ...
  def clear() = element.clear

  //check checkbox or radio button
  def check() = click

  //select in select tag
  def select(text: String) = selectTag.selectByVisibleText(text)
  def select(index: Int) = selectTag.selectByIndex(index)
}
