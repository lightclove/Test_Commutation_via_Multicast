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
class KSSHandler implements Runnable {
    private final LimitSwitch limitSwitch;
    private final InetAddress host;
    private final NotEditableOutputArea area;
    private final JLabel label;

    KSSHandler(LimitSwitch limitSwitch, InetAddress host, NotEditableOutputArea area, JLabel label) {
        this.limitSwitch = limitSwitch;
        this.host = host;
        this.area = area;
        this.label = label;
    }

    @Override
    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(limitSwitch.getKssPort());
            socket.joinGroup(new InetSocketAddress(limitSwitch.getKssGroup(), limitSwitch.getKssPort()), NetworkInterface.getByInetAddress(host));
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
                System.out.println("address " + address);
                boolean port = packet.getPort() == limitSwitch.getKssPort();
                System.out.println("port " + port);
                boolean source = Util.byteToUnsigned(byteBuffer.get()) == limitSwitch.getId();
                System.out.println("source " + source);
                boolean destination = Util.byteToUnsigned(byteBuffer.get()) == 255;
                System.out.println("destination " + destination);
                boolean id = Util.shortToUnsigned(byteBuffer.getShort()) == limitSwitch.getKssId();
                System.out.println("id " + id);

                boolean time =  Util.intToUnsigned(byteBuffer.getInt()) == 0 &&  Util.intToUnsigned(byteBuffer.getInt()) == 0;
                System.out.println("time " + time);
                boolean connection = Util.byteToUnsigned(byteBuffer.get()) == 3;
                System.out.println("connection " + connection);
                boolean serviceability = Util.byteToUnsigned(byteBuffer.get()) == 1;
                System.out.println("serviceability " + serviceability);
                boolean mode = Util.byteToUnsigned(byteBuffer.get()) == 3;
                System.out.println("mode " + mode);
                boolean reserved = Util.byteToUnsigned(byteBuffer.get()) == 0;
                System.out.println("reserved " + reserved);

                area.addColorText("Принято : " + Arrays.toString(received) + "\n",Color.black);
                if(address && port
                        && source && destination && id
                        && time && connection && serviceability && mode && reserved){
                    label.setBackground(Color.GREEN);
                    label.setText("Подключено");
                    area.addColorText("Ошибок нет\n",Color.green);
                }else{
                    label.setBackground(Color.RED);
                    label.setText("Отключено");
                    area.addColorText("Ошибка в пакете\n",Color.red);
                }

                area.addColorText("\n",Color.black);

            } catch (Exception e) {
                e.printStackTrace();
                label.setBackground(Color.RED);
                label.setText("Отключено");
                //area.addColorText("Ошибка приема " + e.getLocalizedMessage() + "\n\n");
            }
        }
        if (socket != null) socket.close();
    }
}
