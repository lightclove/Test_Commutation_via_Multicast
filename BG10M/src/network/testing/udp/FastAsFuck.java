package network.testing.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FastAsFuck {

    private static final int ITERATIONS = 100;

    public static void main(String[] args) throws IOException, InterruptedException {
        InetSocketAddress source = new InetSocketAddress("192.168.0.100", 50000);
        InetSocketAddress destination = new InetSocketAddress("192.168.0.101", 50001);

        DatagramSocket sender = new DatagramSocket(source);
        DatagramSocket receiver = new DatagramSocket(destination);
        receiver.setSoTimeout(1000);

        Map<Integer,Long> received = new HashMap<>();

        Long maximumTime = 0L;
        Long minimumTime = Long.MAX_VALUE;

        Long startTime = NetworkTestUtil.getMicroseconds();
        for(int number = 0; number < ITERATIONS; ++ number){
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(512);
                byteBuffer.putLong((long) 0);
                for(int index = 0; index < 126; ++ index){
                    byteBuffer.putInt(number);
                }

                long sendTime = NetworkTestUtil.getMicroseconds();
                byteBuffer.putLong(0, sendTime);

                DatagramPacket dataSend = new DatagramPacket(byteBuffer.array(),byteBuffer.array().length, destination);
                sender.send(dataSend);

                DatagramPacket datagramPacket = new DatagramPacket(new byte[1400], 1400);
                receiver.receive(datagramPacket);

                byte [] data = new byte[datagramPacket.getLength()];
                System.arraycopy(datagramPacket.getData(), 0, data, 0, data.length);
                byteBuffer = ByteBuffer.allocate(data.length).put(data);
                byteBuffer.flip();

                Long time = byteBuffer.getLong();
                Integer receivedNumber = byteBuffer.getInt();

                if(receivedNumber != number) System.err.println("Order broken, expected : " + number + ", received : " + receivedNumber);

                long transactionTime = NetworkTestUtil.getMicroseconds() - time;
                received.put(receivedNumber, transactionTime);

                if (transactionTime > maximumTime) maximumTime = transactionTime;
                if (transactionTime < minimumTime) minimumTime = transactionTime;

            } catch (IOException e) {
                System.err.println(e.getMessage() + " " + number);
            }
        }

        StringBuilder stringBuilder = NetworkTestUtil.generateResult(startTime, ITERATIONS, 100,
                new ArrayList<>(received.values()), maximumTime, minimumTime);
        Files.write(Paths.get("fast_as_fuck_distribuion.csv"), stringBuilder.toString().getBytes());
    }
}
