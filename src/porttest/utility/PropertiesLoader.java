package porttest.utility;

import java.awt.Desktop;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import porttest.Test_Ethernet;

/**
 * класс загружающий и проверяющий настройки приложения
 */
public class PropertiesLoader {

    private boolean correctProperties;
    private int port;
    private int timeOut;
    private String ip1Bind;
    private String ip2Bind;
    private String group;
    private String BSKPip;
    private String messageToBaud;
    private int howMuchMessages;

    public boolean isCorecTProperties() {
        return correctProperties;
    }
    public int getPort() {
        return port;
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
    
     public String getGroup() {
        return group;
    }
    
    public String getBSKPip() {
        return BSKPip;
    }
    
    public String getMessageToBaud() {
        return messageToBaud;
    }

    public int getHowMuchmessagesToBaud() {
        return howMuchMessages;
    }
    
    public PropertiesLoader(String nameFileProperties)  {
        correctProperties = false;

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
            group = properties.getProperty("group");
            BSKPip = properties.getProperty("BSKPip");
            messageToBaud =properties.getProperty("messageToBaud");
            howMuchMessages = Integer.parseInt(properties.getProperty("howMuchmessagesToBaud"));
            correctProperties = NetworkUtil.checkIP(ip1Bind) & NetworkUtil.checkIP(ip2Bind);
            if (!correctProperties)
                //JOptionPane.showMessageDialog(null, "Неверный ip адрес или отсутствует сетевое соединение", "Ошибка соединения" + nameFileProperties, JOptionPane.ERROR_MESSAGE);
                System.out.println("Неверный ip адрес или отсутствует сетевое соединение!");
            printProperties();

        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Неверное имя или значение параметров в конфиг. файле!\nОткорректируйте конфиг.файл программы.\nИмена и значения параметров должны соответствовать шаблону:\nhost.ip.main = ваш ip адрес передающего адаптера, например 192.168.0.7 \nhost.ip.standby == ваш ip адрес принимающего адаптера, например 192.168.0.77\ngroup = 230.0.0.1\nport = 5001\ntimeOut = 1200\nmessageToBaud = Сообщение для передачи\nhowMuchmessagesToBaud = 20" ,  "Ошибка конфиг.файла "+nameFileProperties, JOptionPane.ERROR_MESSAGE);
            openConfig("./config_Test_Ethernet.properties");
            System.exit(555);
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

    public static void openConfig(String path2Config) {
        try {
            Runtime.getRuntime().exec("C:\\Program Files (x86)\\Notepad++\\notepad++.exe " + path2Config);

        } catch (IOException ex) {
            try {
                Runtime.getRuntime().exec("C:\\Program Files\\Notepad++\\notepad++.exe " + path2Config);
            } catch (IOException ex1) {
                System.out.println("Открыть конфиг. файл текстовым редактором по умолчанию");
                try {
                    Runtime.getRuntime().exec("C:\\Windows\\System32\\notepad.exe " + path2Config);
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }

//    public static void main(String[] args) {
//        PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties"); // Correct!
//    }

}

