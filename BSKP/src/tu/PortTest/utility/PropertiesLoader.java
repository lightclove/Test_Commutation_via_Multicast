package porttest.utility;

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
public class PropertiesLoader {

    private boolean corecTProperties;
    private int port;
    private int port1;
    private int port2;
 
    private int timeOut;
    private String ip1Bind;
    private String ip2Bind;
    private String group1;
    private String group2;
    private String BSKPip;
    private String messageToBaud;
    private int howMuchmessagesToBaud;

    public boolean isCorecTProperties() {
        return corecTProperties;
    }
    public int getPort() {
        return port;
    }
    
    public int getPort1() {
        return port1;    
    }

    public int getPort2() {
        return port2;  
    }

    public int getTimeOut() {
        return timeOut;
    }
    
    public String getIp1Bind() {
        return ip1Bind;
    }

    public String getIp2Bind() {
        return ip2Bind;
    }
    
    public String getGroup1() {
        return group1;
    }
     public String getGroup2() {
        return group2;
    }
    
    public String getBSKPip() {
        return BSKPip;
    }
    
    public String getMessageToBaud() {
        return messageToBaud;
    }

    public int getHowMuchmessagesToBaud() {
        return howMuchmessagesToBaud;
    }
    
    public PropertiesLoader(String nameFileProperties)  {
        corecTProperties = false;

        Properties properties = new Properties();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(nameFileProperties));
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getClass().getName() + " :\n" + e.getMessage(), "Файл настроек не найден  "+nameFileProperties, JOptionPane.ERROR_MESSAGE);
        }
        try {
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getClass().getName() + " :\n" + e.getMessage(), "Некорректный файл "+nameFileProperties, JOptionPane.ERROR_MESSAGE);
        }


        try {
            port = Integer.parseInt(properties.getProperty("port"));
 
 
            timeOut = Integer.parseInt(properties.getProperty("timeOut"));
            ip1Bind = properties.getProperty("host.ip.main");
            ip2Bind = properties.getProperty("host.ip.standby");
            group1 = properties.getProperty("group1");
            group2 = properties.getProperty("group2");
            BSKPip = properties.getProperty("BSKPip");
            messageToBaud =properties.getProperty("messageToBaud");
            howMuchmessagesToBaud = Integer.parseInt(properties.getProperty("howMuchmessagesToBaud"));
            corecTProperties = NetworkUtil.checkIP(ip1Bind) & NetworkUtil.checkIP(ip2Bind);
            if (!corecTProperties)
                JOptionPane.showMessageDialog(null, "Неверный ip адрес или отсутствует сетевое соединение", "error ip host" + nameFileProperties, JOptionPane.ERROR_MESSAGE);
            printProperties();

        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Неверное значение параметров в конфиг. файле",  "Incorrect properties in file "+nameFileProperties, JOptionPane.ERROR_MESSAGE);
        }

    }

    private void printProperties() {
//        System.out.println("Load properties");
//        System.out.println("port: " + port);
//        System.out.println("timeOut: " + timeOut);
//        System.out.println("group1: "  + group1);
//        System.out.println("group2: "  + group2);
//        System.out.println("BSKP ip: "  + BSKPip);
//        System.out.println("device.ip.main: " + ip1Bind);
//        System.out.println("device.ip.standby: " + ip2Bind);
    }


//    public static void main(String[] args) {
//        PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties"); // Correct!
//    }

}

