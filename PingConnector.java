package ru.nppame.utility;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

class Connector extends Thread {
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
