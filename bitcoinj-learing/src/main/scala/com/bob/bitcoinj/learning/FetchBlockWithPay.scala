package com.bob.bitcoinj.learning

import com.bob.bitcoin.utils.TimeCheck
import com.google.common.util.concurrent.{FutureCallback, Futures, MoreExecutors}
import org.bitcoinj.core._
import org.bitcoinj.net.discovery.DnsDiscovery
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.store.MemoryBlockStore
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener

/**
  * Created by wangxiang on 18/1/15.
  */
object FetchBlockWithPay extends App {

  val netParams = TestNet3Params.get()

  val wallet = new Wallet(netParams)
  val key = new ECKey()
  wallet.importKey(key)

  val addressFromKey = key.toAddress(netParams)
  println(s"public address generated: ${addressFromKey}")
  println(s"private key is: ${key.getPrivateKeyEncoded(netParams).toString}")

  val blockStore = new MemoryBlockStore(netParams)
  val chain: BlockChain = new BlockChain(netParams, blockStore)
  val peerGroup = new PeerGroup(netParams, chain)
  peerGroup.addPeerDiscovery(new DnsDiscovery(netParams))
  peerGroup.addWallet(wallet)
  println("start peer group")
  peerGroup.start()

  println("begin to downloading block chain")
  // 此句可能会运行很久很久
  TimeCheck.run {
    () => peerGroup.downloadBlockChain()
  }
  println("block chain downloaded")

  wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {

    // 当收到比特币时此方法会被回调
    override def onCoinsReceived(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin): Unit = {

      val value = tx.getValueSentToMe(wallet)
      println(s"received tx for ${value.toFriendlyString} : ${tx}")

      println(s"previous balance is: ${prevBalance.toFriendlyString}")

      println(s"new estimated balance is: ${newBalance.toFriendlyString}")

      println(s"coin received, wallet balance is: ${wallet.getBalance}")

      Futures.addCallback(tx.getConfidence.getDepthFuture(1), new FutureCallback[TransactionConfidence] {
        override def onFailure(t: Throwable): Unit = t.printStackTrace()

        override def onSuccess(result: TransactionConfidence): Unit = println(s"transaction confirmed, wallet balance is: ${wallet.getBalance}")
      })
    }
  })

  /** ****************************************************************************************************************/

  //region about pay

  val amountToPay = Coin.valueOf(0, 0)
  val addressToPay = Address.fromBase58(netParams, addressFromKey.toString)
  val sendResult = wallet.sendCoins(peerGroup, addressToPay, amountToPay)
  sendResult.broadcastComplete.addListener(new Runnable {
    override def run(): Unit = {
      println(s"coins sent,transaction has is: ${sendResult.tx.getHashAsString}")
    }
  }, MoreExecutors.directExecutor())

  //endregion

  io.StdIn.readLine()
}