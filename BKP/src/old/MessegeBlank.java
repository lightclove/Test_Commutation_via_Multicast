package old;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

   ///Thread thread = new Thread(new SenderMessage(25406, "230.0.06.37", panelIPBind.getIpbind(), 1, messegeBlank.puck()));

public class MessegeBlank {
    private int timeSilence;
    private MessegeBlank.HeadMessage headMessage;

    public int getTimeSilence() {
        return this.timeSilence;
    }

    public void setTimeSilence(int timeSilence) {
        this.timeSilence = timeSilence;
    }

    public MessegeBlank.HeadMessage getHeadMessage() {
        return this.headMessage;
    }

    public void setHeadMessage(MessegeBlank.HeadMessage headMessage) {
        this.headMessage = headMessage;
    }

    public MessegeBlank(int timeSilence) {
        this.timeSilence = timeSilence;
        this.headMessage = new MessegeBlank.HeadMessage("EMS00BKP".getBytes(), "ALLGROUP".getBytes(), 524337, "VER01.00".getBytes(), 60, 1, 1, 1, 1, 0);
    }

    public MessegeBlank(int timeSilence, MessegeBlank.HeadMessage headMessage) {
        this.timeSilence = timeSilence;
        this.headMessage = headMessage;
    }

    public MessegeBlank(byte[] arrMessege) {
        byte[] headArray = new byte[56];
        ByteBuffer pByteBuffer = ByteBuffer.allocate(60).order(ByteOrder.LITTLE_ENDIAN);
        pByteBuffer.position(0);
        pByteBuffer.get(headArray);
        int i = pByteBuffer.getInt();
        new MessegeBlank(i, new MessegeBlank.HeadMessage(headArray));
    }

    public byte[] puck() {
        ByteBuffer pByteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        pByteBuffer.position(0);
        this.headMessage.setnCRC(CheckCRC.ClcCRC(pByteBuffer.array()));
        pByteBuffer = ByteBuffer.allocate(60).order(ByteOrder.LITTLE_ENDIAN);
        pByteBuffer.position(0);
        pByteBuffer.put(this.headMessage.puck()).putInt(this.timeSilence);
        return pByteBuffer.array();
    }

    private class HeadMessage {
        private byte[] nameSender;
        private byte[] nameReceiver;
        private int messageType;
        private byte[] nameVersion;
        private int packetLength;
        private int messageNumber;
        private int numberOfMessage;
        private int amoountPaket;
        private int numberPaket;
        private int nReserved;
        private int nCRC;

        public HeadMessage(byte[] nameSender, byte[] nameReceiver, int messageType, byte[] nameVersion, int packetLength, int messageNumber, int numberOfMessage, int amoountPaket, int numberPaket, int nReserved) {
            this.nameSender = nameSender;
            this.nameReceiver = nameReceiver;
            this.messageType = messageType;
            this.nameVersion = nameVersion;
            this.packetLength = packetLength;
            this.messageNumber = messageNumber;
            this.numberOfMessage = numberOfMessage;
            this.amoountPaket = amoountPaket;
            this.numberPaket = numberPaket;
            this.nReserved = nReserved;
            this.nCRC = 0;
        }

        public HeadMessage(byte[] arrayByte) {
            ByteBuffer pByteBuffer = ByteBuffer.allocate(56).order(ByteOrder.LITTLE_ENDIAN);
            pByteBuffer.position(0);
            pByteBuffer.put(arrayByte);
            pByteBuffer.position(0);
            byte[] nameSender = new byte[8];
            byte[] nameReceiver = new byte[8];
            byte[] nameVersion = new byte[8];
            pByteBuffer.get(nameSender).get(nameReceiver);
            int messageType = pByteBuffer.getInt();
            pByteBuffer.get(nameVersion);
            int packetLength = pByteBuffer.getInt();
            int messageNumber = pByteBuffer.getInt();
            int numberOfMessage = pByteBuffer.getInt();
            int amoountPaket = pByteBuffer.getInt();
            int numberPaket = pByteBuffer.getInt();
            int nReserved = pByteBuffer.getInt();
            int nCRC = pByteBuffer.getInt();
            MessegeBlank.this.new HeadMessage(nameSender, nameReceiver, messageType, nameVersion, packetLength, messageNumber, numberOfMessage, amoountPaket, numberPaket, nReserved);
            this.setnCRC(nCRC);
        }

        public byte[] puck() {
            ByteBuffer pByteBuffer = ByteBuffer.allocate(56).order(ByteOrder.LITTLE_ENDIAN);
            pByteBuffer.position(0);
            pByteBuffer.put(this.nameSender).put(this.nameReceiver);
            pByteBuffer.putInt(this.messageType);
            pByteBuffer.put(this.nameVersion);
            pByteBuffer.putInt(this.packetLength);
            pByteBuffer.putInt(this.messageNumber);
            pByteBuffer.putInt(this.numberOfMessage);
            pByteBuffer.putInt(this.amoountPaket);
            pByteBuffer.putInt(this.numberPaket);
            pByteBuffer.putInt(this.nReserved);
            pByteBuffer.putInt(this.nCRC);
            return pByteBuffer.array();
        }

        public void setnCRC(int nCRC) {
            this.nCRC = nCRC;
        }
    }
}
