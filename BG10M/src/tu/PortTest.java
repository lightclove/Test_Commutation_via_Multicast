package tu;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 19.10.16.
 */
public class PortTest {
    public static void main(String[] args) throws IOException {
        InetAddress senderAddress = InetAddress.getByName("192.168.111.10");
        InetAddress receiverAddress = InetAddress.getByName("192.168.121.10");
        InetAddress group = InetAddress.getByName("230.0.0.1");
        int port = 50000;

        MulticastSocket sender = new MulticastSocket(null);
        MulticastSocket receiver = new MulticastSocket(port);

        sender.bind(new InetSocketAddress(senderAddress,port));
        //sender.setNetworkInterface(NetworkInterface.getByInetAddress(senderAddress));

        //receiver.bind(new InetSocketAddress(receiverAddress,port));
        receiver.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(receiverAddress));

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);

        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            byte[] data = new byte[]{1,2,3,4};
            DatagramPacket datagramPacket = new DatagramPacket(data,data.length,group,port);
            try {
                sender.send(datagramPacket);
                System.out.println("send");
            } catch (IOException e) {
                e.printStackTrace();
            }
        },0,1, TimeUnit.MILLISECONDS);

        scheduledThreadPoolExecutor.execute(() -> {
            while (true){
                byte[] buffer = new byte[1400];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    receiver.receive(datagramPacket);
                    byte[] received = new byte[datagramPacket.getLength()];
                    System.arraycopy(datagramPacket.getData(), 0, received, 0, received.length);
                    System.out.println(Arrays.toString(received));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
