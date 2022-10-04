import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Щтветное приложение для TestBI (имитатор БКП)
 */
public class TestForTestBI {
    public static void main(String[] args) throws IOException {
        UdpSoket udpSoket;
        boolean b= true;
        if(b)
            udpSoket= new UdpSoket(12345,12345, "192.168.111.50", "192.168.111.55");
        else
            udpSoket = new UdpSoket(12345,12345, "192.168.111.50", "192.168.111.55");
        short id =12;
        while (true) {
            byte[] message;
            int lengthM=10;
            message = udpSoket.receive();
            ByteBuffer byteBuffer = ByteBuffer.allocate(message.length).order(ByteOrder.BIG_ENDIAN);
            byteBuffer.position(0);
            byteBuffer.put(message);
            byteBuffer.position(0);
            byte src = byteBuffer.get();
            byte dst = byteBuffer.get();
            byteBuffer.getShort();
            int sequens = byteBuffer.getInt();
            int silence = byteBuffer.getInt();
            System.out.println("sequens " + sequens + " silence " + silence);
            byteBuffer = ByteBuffer.allocate(message.length).order(ByteOrder.BIG_ENDIAN);
            byteBuffer.position(0);
            byteBuffer.put(dst).put(src).putShort(id).putInt(1).putInt(sequens);

            udpSoket.send(byteBuffer.array());

        }
    }
}
