package porttest.utility;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
 

public class NetworkUtil {

    /**
     * Функция возвращающея IP адреса сетевых карт.
     *
     * @return массив с IP адресами сетывых карт ПК.
     * @throws Exception
     *
     */
    public static InetAddress[] getLocalInterfesesUp() throws Exception {
        List<NetworkInterface> netInts = Collections.list(NetworkInterface.getNetworkInterfaces());
        ArrayList<InetAddress> list = new ArrayList<InetAddress>();
        for (NetworkInterface net : netInts) {
            if (!net.isLoopback() && !net.isVirtual() && net.isUp() && !net.isPointToPoint()) {
                Enumeration<InetAddress> addrEnum = net.getInetAddresses();
                while (addrEnum.hasMoreElements()) {
                    InetAddress addr = addrEnum.nextElement();
                    // filter out addresses, which cannot be considered as the main address
                    // and return the first suitable address
                    if (!addr.isLoopbackAddress() && !addr.isAnyLocalAddress()
                            && !addr.isLinkLocalAddress() && !addr.isMulticastAddress()) {
                        list.add(addr);
                    }
                }
            }
        }

        InetAddress[] arrInet = new InetAddress[0];
        arrInet = list.<InetAddress>toArray(arrInet);
        return arrInet;
    }

    /**
     * Функция возвращающея IP адреса сетевых карт.
     *
     * @return массив с IP адресами сетывых карт ПК.
     * @throws Exception
     */
    public static String[] getLocalNameUp() {
        InetAddress[] inetAdress = new InetAddress[0];
        try {
            inetAdress = getLocalInterfesesUp();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        String[] arrString = new String[inetAdress.length];
        for (int i = 0; i < inetAdress.length; i++) {
            StringBuffer stringBuffer = new StringBuffer(inetAdress[i].toString());
            arrString[i] = stringBuffer.substring(1);
        }
        return arrString;
    }

    /**
     * Проверяет соответствует ли заданный ip адрес какому-либо значению
     * списка(массива) ip адресов NIC'ов, который isUp
     *
     * @param stringIP
     * @return
     */
    public static boolean checkIP(String stringIP) {

        try {
            InetAddress[] arrIP = getLocalInterfesesUp();

            for (InetAddress ip : arrIP) {
                String ips = InetAddress.getByName(stringIP).toString();
                if (ips.equals(ip.toString())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
   

    // Use this method to ping1 hosts on Windows or other platforms, with timeout of 3 secs
    // 
    /**
     * 
     * @param sourceIp
     * @param hostIp
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    public static boolean ping2(String sourceIp, String hostIp) throws IOException, InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            ProcessBuilder processBuilder = new ProcessBuilder("ping", isWindows ? "-S" : "-I", sourceIp, hostIp);
            final Process proc = processBuilder.start();
            proc.waitFor();
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "CP866");
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {

                if (line.contains("TTL")) {
                    System.out.println("Host " + hostIp + " available from ip " + sourceIp);
                    return true;
                }

            }
        }
        return false;
    }
}
