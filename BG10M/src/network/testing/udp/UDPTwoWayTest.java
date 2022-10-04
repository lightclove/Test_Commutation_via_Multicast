package network.testing.udp;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AUTHOR : Lobanov F.S.
 * DATE : 13.01.2016
 * TIME : 10:02
 * DESCRIPTION :
 */
public class UDPTwoWayTest extends JFrame implements Agent.Listener {
    private final Agent first;
    private final Agent second;
    private final Inet4Address secondAddress;
    private final Integer secondPort;

    private final static Integer WAIT_BETWEEN_ITERATIONS = 1;
    private final static Integer ITERATIONS = 10_000;

    private final AtomicLong maximumTime = new AtomicLong(0);
    private final AtomicLong minimumTime = new AtomicLong(Long.MAX_VALUE);

    private final ConcurrentHashMap<Integer,Long> times = new ConcurrentHashMap<>();

    UDPTwoWayTest(String firstAddress, Integer firstPort, String secondAddress, Integer secondPort) throws IOException {
        this.secondAddress = (Inet4Address) Inet4Address.getByName(secondAddress);

        first = new Agent((Inet4Address) Inet4Address.getByName(firstAddress), firstPort , "FIRST") {
            @Override
            protected void processReceive() {
                try {
                    DatagramPacket datagramPacket  = receivePacket();
                    byte [] data = getData(datagramPacket);
                    notifyDataReceived(this, data, (Inet4Address) datagramPacket.getAddress(),datagramPacket.getPort());
                } catch (IOException ignored) {}
            }
        };

        second = new Agent(this.secondAddress, secondPort, "SECOND") {
            @Override
            protected void processReceive() {
                try {
                    DatagramPacket datagramPacket = receivePacket();
                    byte [] data = getData(datagramPacket);
                    notifyDataReceived(this, data, (Inet4Address) datagramPacket.getAddress(),datagramPacket.getPort());
                    send(data, (Inet4Address) datagramPacket.getAddress(),datagramPacket.getPort());
                } catch (IOException ignored) {}
            }
        };

        this.secondPort = secondPort;

        first.addListener(this);
        second.addListener(this);
    }

    public void run() throws IOException {
        second.startReceiving();
        Long startTime = (NetworkTestUtil.getMicroseconds());

        for (Integer iteration = 0; iteration < ITERATIONS; ++iteration) {
            ByteBuffer data = ByteBuffer.allocate(512);

            data.putLong(0L);
            for(int index = 0; index < 126; ++ index){
                data.putInt(iteration);
            }
            Long time = NetworkTestUtil.getMicroseconds();
            data.putLong(0, time);
            first.send(data.array(), secondAddress, secondPort);
            first.startReceiving();
            try {
                Thread.sleep(WAIT_BETWEEN_ITERATIONS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            first.stopReceiving();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        first.shutDown();
        second.shutDown();

        for(int i = 0; i < ITERATIONS; ++ i){
            if(times.get(i) == null) System.err.println("MISSED " + i);
        }

        StringBuilder stringBuilder = NetworkTestUtil.generateResult(startTime, ITERATIONS, 1000,
                new ArrayList<>(times.values()), maximumTime.get(), minimumTime.get());

        Files.write(Paths.get("udp_two_way_distribuion.csv"),stringBuilder.toString().getBytes());
    }

    @Override
    public  synchronized void dataReceived(Agent agent, byte[] data, Inet4Address address, Integer port) {
        if(agent == second){
            System.out.println("SECOND RECEIVED FROM  :  " + address + " / " + port );
        }else if(agent == first){
            System.out.println("FIRST RECEIVED FROM :  " + address + " / " + port );

            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length).put(data);
            byteBuffer.flip();
            Long time = byteBuffer.getLong();
            Integer iteration = byteBuffer.getInt();
            //System.out.println("DATA RECEIVED FROM :  " + address + " / " + port + " ITERATION : " + iteration + " TIME : " + time);

            Long transactionTime = (NetworkTestUtil.getMicroseconds()) - time;
            System.out.println("TRANSACTION : " + transactionTime);
            times.put(iteration,transactionTime);

            if (transactionTime > maximumTime.get()) maximumTime.set(transactionTime);
            if (transactionTime < minimumTime.get()) minimumTime.set(transactionTime);
        }
    }

    @Override
    public synchronized void dataSend(Agent agent, byte[] data, Inet4Address address, Integer port) {
        if(agent == second) System.out.println("SECOND SEND TO :" + address + " / " + port ) ;
        else if (agent == first) System.out.println("FIRST SEND TO :" + address + " / " + port );
    }

    public static void main(String[] args) throws IOException{
        UDPTwoWayTest UDPTwoWayTest = new UDPTwoWayTest("192.168.0.100", 50000, "192.168.0.101", 50001);
        UDPTwoWayTest.run();
        System.exit(0);
    }
}
