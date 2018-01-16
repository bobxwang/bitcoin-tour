package com.bob.bitcoin.utils

/**
  * Created by wangxiang on 18/1/16.
  */
object TimeCheck {

  def run(action: () => Unit) = {
    val start = System.currentTimeMillis
    action
    val end = System.currentTimeMillis()
    println(s"action invoke spend ${(end - start) / 1000f} s")
  }
}