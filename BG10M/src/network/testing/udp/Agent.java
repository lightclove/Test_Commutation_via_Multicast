package network.testing.udp;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * AUTHOR : Lobanov F.S.
 * DATE : 13.01.2016
 * TIME : 17:24
 * DESCRIPTION :
 */
public abstract class Agent {
    private final static Logger logger = Logger.getLogger(Agent.class.getName());

    static {
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
        logger.addHandler(consoleHandler);
    }

    private final String name;
    private final DatagramSocket datagramSocket;
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture scheduledFuture;

    private AtomicBoolean shouldProcessReceive = new AtomicBoolean(Boolean.FALSE);


    public Agent(Inet4Address address, Integer port, String name) throws SocketException {
        datagramSocket = new DatagramSocket(null);
        datagramSocket.bind(new InetSocketAddress(address, port));
        datagramSocket.setSoTimeout(1);
        this.name = name;
    }

    public void send(byte [] data, Inet4Address destinationAddress, Integer destinationPort) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(data,data.length, destinationAddress,destinationPort);
        datagramSocket.send(datagramPacket);
        notifyDataSend(this,data,destinationAddress,destinationPort);
    }

    public void startReceiving(){
        stopReceiving();
        shouldProcessReceive.set(true);
        scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            while (shouldProcessReceive.get()){
                processReceive();
            }
        }, 0, 1, TimeUnit.MICROSECONDS);
    }

    public void stopReceiving(){
        shouldProcessReceive.set(false);
        if(scheduledFuture != null){
            scheduledFuture.cancel(true);
        }
    }

    public void shutDown(){
        stopReceiving();
        scheduledThreadPoolExecutor.shutdownNow();
        datagramSocket.close();
    }

    protected abstract void processReceive();

    protected DatagramPacket receivePacket() throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[1400], 1400);
        datagramSocket.receive(datagramPacket);
        return datagramPacket;
    }

    public byte [] getData(DatagramPacket datagramPacket){
        byte[] data = new byte[datagramPacket.getLength()];
        System.arraycopy(datagramPacket.getData(), 0, data, 0, data.length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length).put(data);
        byteBuffer.flip();
        return byteBuffer.array();
    }

    public interface Listener{
        void dataReceived(Agent source, byte[] data, Inet4Address address, Integer port);
        void dataSend(Agent source, byte[] data, Inet4Address address, Integer port);
    }

    public void addListener(Listener listener){
        listeners.add(listener);
    }

    public void notifyDataReceived(Agent source, byte [] data, Inet4Address address, Integer port){
        for(Listener listener : listeners){
            listener.dataReceived(source, data, address, port);
        }
    }

    public void notifyDataSend(Agent source, byte [] data, Inet4Address address, Integer port){
        for(Listener listener : listeners){
            listener.dataSend(source, data, address, port);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        if (name != null ? !name.equals(agent.name) : agent.name != null) return false;
        if (datagramSocket != null ? !datagramSocket.equals(agent.datagramSocket) : agent.datagramSocket != null)
            return false;
        if (listeners != null ? !listeners.equals(agent.listeners) : agent.listeners != null) return false;
        if (scheduledThreadPoolExecutor != null ? !scheduledThreadPoolExecutor.equals(agent.scheduledThreadPoolExecutor) : agent.scheduledThreadPoolExecutor != null)
            return false;
        return !(scheduledFuture != null ? !scheduledFuture.equals(agent.scheduledFuture) : agent.scheduledFuture != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (datagramSocket != null ? datagramSocket.hashCode() : 0);
        result = 31 * result + (listeners != null ? listeners.hashCode() : 0);
        result = 31 * result + (scheduledThreadPoolExecutor != null ? scheduledThreadPoolExecutor.hashCode() : 0);
        result = 31 * result + (scheduledFuture != null ? scheduledFuture.hashCode() : 0);
        return result;
    }
}
