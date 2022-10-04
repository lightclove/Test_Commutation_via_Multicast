package tu.LimitSwitch;


import components.NotEditableOutputArea;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by user on 27.10.16.
 */
public class AlarmHandler implements Runnable{
    private final LimitSwitch limitSwitch;
    private final InetAddress host;
    private final NotEditableOutputArea area;
    private final JLabel label;

    AlarmHandler(LimitSwitch limitSwitch, InetAddress host, NotEditableOutputArea area, JLabel label) {
        this.limitSwitch = limitSwitch;
        this.host = host;
        this.area = area;
        this.label = label;
    }
    @Override
    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(limitSwitch.getAlarmPort());
            socket.joinGroup(new InetSocketAddress(limitSwitch.getAlarmGroup(), limitSwitch.getAlarmPort()), NetworkInterface.getByInetAddress(host));
            socket.setSoTimeout(1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            byte[] data = new byte[1400];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                byte[] received = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, received, 0, received.length);
                ByteBuffer byteBuffer = ByteBuffer.wrap(received);

                String packetAddress = packet.getAddress().getHostAddress();
                boolean address = (packetAddress.equals(limitSwitch.getAddress()));
                boolean port = packet.getPort() == limitSwitch.getAlarmPort();

                boolean source = Util.byteToUnsigned(byteBuffer.get()) == limitSwitch.getId();
                boolean destination = Util.byteToUnsigned(byteBuffer.get()) == 255;
                boolean id = Util.shortToUnsigned(byteBuffer.getShort()) == limitSwitch.getAlarmId();

                boolean alarmData = Util.intToUnsigned(byteBuffer.getInt()) == limitSwitch.getAlarmData();

                area.addColorText("Принято : " + Arrays.toString(received) + "\n",Color.black);
                if(address && port
                        && source && destination && id
                        && alarmData){
                    label.setBackground(Color.RED);
                    label.setText("Открыта");
                    area.addColorText("Ошибок нет\n",Color.green);
                }else{
                    label.setBackground(Color.GREEN);
                    label.setText("Закрыта");
                    area.addColorText("Ошибка в пакете\n",Color.red);
                }
                area.addColorText("\n",Color.black);
            } catch (Exception e) {
                //e.printStackTrace();
                label.setBackground(Color.GREEN);
                label.setText("Закрыта");
                //area.addColorText("Ошибка приема " + e.getLocalizedMessage() + "\n\n");
            }
        }
        if (socket != null) socket.close();
    }
}
