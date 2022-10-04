package network.testing.tcp;

import network.testing.udp.NetworkTestUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AUTHOR : Lobanov F.S.
 * DATE : 15.01.2016
 * TIME : 14:42
 * DESCRIPTION :
 */
public class TCPOneWayTest {
    private final Socket socket;
    private final ServerSocket serverSocket;

    private final Integer ITERATIONS = 1000000;

    private final AtomicLong maximumTime = new AtomicLong(0L);
    private final AtomicLong minimumTime = new AtomicLong(Long.MAX_VALUE);
    private final Map<Integer,Long> times = new ConcurrentHashMap<>();

    private final Inet4Address secondAddress;
    private final Integer secondPort;

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private Future future;

    public TCPOneWayTest(String firstAddress, Integer firstPort, String secondAddress, Integer secondPort) throws IOException {
        this.socket = new Socket();
        this.serverSocket = new ServerSocket(secondPort);

        this.secondAddress = (Inet4Address) Inet4Address.getByName(secondAddress);
        this.secondPort = secondPort;

        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(InetAddress.getByName(firstAddress), firstPort));

        Executors.newSingleThreadScheduledExecutor().execute(() -> {
            try {
                Socket incoming = serverSocket.accept();
                future = scheduledThreadPoolExecutor.submit(new Session(incoming));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private class Session implements Runnable{
        private final Socket incoming;

        Session(Socket incoming){
            this.incoming = incoming;
        }

        @Override
        public void run() {
            try {
                DataInputStream dataInputStream = new DataInputStream(incoming.getInputStream());
                while (true) {
                    try{
                        byte [] data = new byte[512];
                        dataInputStream.readFully(data);

                        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length).put(data);
                        byteBuffer.flip();
                        Long time = byteBuffer.getLong();
                        Integer iteration = byteBuffer.getInt();

                        Long transactionTime = (NetworkTestUtil.getMicroseconds()) - time;
                        times.put(iteration,transactionTime);

                        System.out.println(transactionTime);
                        System.out.println(iteration);

                        if (transactionTime > maximumTime.get()) maximumTime.set(transactionTime);
                        if (transactionTime < minimumTime.get()) minimumTime.set(transactionTime);
                    }catch (IOException ignored){}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void run() throws IOException {
        System.out.println("TRY CONNECT");
        socket.connect(new InetSocketAddress(secondAddress,secondPort));
        System.out.println("CONNECTED");
        Long startTime = (NetworkTestUtil.getMicroseconds());
        for (Integer iteration = 0; iteration < ITERATIONS; ++iteration) {
            try {
                ByteBuffer data = ByteBuffer.allocate(512);

                data.putLong(0L);
                for(int index = 0; index < 126; ++ index){
                    data.putInt(iteration);
                }

                Long time = NetworkTestUtil.getMicroseconds();
                data.putLong(0, time);
                socket.getOutputStream().write(data.array());
            } catch (IOException  e) {
                System.err.println(e.getMessage() + " " + iteration);
            }

        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        socket.close();
        serverSocket.close();

        StringBuilder stringBuilder = NetworkTestUtil.generateResult(startTime, ITERATIONS, 1000,
                new ArrayList<>(times.values()), maximumTime.get(), minimumTime.get());

        Files.write(Paths.get("tcp_one_way_distribuion.csv"),stringBuilder.toString().getBytes());
    }

    public static void main(String[] args) throws IOException {
        System.out.println("START");
        TCPOneWayTest TCPOneWayTest = new TCPOneWayTest("192.168.0.100",50000,"192.168.0.101",50001);
        TCPOneWayTest.run();
        System.exit(0);
    }
}
