import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by admin on 22.01.14.
 * Класс содержащий статическую функцию возвращающею IP адреса сетевых карт пк
 */
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
        for (NetworkInterface net : netInts)
            if (!net.isLoopback() && !net.isVirtual() && net.isUp() && !net.isPointToPoint()) {
                Enumeration<InetAddress> addrEnum = net.getInetAddresses();
                while (addrEnum.hasMoreElements()) {
                    InetAddress addr = addrEnum.nextElement();
                    // filter out addresses, which cannot be considered as the main address
                    // and return the first suitable address
                    if (!addr.isLoopbackAddress() && !addr.isAnyLocalAddress()
                            && !addr.isLinkLocalAddress() && !addr.isMulticastAddress()
                            ) {
                        list.add(addr);
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
    public static String [] getLocalNameUp()  {
        InetAddress [] inetAdress = new InetAddress[0];
        try {
            inetAdress = getLocalInterfesesUp();
        } catch (Exception e) {
           // e.printStackTrace();
        }
        String [] arrString= new String[inetAdress.length];
        for(int i=0; i < inetAdress.length; i++){
            StringBuffer stringBuffer = new StringBuffer(inetAdress[i].toString());
            arrString[i] = stringBuffer.substring(1);
        }
        return arrString;
    }



    public static boolean checkIP(String stringIP) {

        try {
            InetAddress[] arrIP = getLocalInterfesesUp();

            for (InetAddress ip : arrIP) {
                String ips = InetAddress.getByName(stringIP).toString();
                if (ips.equals(ip.toString())) return true;
            }
            return false;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }

    }
}
