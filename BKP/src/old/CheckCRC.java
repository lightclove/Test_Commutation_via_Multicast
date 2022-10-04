package old;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CheckCRC {
    public CheckCRC() {
    }

    public static int ClcCRC(byte[] arrbyte) {
        return ClcCRC(arrbyte, 0, arrbyte.length);
    }

    public static int ClcCRC(byte[] arrbyte, int nFr, int nnn) {
        ByteBuffer pByteBuffer = ByteBuffer.allocate(nnn + 4).order(ByteOrder.LITTLE_ENDIAN);
        pByteBuffer.putInt(nnn, 0);
        pByteBuffer.position(0);
        pByteBuffer.put(arrbyte, nFr, nnn);
        pByteBuffer.position(0);
        int nCRC = 0;

        for(int i = 0; i < nnn; i += 4) {
            int nTmp = pByteBuffer.getInt();
            nCRC += nTmp;
            nCRC &= -1;
        }

        return nCRC;
    }

    public static void main(String[] args) {
        byte[] arrbyte = new byte[]{12, -22, 14, 8, -93, -49, 87, 95, 89, 12, 37, 85, -69, -22, 58, 17, -22, 59, -55, -55, -71, 2, 34, -118, 1, 95, 7, -74, 73, 51, 82, 122, 57, 89, 16, 23, 49, 51, 2, 85, 6, -64, 61, 22, -1};
        int nCRC = ClcCRC(arrbyte);
        if(nCRC == -732180705) {
            System.out.println("nCRC OK");
        } else {
            System.out.println("nCRC ERROR");
        }

    }
}
