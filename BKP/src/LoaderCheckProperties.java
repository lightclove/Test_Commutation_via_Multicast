import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.util.Properties;

/**
 * класс загружающий и проверяющий настройки приложения
 */
class LoaderCheckProperties {

    private boolean corecTProperties;
    private int port;
    private int portR;
    private int biDevices;
    private String ip1Bind;
    private String ip2Bind;
    private String ip1Send;
    private String ip2Send;
    private String useFor;

    boolean isCorecTProperties() {
        return corecTProperties;
    }

    int getPort() {
        return port;
    }

    int getPortR() {
        return portR;
    }

    String getIp1Bind() {
        return ip1Bind;
    }

    String getIp2Bind() {
        return ip2Bind;
    }

    String getIp1Send() {
        return ip1Send;
    }

    String getIp2Send() {
        return ip2Send;
    }


    public int getBiDevices() {
        return biDevices;
    }

    boolean isTest() {
        if (useFor.equals("test"))
            return true;
        else
            return false;
    }

    LoaderCheckProperties(String nameFileProperties) {
        corecTProperties = false;

        Properties properties = new Properties();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(nameFileProperties));
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getClass().getName() + " :\n" + e.getMessage(), "Not found file " + nameFileProperties, JOptionPane.ERROR_MESSAGE);
        }
        try {
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getClass().getName() + " :\n" + e.getMessage(), "bad file " + nameFileProperties, JOptionPane.ERROR_MESSAGE);
        }


        try {
            port = Integer.parseInt(properties.getProperty("port"));
            portR = Integer.parseInt(properties.getProperty("portR"));
            biDevices = Integer.parseInt(properties.getProperty("set.bi.devices"), 2);
            ip1Bind = properties.getProperty("host.ip.main");
            ip2Bind = properties.getProperty("host.ip.standby");
            ip1Send = properties.getProperty("device.ip.main");
            ip2Send = properties.getProperty("device.ip.standby");
            useFor = properties.getProperty("use.for");
            NetworkUtil.checkIP(ip1Send);
            NetworkUtil.checkIP(ip2Send);
            corecTProperties = NetworkUtil.checkIP(ip1Bind) & NetworkUtil.checkIP(ip2Bind);
            if (!corecTProperties) {
                System.out.println("ERROR: host ip ");
                JOptionPane.showMessageDialog(null, "error ip host!!!!!!", "error ip host" + nameFileProperties, JOptionPane.ERROR_MESSAGE);
            }
            printProperties();
            try {
                UdpSoket udpSoket1 = new UdpSoket(port, portR, ip1Bind, ip1Send);
                UdpSoket udpSoket2 = new UdpSoket(port, portR, ip2Bind, ip2Send);
                udpSoket1.close();
                udpSoket2.close();
            } catch (BindException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getClass().getName() + " :\n" + e.getMessage(), "bad properties in file " + nameFileProperties, JOptionPane.ERROR_MESSAGE);
        }

    }

    private void printProperties() {
        System.out.println("Load properties");
        System.out.println("portS: " + port);
        System.out.println("portR: " + portR);
        System.out.println("biDevices: " + biDevices);
        System.out.println("device.ip.main: " + ip1Send);
        System.out.println("device.ip.standby: " + ip2Send);
        System.out.println("host.ip.main: " + ip1Bind);
        System.out.println("host.ip.standby: " + ip2Bind);
        System.out.println("use.for: " + useFor);
    }


}

