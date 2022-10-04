package old;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SenderMessage implements Runnable {
    private MulticastSocket multicastSocket;
    private int quantityNumbers;
    private byte[] messageArrByte;
    private int port;
    private String group;
    private String ipBind;

    public SenderMessage(int port, String group, String ipBind, int quantityNumbers, byte[] messageArrByte) {
        this.quantityNumbers = quantityNumbers;
        this.messageArrByte = messageArrByte;
        this.port = port;
        this.group = group;
        this.ipBind = ipBind;
    }

    public void run() {
        try {
            this.multicastSocket = new MulticastSocket();
            this.multicastSocket.setInterface(InetAddress.getByName(this.ipBind));
            DatagramPacket e = new DatagramPacket(this.messageArrByte, this.messageArrByte.length, InetAddress.getByName(this.group), this.port);

            for(int i = 0; i < this.quantityNumbers; ++i) {
                this.multicastSocket.send(e);
                System.out.println("send message on that socket:" + this.group + ":" + this.port);
            }

            this.multicastSocket.close();
        } catch (Exception var3) {
            this.multicastSocket.close();
            System.out.println("Упс, вот незадача");
            System.out.println(var3.getMessage());
        }

    }
}
