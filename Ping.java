package ru.nppame.utility;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Ping {
  static int DAYTIME_PORT = 13;
  
  static int port = DAYTIME_PORT;
  
  static class Target {
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
  
  static class Printer extends Thread {
    LinkedList<Ping.Target> pending = new LinkedList<>();
    
    Printer() {
      setName("Printer");
      setDaemon(true);
    }
    
    void add(Ping.Target t) {
      synchronized (this.pending) {
        this.pending.add(t);
        this.pending.notify();
      } 
    }
    
    public void run() {
      try {
        while (true) {
          Ping.Target t = null;
          synchronized (this.pending) {
            while (this.pending.size() == 0)
              this.pending.wait(); 
            t = this.pending.removeFirst();
          } 
          t.show();
        } 
      } catch (InterruptedException x) {
        return;
      } 
    }
  }
  
  static class Connector extends Thread {
    Selector sel;
    
    Ping.Printer printer;
    
    LinkedList<Ping.Target> pending = new LinkedList<>();
    
    volatile boolean shutdown;
    
    void add(Ping.Target t) {
      SocketChannel sc = null;
      try {
        sc = SocketChannel.open();
        sc.configureBlocking(false);
        boolean connected = sc.connect(t.address);
        t.channel = sc;
        t.connectStart = System.currentTimeMillis();
        if (connected) {
          t.connectFinish = t.connectStart;
          sc.close();
          this.printer.add(t);
        } else {
          synchronized (this.pending) {
            this.pending.add(t);
          } 
          this.sel.wakeup();
        } 
      } catch (IOException x) {
        if (sc != null)
          try {
            sc.close();
          } catch (IOException xx) {} 
        t.failure = x;
        this.printer.add(t);
      } 
    }
    
    void processPendingTargets() throws IOException {
      synchronized (this.pending) {
        while (this.pending.size() > 0) {
          Ping.Target t = this.pending.removeFirst();
          try {
            t.channel.register(this.sel, 8, t);
          } catch (IOException x) {
            t.channel.close();
            t.failure = x;
            this.printer.add(t);
          } 
        } 
      } 
    }
    
    void processSelectedKeys() throws IOException {
      for (Iterator<SelectionKey> i = this.sel.selectedKeys().iterator(); i.hasNext(); ) {
        SelectionKey sk = i.next();
        i.remove();
        Ping.Target t = (Ping.Target)sk.attachment();
        SocketChannel sc = (SocketChannel)sk.channel();
        try {
          if (sc.finishConnect()) {
            sk.cancel();
            t.connectFinish = System.currentTimeMillis();
            sc.close();
            this.printer.add(t);
          } 
        } catch (IOException x) {
          sc.close();
          t.failure = x;
          this.printer.add(t);
        } 
      } 
    }
    
    Connector(Ping.Printer pr) throws IOException {
      this.shutdown = false;
      this.printer = pr;
      this.sel = Selector.open();
      setName("Connector");
    }
    
    void shutdown() {
      this.shutdown = true;
      this.sel.wakeup();
    }
    
    public void run() {
      while (true) {
        try {
          int n = this.sel.select();
          if (n > 0)
            processSelectedKeys(); 
          processPendingTargets();
          if (this.shutdown) {
            this.sel.close();
            return;
          } 
        } catch (IOException x) {
          x.printStackTrace();
        } 
      } 
    }
  }
  
  public static void main(String[] args) throws InterruptedException, IOException {
    if (args.length < 1) {
      System.err.println("Usage: java Ping [port] host...");
      return;
    } 
    int firstArg = 0;
    if (Pattern.matches("[0-9]+", args[0])) {
      port = Integer.parseInt(args[0]);
      firstArg = 1;
    } 
    Printer printer = new Printer();
    printer.start();
    Connector connector = new Connector(printer);
    connector.start();
    LinkedList<Target> targets = new LinkedList<>();
    for (int j = firstArg; j < args.length; j++) {
      Target t = new Target(args[j]);
      targets.add(t);
      connector.add(t);
    } 
    Thread.sleep(2000L);
    connector.shutdown();
    connector.join();
    for (Iterator<Target> i = targets.iterator(); i.hasNext(); ) {
      Target t = i.next();
      if (!t.shown)
        t.show(); 
    } 
  }
}
