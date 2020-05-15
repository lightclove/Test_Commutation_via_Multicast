package porttest;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import porttest.utility.NetworkUtil;
import porttest.utility.PropertiesLoader;

 
public class Test_Ethernet extends JFrame {

    private static final long serialVersionUID = 0L;
    private PropertiesLoader l;
    private JPanel mainPanel = null;
    private JPanel topPane = null;
    private JPanel bottomPane = null;
    private JPanel testConnectionsPane = null;
    private JPanel buttonsTestConnectionPane = null;
    private JPanel indicatorsTestConnectionPane = null;
    private JButton startTestConnectionsButton = null;
    private JButton stopTestConnectionsButton = null;
    private JPanel testConnectionsPort_1_IndicatorPane = null;
    private JPanel testConnectionsPort_2_IndicatorPane = null;
    private JLabel testConnectionsPort_1_IndicatorLabel;
    private JLabel testConnectionsPort_2_IndicatorLabel;
    private JPanel testDataBaudPane = null;
    private JPanel leftColumnLabelsOfTestDataBaudPane = null;
    private JLabel specifyMCastGroupLabel = null;
    private JTextArea specifyMCastGroupTextArea = null;
    private JLabel specifyBSKP_IpAddressLabel = null;
    private JPanel rightColumnTextAreasOfTestDataBaudPane = null;
    private JPanel buttonsTestDataBaudPane = null;
    private JButton startTestDataBaudButton = null;
    private JButton stopTestDataBaudButton = null;
    private JPanel buttonsIndicatorTestDataBaudPane = null;
    private JPanel howMuchMessagesToBaudPane = null;
    private JLabel howMuchMessagesToBaudLabel = null;
    private JCheckBox howMuchMessagesToBaudCheckBox = null;
    private JPanel paramsPane = null;
    private JLabel portNumberLabel = null;
    private JTextArea portNumberTextArea = null;
    private JTextArea specifyBSKP_IpAddressTextArea;
    private JPanel sendReceivePane = null;
    private JPanel sendPane = null;
    private JScrollPane sendScrollPane = null;
    private JPanel receivePane = null;
    private JScrollPane receiveScrollPane = null;
    private NotEditableOutputArea sendTextArea = null;
    private NotEditableOutputArea receiveTextArea = null;
    private JLabel buttonsTestDataBaudPaneLabel = null;
    private JComboBox howMuchMessagesToBaudCombobox = null;
  
    public Test_Ethernet() throws IOException {
        super();
        initialize();
    }

    private void initialize() throws IOException {
        this.setLocationRelativeTo(null);
        this.setSize(1000, 625);
        this.setContentPane(getMainPane());
        this.setLocationByPlatform(true); // окно посередине
        this.setTitle("Test_Ethernet");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setDefaultLookAndFeelDecorated(false);
        this.pack();
        this.addWindowListener(new WindowAdapter() { //
            public void windowClosing(WindowEvent event) {
                System.exit(0); // Чтобы убивался поток логики при закрытии окна
            }
        });
    }
    
    private JPanel getMainPane() throws IOException {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(getTopPane());
            mainPanel.add(getHowMuchMessagesToBaudPane());
            mainPanel.add(getSendReceivePane());
            //mainPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        }
        return mainPanel;
    }

    private JPanel getTopPane() throws IOException {
        if (topPane == null) {
            topPane = new JPanel();
            topPane.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            //topPane.setLayout(new FlowLayout());
            //topPane.add(getTestConnectionsPane());
            topPane.add(getTestDataBaudPane());
            //topPane.add(getParamsPane());
            topPane.setPreferredSize(new Dimension(1000, 200));
            //topPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return topPane;
    }

    private JPanel getTestConnectionsPane() throws IOException {
        if (testConnectionsPane == null) {
            testConnectionsPane = new JPanel();
            BoxLayout b = new BoxLayout(testConnectionsPane, BoxLayout.Y_AXIS);
            testConnectionsPane.setLayout(b);
            testConnectionsPane.add(getIndicatorsTestConnectionPane());
            //testConnectionsPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            testConnectionsPane.setPreferredSize(new Dimension(300, 150));
        }
        return testConnectionsPane;
    }


    volatile AtomicBoolean testConnections1Flag = new AtomicBoolean(true);
    volatile AtomicBoolean testConnections2Flag = new AtomicBoolean(true);
    Runnable taskTestConnections1;
    Runnable taskTestConnections2;
    int counter1 = 0;
    int counter2 = 0;
    volatile String lastErrorCount1;
    volatile String lastErrorCount2;


        
    private JButton getStopTestConnectionsButton() {
        if (stopTestConnectionsButton == null) {
            stopTestConnectionsButton = new JButton();
            stopTestConnectionsButton.setText("Стоп тест ");
            stopTestConnectionsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
//                        acceptedTCcounter1 = 0;
//                        failedTCtimeCounter1 = 0;
//                        acceptedTCcounter2 = 0;
//                        failedTCtimeCounter2 = 0;
                        testConnections1Flag.set(false);
                        testConnections2Flag.set(false);
                        
                        getStopTestConnectionsButton().setEnabled(false);
                }
            });
        }
        return stopTestConnectionsButton;
    }

       
    private JPanel getIndicatorsTestConnectionPane() {
        if (indicatorsTestConnectionPane == null) {
            indicatorsTestConnectionPane = new JPanel();
            indicatorsTestConnectionPane.setLayout(new FlowLayout());
            indicatorsTestConnectionPane.add(getTestConnectionsPort_1_IndicatorPane());
            indicatorsTestConnectionPane.add(getTestConnectionsPort_2_IndicatorPane());
            //indicatorsTestConnectionPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            //indicatorsTestConnectionPane.setPreferredSize(new Dimension(10, 10));
        }
        return indicatorsTestConnectionPane;
    }

    private JPanel getTestConnectionsPort_1_IndicatorPane() {
        if (testConnectionsPort_1_IndicatorPane == null) {
            testConnectionsPort_1_IndicatorPane = new JPanel(new FlowLayout(0, 20, 1));
            testConnectionsPort_1_IndicatorPane.add(getTestConnectionsPort_1_IndicatorLabel());
            testConnectionsPort_1_IndicatorPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            testConnectionsPort_1_IndicatorPane.setPreferredSize(new Dimension(120, 90));
        }
        return testConnectionsPort_1_IndicatorPane;
    }

    private JLabel getTestConnectionsPort_1_IndicatorLabel() {
        if (testConnectionsPort_1_IndicatorLabel == null) {
            testConnectionsPort_1_IndicatorLabel = new JLabel("<html> Состояние <br> Порта 1 БСКП:<br>Неизвестно<html>");
            testConnectionsPort_1_IndicatorLabel.setPreferredSize(new Dimension(120, 90));
        }
        return testConnectionsPort_1_IndicatorLabel;
    }

    private JPanel getTestConnectionsPort_2_IndicatorPane() {
        if (testConnectionsPort_2_IndicatorPane == null) {
            testConnectionsPort_2_IndicatorPane = new JPanel(new FlowLayout(0, 20, 1));
            testConnectionsPort_2_IndicatorPane.add(getTestConnectionsPort_2_IndicatorLabel());
            testConnectionsPort_2_IndicatorPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            testConnectionsPort_2_IndicatorPane.setPreferredSize(new Dimension(120, 90));
        }
        return testConnectionsPort_2_IndicatorPane;
    }

    private JLabel getTestConnectionsPort_2_IndicatorLabel() {
        if (testConnectionsPort_2_IndicatorLabel == null) {
            testConnectionsPort_2_IndicatorLabel = new JLabel("<html> Состояние <br> Порта 2 БСКП:<br>Неизвестно<html>");
            testConnectionsPort_2_IndicatorLabel.setPreferredSize(new Dimension(120, 90));
        }
        return testConnectionsPort_2_IndicatorLabel;
    }

    private JPanel getTestDataBaudPane() {
        if (testDataBaudPane == null) {
            testDataBaudPane = new JPanel();
            testDataBaudPane.setLayout(new BoxLayout(testDataBaudPane, BoxLayout.Y_AXIS));
            testDataBaudPane.add(getButtonsTestDataBaudPane());
            testDataBaudPane.add(getButtonsIndicatorTestDataBaudPane());
            //testDataBaudPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            testDataBaudPane.setPreferredSize(new Dimension(300, 150));
        }
        return testDataBaudPane;
    }

    private JPanel getButtonsTestDataBaudPane() {
        if (buttonsTestDataBaudPane == null) {
            buttonsTestDataBaudPane = new JPanel();
            buttonsTestDataBaudPane.setLayout(new FlowLayout());
            buttonsTestDataBaudPane.add(getStartTestDataBaudButton());
            buttonsTestDataBaudPane.add(getStopTestDataBaudButton());
        }
        return buttonsTestDataBaudPane;
    }

    static int acceptedDBcounter = 0;
    static int failedDBtimeCounter = 0;
    static int lastFailedDBtime;
    static int lastAcceptedDBtime;
    
    

    private void paintTestDataBaudPanel(boolean isTestDataBaudThreadRunning) {
        
        if (isTestDataBaudThreadRunning == true) {
            receiveTextArea.setForeground(null);
            getButtonsIndicatorTestDataBaudPane().setBackground(Color.GREEN);
            lastAcceptedDBtime = acceptedDBcounter;
            failedDBtimeCounter = 0;
            buttonsTestDataBaudPaneLabel.setText("ПЕРЕДАЧА и ПРИЕМ ");
        } else {
            //receiveTextArea.setText("");
            receiveTextArea.setForeground(Color.red);
            lastFailedDBtime = failedDBtimeCounter;
            acceptedDBcounter = 0;
            buttonsTestDataBaudPaneLabel.setText("НЕТ приема ");
            getButtonsIndicatorTestDataBaudPane().setBackground(null);
             
        }
        
    }

    volatile AtomicBoolean dataBaudFlag = new AtomicBoolean(true);
    Runnable task;

    private JButton getStartTestDataBaudButton() {
        l = new PropertiesLoader("./config_Test_Ethernet.properties");
        if (startTestDataBaudButton == null) {
            startTestDataBaudButton = new JButton();
            startTestDataBaudButton.setText("Старт");
            startTestDataBaudButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // SwingUtilities.invokeLater(new Runnable() {
                    //public void run() {

                    getStopTestConnectionsButton().setEnabled(false);
                    getStartTestDataBaudButton().setEnabled(false);
                    getStopTestDataBaudButton().setEnabled(true);
                    //getSpecifyMCastGroupTextArea().setEnabled(false);
                    //getPortNumberTextArea().setEnabled(false);
                    //getSpecifyBSKP_IpAddressTextArea().setEnabled(false);
                    getHowMuchMessageToBaudCombobox().setEnabled(false);
                    
                    task = () -> {
                    
                        //l = new PropertiesLoader("./config_Test_ethernet.properties");
                        MulticastSocket multicastSocket1 = null;
                        MulticastSocket multicastSocket2 = null;
                        String howMuchMessageToBaud = (String) getHowMuchMessageToBaudCombobox().getSelectedItem();

                        if (howMuchMessageToBaud.equals("Беск.")) {
                            howMuchMessageToBaud = new String("2147483647");
                        }

                        byte[] arrbytes = l.getMessageToBaud().getBytes(); // @TODO Заменить значением Поля "Укажите сообщение для передачи"
                        DatagramPacket pack = null;

                        int counter = 0;

                        while ((counter <= Integer.parseInt((String) howMuchMessageToBaud) - 1) && !Thread.currentThread().isInterrupted()) {
                            try {
                                if (!dataBaudFlag.get()) {
                                    paintTestDataBaudPanel(false);
                                    enableGUIElements(); // когда нажимаешь на кнопку, поток может еще что-то делать(например зависнут на нек-е время)
                                    getButtonsTestDataBaudPaneLabel().setText("<html>Тест остановлен</html>");
                                    getReceiveTextArea().setForeground(null);
                                    System.out.println("Thread has stopped on demand");
                                    return;
                                }

                                pack = new DatagramPacket(arrbytes, arrbytes.length, InetAddress.getByName(l.getGroup()),l.getPort());
                                //Integer.parseInt(getPortNumberTextArea().getText()));
                                multicastSocket1 = new MulticastSocket(l.getPort()); // String value to int
                                multicastSocket2 = new MulticastSocket(l.getPort()); // String value to int
                                multicastSocket1.setInterface(InetAddress.getByName(l.getIp1Bind())); 
                                multicastSocket2.setInterface(InetAddress.getByName(l.getIp2Bind()));
                                multicastSocket2.joinGroup(InetAddress.getByName(l.getGroup()));
                                multicastSocket1.setSoTimeout(l.getTimeOut());
                                multicastSocket2.setSoTimeout(l.getTimeOut());

                                counter++;
                                // Sending packets
                                multicastSocket1.send(pack);
                                TimeUnit.SECONDS.sleep(1);
 
                                System.out.println(counter + ". Отправка сообщения №" + counter + ": \"" + new String(arrbytes) + "\"");
                                //getSendTextArea().append(String.valueOf(counter) + ". " +" " + "Отправлено сообщение №" + String.valueOf(counter) + ": \"" + new String(arrbytes) + "\" \n");
                                getSendTextArea().appendColorText(String.valueOf(counter) + ". " +" " + "Отправка сообщение №" + String.valueOf(counter) + ": \"" + new String(arrbytes) + "\" \n", Color.BLACK);
                                
                                // Receiving packets
                                byte[] buf = new byte[1500];
                                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                                multicastSocket2.receive(datagramPacket);
                                System.out.print(counter + ". Сообщение №" + counter + " " + "получено:\"");
 
                                
                                //getReceiveTextArea().append(String.valueOf(counter) + ". " +  " Принято сообщение №" + counter + ": \"" + new String(arrbytes) + "\"\n");
                                getReceiveTextArea().appendColorText(String.valueOf(counter) + ". " +  " Принято сообщение №" + counter + ": \"" + new String(arrbytes) + "\"\n", Color.BLACK);
                                
                                byte[] arrBytes = new byte[datagramPacket.getLength()];
                                System.arraycopy(buf, 0, arrBytes, 0, arrBytes.length);
                                System.out.println(new String(arrBytes) + "\"");
                                 
                                paintTestDataBaudPanel(true);
//                                if (counter % 4 == 0) {
//                                    buttonsTestDataBaudPaneLabel.setText("Последнее время активации  = " + lastFailedDBtime + " сек.");
//                                }

                                // For alert's window with background
//                                if (counter == Integer.parseInt((String) howMuchMessageToBaud)) {
//                                    UIManager UI = new UIManager();
//                                    UI.put("OptionPane.background", Color.GREEN);
//                                    UI.put("Panel.background", Color.GREEN);
//                                    JOptionPane.showMessageDialog(null, "Тест успешно завершен!\n" + "Данные переданы и приняты через мультикаст сокет, что означает, что \n" + "функция коммутации БСКП работоспособна\n\n", "Результат теста", JOptionPane.INFORMATION_MESSAGE);
//                                    UI = null;
//                                }

                                //counter = 0;
                            } catch (Exception e) {
                                paintTestDataBaudPanel(false);
                                getSendTextArea().appendColorText(String.valueOf(++counter) + ". " + " " + "Попытка отправки сообщения № " + String.valueOf(counter) + ": \"" + new String(arrbytes) + "\" \n", Color.RED);
                                e.printStackTrace();

                                 getReceiveTextArea().appendColorText(counter + ". " + "Ошибка приема сообщения №" +counter+ ": "+ e.toString() + "\n", Color.RED);

                                try { 
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        paintTestDataBaudPanel(false);
                        enableGUIElements();
                        System.out.println("Thread has stopped on counter");

                    };

                    dataBaudFlag.set(true);
                    new Thread(task).start();
                    System.out.println("Thread has started");
                }
            });
        }
        return startTestDataBaudButton;
    }

    private JButton getStopTestDataBaudButton() {
        if (stopTestDataBaudButton == null) {
            stopTestDataBaudButton = new JButton();
            stopTestDataBaudButton.setText("Стоп");
            stopTestDataBaudButton.setEnabled(false);
            stopTestDataBaudButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if (stopTestDataBaudButton != null) {
                        dataBaudFlag.set(false);
                        getButtonsTestDataBaudPaneLabel().setText("Тест остановлен");
                    }
                }
            });
        }
        return stopTestDataBaudButton;
    }
    
    private JButton settingsButton = null;
    
    // открыть файл настроек в тектовом редакторе
    private JButton getSettingsButton() { // 

        if (settingsButton == null) {
            settingsButton = new JButton("<html>Настройки программы</html>");
            settingsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    PropertiesLoader.openConfig("./config_Test_Ethernet.properties");
                }
            });
        }
        return settingsButton;
    }
    
    private JButton clearSendReceiveButton = null;
    
    // очистить send и receive поля
    private JButton getClearSendReceiveButton() { // 

        if (clearSendReceiveButton == null) {
            clearSendReceiveButton = new JButton("<html>Очистить информационные поля</html>");
            clearSendReceiveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    sendTextArea.setText("");
                    receiveTextArea.setText("");
                }
            });
        }
        return clearSendReceiveButton;
    }
    
    private void enableGUIElements() {

        getStopTestConnectionsButton().setEnabled(true);
        getStartTestDataBaudButton().setEnabled(true);
        getStopTestDataBaudButton().setEnabled(false);
        //getSpecifyMCastGroupTextArea().setEnabled(true);
        //getPortNumberTextArea().setEnabled(true);
        //getSpecifyBSKP_IpAddressTextArea().setEnabled(true);
        getHowMuchMessageToBaudCombobox().setEnabled(true);
    }

    public JPanel getButtonsIndicatorTestDataBaudPane() {
        if (buttonsIndicatorTestDataBaudPane == null) {
            buttonsIndicatorTestDataBaudPane = new JPanel();
            buttonsIndicatorTestDataBaudPane.setLayout((new FlowLayout(40, 60, 35)));
            buttonsIndicatorTestDataBaudPane.add(getButtonsTestDataBaudPaneLabel());
            buttonsIndicatorTestDataBaudPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            buttonsIndicatorTestDataBaudPane.setPreferredSize(new Dimension(90, 90));
        }
        return buttonsIndicatorTestDataBaudPane;
    }

    private JLabel getButtonsTestDataBaudPaneLabel() {
        if (buttonsTestDataBaudPaneLabel == null) {
            buttonsTestDataBaudPaneLabel = new JLabel(" ");
            //buttonsTestDataBaudPaneLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return buttonsTestDataBaudPaneLabel;
    }

//    private JPanel getParamsPane() {
//        if (paramsPane == null) {
//            paramsPane = new JPanel();
//            paramsPane.setLayout(new BoxLayout(paramsPane, BoxLayout.Y_AXIS));
//            //paramsPane.setLayout(new GridLayout(3 , 2, 0, 0));
//            JPanel specifyMCastGroupPane = new JPanel(new FlowLayout());
//            //specifyMCastGroupPane.add(getSpecifyMCastGroupLabel());
//            //specifyMCastGroupPane.add(getSpecifyMCastGroupTextArea());
//            JPanel portNumberPane = new JPanel(new FlowLayout());
//            portNumberPane.add(getPortNumberLabel());
//            portNumberPane.add(getPortNumberTextArea());
//            JPanel specifyBSKP_IpAddressPane = new JPanel(new FlowLayout());
//            specifyBSKP_IpAddressPane.add(getSpecifyBSKP_IpAddressLabel());
//            specifyBSKP_IpAddressPane.add(getSpecifyBSKP_IpAddressTextArea());
//            paramsPane.add(specifyMCastGroupPane);
//            paramsPane.add(portNumberPane);
//            paramsPane.add(specifyBSKP_IpAddressPane);
//            paramsPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
//
//        }
//        return paramsPane;
//    }
//
//    private JLabel getSpecifyMCastGroupLabel() {
//        if (specifyMCastGroupLabel == null) {
//            specifyMCastGroupLabel = new JLabel("Укажите IP-адрес мультикаст группы:");
//        }
//        return specifyMCastGroupLabel;
//    }

//    private JTextArea getSpecifyMCastGroupTextArea() {
//        if (specifyMCastGroupTextArea == null) {
//            PropertiesLoader l = new PropertiesLoader("./config_Test_ethernet.properties");
//            specifyMCastGroupTextArea = new JTextArea(l.getGroup());
//            specifyMCastGroupTextArea.setPreferredSize(new Dimension(80, 20));
//            specifyMCastGroupTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
//        }
//        return specifyMCastGroupTextArea;
//    }

//    private JLabel getPortNumberLabel() {
//        if (portNumberLabel == null) {
//            portNumberLabel = new JLabel("Укажите номер IP порта для передачи:");
//
//        }
//        return portNumberLabel;
//    }

//    private JTextArea getPortNumberTextArea() {
//        if (portNumberTextArea == null) {
//            PropertiesLoader l = new PropertiesLoader("./config_Test_ethernet.properties");
//            String port = null;
//            portNumberTextArea = new JTextArea(port.valueOf(l.getPort())); // int to the String parameter
//            portNumberTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
//        }
//        portNumberTextArea.setPreferredSize(new Dimension(80, 20));
//        return portNumberTextArea;
//    }

//    private JLabel getSpecifyBSKP_IpAddressLabel() {
//        if (specifyBSKP_IpAddressLabel == null) {
//            specifyBSKP_IpAddressLabel = new JLabel("Укажите IP-адрес БСКП:");
//        }
//        return specifyBSKP_IpAddressLabel;
//    }

//    private JTextArea getSpecifyBSKP_IpAddressTextArea() {
//        PropertiesLoader l = new PropertiesLoader("./config_Test_ethernet.properties");
//        String bskpIP = l.getBSKPip();
//        if (specifyBSKP_IpAddressTextArea == null) {
//            specifyBSKP_IpAddressTextArea = new JTextArea(bskpIP);
//            specifyBSKP_IpAddressTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
//        }
//        specifyBSKP_IpAddressTextArea.setPreferredSize(new Dimension(100, 20));
//        return specifyBSKP_IpAddressTextArea;
//    }

    private JPanel getSendPane() {
        if (sendPane == null) {
            sendPane = new JPanel();
            sendScrollPane = new JScrollPane(getSendTextArea());
            BoxLayout b = new BoxLayout(sendPane, BoxLayout.Y_AXIS);
            sendPane.setLayout(b);
            sendPane.add(new JLabel("Отправка на порт Eth1"));
            //sendScrollPane.add(getSendTextArea());
            sendScrollPane.setPreferredSize(new Dimension(490, 380));
            sendPane.setPreferredSize(new Dimension(490, 380));
            sendPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            sendPane.add(sendScrollPane);
        }
        return sendPane;
    }

    private JPanel getReceivePane() {
        if (receivePane == null) {
            receivePane = new JPanel();
            receiveScrollPane = new JScrollPane(getReceiveTextArea());
            BoxLayout b = new BoxLayout(receivePane, BoxLayout.Y_AXIS);
            receivePane.setLayout(b);
            receivePane.add(new JLabel("Прием на порт Eth2"));
            receiveScrollPane.setPreferredSize(new Dimension(490, 380));
            receivePane.setPreferredSize(new Dimension(490, 380));
            receivePane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            receivePane.add(receiveScrollPane);
        }
        return receivePane;
    }
    
        private NotEditableOutputArea getSendTextArea() {
        if (sendTextArea == null) {
            sendTextArea = new NotEditableOutputArea();
            sendTextArea.setPreferredSize(new Dimension(390, 290));
            sendTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return sendTextArea;
    }

    private NotEditableOutputArea getReceiveTextArea() {

        if (receiveTextArea == null) {
            receiveTextArea = new NotEditableOutputArea();
            sendTextArea.setPreferredSize(new Dimension(390, 290));;
            receiveTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return receiveTextArea;
    }

    private JPanel getHowMuchMessagesToBaudPane() {
        if (howMuchMessagesToBaudPane == null) {
            howMuchMessagesToBaudPane = new JPanel();
            howMuchMessagesToBaudPane.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            howMuchMessagesToBaudPane.add(getHowMuchMessagesToBaudLabel());
            howMuchMessagesToBaudPane.add(getHowMuchMessageToBaudCombobox());
            //howMuchMessagesToBaudPane.add(getSettingsButton());
            howMuchMessagesToBaudPane.add(getClearSendReceiveButton());
            //howMuchMessagesToBaudPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return howMuchMessagesToBaudPane;
    }

    private JComboBox getHowMuchMessageToBaudCombobox() {
        if (howMuchMessagesToBaudCombobox == null) {
            howMuchMessagesToBaudCombobox = new JComboBox(new String[]{"1", "5", "10", "20", "50", "100", "1000", "5000", "10000", "30000", "100000", "Беск."});
            //howMuchMessagesToBaudCombobox.setSelectedItem(howMuchMessagesToBaudCombobox.getItemAt(howMuchMessagesToBaudCombobox.getItemCount() - 1)); // "Беск." value by default to choose
            howMuchMessagesToBaudCombobox.setSelectedItem(howMuchMessagesToBaudCombobox.getItemAt(howMuchMessagesToBaudCombobox.getItemCount() - 1));
        }
        howMuchMessagesToBaudCombobox.setPreferredSize(new Dimension(60, 20));
        return howMuchMessagesToBaudCombobox;
    }

    private JLabel getHowMuchMessagesToBaudLabel() {
        if (howMuchMessagesToBaudLabel == null) {
            howMuchMessagesToBaudLabel = new JLabel("Выберите количество сообщений для передачи");
        }
        return howMuchMessagesToBaudLabel;
    }

    private JPanel getSendReceivePane() {
        if (sendReceivePane == null) {
            sendReceivePane = new JPanel();
            sendReceivePane.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
            sendReceivePane.add(getSendPane());
            sendReceivePane.add(getReceivePane());
            //sendReceivePane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            sendReceivePane.setPreferredSize(new Dimension(1000, 400));
        }
        return sendReceivePane;
    }
    
    class NotEditableOutputArea extends JTextPane {

        public NotEditableOutputArea() {
            setEditable(false);
            DefaultCaret caret = (DefaultCaret) getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

            Font defaultFont = new Font("monospaced", Font.PLAIN, 12);
            setFont(defaultFont);
            setCaretPosition(getDocument().getLength());
        }

        public void appendColorText(String text, Color c) {
            StyledDocument doc = getStyledDocument();
            Style style = addStyle("Style", null);
            StyleConstants.setForeground(style, c);
            try {
                doc.insertString(doc.getLength(), text, style);

            } catch (BadLocationException e) {
            }
            /*  AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        //attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontFamily, "Lucida Console");
        //attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        int len = getDocument().getLength();
        setCaretPosition(len);
        setCharacterAttributes(attributeSet, false);
        replaceSelection(text);*/
        }

        public void setColorText(String text, Color c) {
            setText(null);
            appendColorText(text, c);
        }
    }

    public static void main(String args[]) {

            SwingUtilities.invokeLater(() -> { // 
            //@Override
            //public void run() {
                try {
                    //TestDataBaud thisClass = new Test_Ethernet();
                    Test_Ethernet thisClass = new Test_Ethernet();
                    thisClass.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    thisClass.setResizable(false);
                    thisClass.setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            //}
        });
    }

} 
