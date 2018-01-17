package com.bob.bitcoinj.learning

import org.bitcoinj.core.ECKey
import org.bitcoinj.params.TestNet3Params

/**
  * Created by wangxiang on 18/1/15.
  */
object KeyWithAddress extends App {

  val net = "test"

  val key = new ECKey()
  println(s"We created a key:\n ${key}")

  val netParams = TestNet3Params.get()
  val addressFromKey = key.toAddress(netParams)
  val privateKey = key.getPrivateKeyEncoded(netParams)

  println(s"On the ${net} network, we can using the public address:\n ${addressFromKey}")
  println(s"private key is\n: ${privateKey.toString}")
}