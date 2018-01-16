package com.bob.bitcoinj.learning

import java.io.{File, IOException}

import org.bitcoinj.core.ECKey
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.Wallet

/**
  * Created by wangxiang on 18/1/15.
  */
object WalletCreate extends App {

  val netParams = TestNet3Params.get()
  val walletFile = new File("test.wallet")
  var wallet: Wallet = null

  try {
    wallet = new Wallet(netParams)
    (0 to 4).foreach(_ => {
      wallet.importKey(new ECKey())
    })
    wallet.saveToFile(walletFile)
  } catch {
    case ioe: IOException => println(ioe.getMessage)
  }

  val firstKey = wallet.getImportedKeys.get(0)
  println(s"First key in the wallet:\n ${firstKey}")

  println(s"Complete content of the wallet:\n ${wallet}")

  if(wallet.isPubKeyHashMine(firstKey.getPubKeyHash)) {
    println("Yep, that's my key")
  } else {
    println("Nope, that key didn't come from this wallet")
  }

}