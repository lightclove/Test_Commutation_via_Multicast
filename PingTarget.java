package ru.nppame.utility;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

class Target {
  InetSocketAddress address;
  
  SocketChannel channel;
  
  Exception failure;
  
  long connectStart;
  
  long connectFinish = 0L;
  
  boolean shown = false;
  
  Target(String host) {
    try {
      this.address = new InetSocketAddress(InetAddress.getByName(host), Ping.port);
    } catch (IOException x) {
      this.failure = x;
    } 
  }
  
  void show() {
    String result;
    if (this.connectFinish != 0L) {
      result = Long.toString(this.connectFinish - this.connectStart) + "ms";
    } else if (this.failure != null) {
      result = this.failure.toString();
    } else {
      result = "Timed out";
    } 
    System.out.println(this.address + " : " + result);
    this.shown = true;
  }
}
