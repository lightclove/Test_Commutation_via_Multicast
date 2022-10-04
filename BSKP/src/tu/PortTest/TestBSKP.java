package porttest;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import porttest.utility.NetworkUtil;
import porttest.utility.PropertiesLoader;
import porttest.utility.SoundUtils;
 
public class TestBSKP extends JFrame {

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
    private JPanel sendScrollPane = null;
    private JPanel receiveScrollPane = null;
    private TextArea sendTextArea = null;
    private TextArea receiveTextArea = null;
    private JLabel buttonsTestDataBaudPaneLabel = null;
    private JComboBox howMuchMessagesToBaudCombobox = null;
    private JButton pingPort1Button  ;
    private JButton pingPort2Button = null;;

    public TestBSKP() throws IOException {
        super();
        this.l = new PropertiesLoader("./configTestBSKP.properties");
        initialize();
    }

    private void initialize() throws IOException {
        this.setLocationRelativeTo(null);
        this.setSize(1000, 625);
        this.setContentPane(getMainPane());
        this.setLocationByPlatform(true); // окно посередине
        this.setTitle("Тестирование коммутации БСКП согласно ТУ");
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
            topPane.add(getTestConnectionsPane());
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
            testConnectionsPane.add(getButtonsTestConnectionPane());
            testConnectionsPane.add(getIndicatorsTestConnectionPane());
            //testConnectionsPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            testConnectionsPane.setPreferredSize(new Dimension(300, 150));
        }
        return testConnectionsPane;
    }

    private JPanel getButtonsTestConnectionPane() throws IOException {
        if (buttonsTestConnectionPane == null) {
            buttonsTestConnectionPane = new JPanel();
            buttonsTestConnectionPane.setLayout(new FlowLayout()); // 
            buttonsTestConnectionPane.add(getStartTestConnectionsButton());
            buttonsTestConnectionPane.add(getStopTestConnectionsButton());
            //buttonsTestConnectionPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return buttonsTestConnectionPane;
    }

    static int acceptedTCcounter1 = 0;
    static int failedTCtimeCounter1 = 0;
    static int lastFailedTCtime1;
    static int lastAcceptedTCtime1;

    static int acceptedTCcounter2 = 0;
    static int failedTCtimeCounter2 = 0;
    static int lastFailedTCtime2;
    static int lastAcceptedTCtime2;
    

    private void paintTestConnectionsPanel1(boolean isTestDataBaudThreadRunning) throws InterruptedException, LineUnavailableException {
        //if (!inet.isReachable(NetworkInterface.getByName(l.getIp2Bind()), 128, 1100)) { //физ.линк с конкретного айпишника
       if (isTestDataBaudThreadRunning == true) {
            getTestConnectionsPort_1_IndicatorPane().setBackground(Color.GREEN);
            SoundUtils.tone(1000, 1000, acceptedTCcounter1);
            lastFailedTCtime1 = acceptedTCcounter1;
            failedTCtimeCounter1 = 0;
            getTestConnectionsPort_1_IndicatorLabel().setText("<html>Порт 1<br>активен<br>" + (++acceptedTCcounter1) + " сек.<html>");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            getSendTextArea().append( sdf.format(cal.getTime()) + ": Порт 1 активен " + acceptedTCcounter1 + " сек.\n");
        }
       if (isTestDataBaudThreadRunning == false) {
            lastFailedTCtime1 = failedTCtimeCounter1;
            acceptedTCcounter1 = 0;
            getTestConnectionsPort_1_IndicatorLabel().setText("<html>Порт 1<br>НЕ АКТИВЕН<br>" + (++failedTCtimeCounter1) + " сек.<html>");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            getSendTextArea().append(sdf.format(cal.getTime()) + ": Порт 1 НЕ АКТИВЕН " + failedTCtimeCounter1 + " сек.\n");
            getTestConnectionsPort_1_IndicatorPane().setBackground(Color.RED);
        }
    }

    private void paintTestConnectionsPanel2(boolean isTestDataBaudThreadRunning) throws InterruptedException, LineUnavailableException {
        //if (!inet.isReachable(NetworkInterface.getByName(l.getBSKPip()), 128, 1100)) { //физ.линк с конкретного айпишника
        if (isTestDataBaudThreadRunning == true) {
            getTestConnectionsPort_2_IndicatorPane().setBackground(Color.GREEN);
            SoundUtils.tone(1000, 1000, acceptedTCcounter2);
            lastFailedTCtime2 = acceptedTCcounter2;
            failedTCtimeCounter2 = 0;
            getTestConnectionsPort_2_IndicatorLabel().setText("<html>Порт 2<br>активен<br>" + ++acceptedTCcounter2 + " сек.<html>");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            getReceiveTextArea().append(sdf.format(cal.getTime()) + ": Порт 2 активен " + acceptedTCcounter2 + " сек.\n");

        } else {
            lastFailedTCtime2 = failedTCtimeCounter2;
            acceptedTCcounter2 = 0;
            getTestConnectionsPort_2_IndicatorLabel().setText("<html>Порт 2<br>НЕ АКТИВЕН<br>" + ++failedTCtimeCounter2 + " сек.<html>");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            getReceiveTextArea().append(sdf.format(cal.getTime()) + ": Порт 2 активен " + failedTCtimeCounter2 + " сек.\n");
            getTestConnectionsPort_2_IndicatorPane().setBackground(Color.RED);
 
        }
    }

    volatile AtomicBoolean testConnections1Flag = new AtomicBoolean(true);
    volatile AtomicBoolean testConnections2Flag = new AtomicBoolean(true);
    Runnable taskTestConnections1;
    Runnable taskTestConnections2;
    int counter1 = 0;
    int counter2 = 0;
    volatile String lastErrorCount1;
    volatile String lastErrorCount2;

    private JButton getStartTestConnectionsButton() {
        
        if (startTestConnectionsButton == null) {
            startTestConnectionsButton = new JButton();
//            stopTestConnectionsButton.setEnabled(false);
            startTestConnectionsButton.setText("Тест подключения");
            startTestConnectionsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            
                            getStartTestConnectionsButton().setEnabled(false);
                            getStopTestConnectionsButton().setEnabled(true);
                            l = new PropertiesLoader("./configTestBSKP.properties");

                            taskTestConnections1 = () -> {
                                int upCounter1 = 0;
                                while ((counter1 <= 2147483647) && !Thread.currentThread().isInterrupted()) {
                                    try {
                                        if (!testConnections1Flag.get()) {
                                            paintTestConnectionsPanel1(false);
                                            System.out.println("Thread has stopped on demand");
                                            getTestConnectionsPort_1_IndicatorPane().setBackground(getMainPane().getBackground());
                                            getTestConnectionsPort_1_IndicatorLabel().setText("<html>Порт 1<br>Тест <br>не выполняется</html> "); 
                                            return;
                                        }
                                         
                                        //if (!inet.isReachable(NetworkInterface.getByName(l.getBSKPip()), 128, 1100)) { //физ.линк с конкретного айпишника
                                        if (NetworkUtil.ping(l.getIp1Bind(), l.getIp2Bind()) == false) {
                                            paintTestConnectionsPanel1(false); //testConnectionsPort_1_IndicatorPane.setBackground(Color.RED);
                                        } else {
                                            paintTestConnectionsPanel1(true); //testConnectionsPort_1_IndicatorPane.setBackground(Color.GREEN);
                                        }

                                    }catch (InterruptedException ex) {
                                        try {
                                            paintTestConnectionsPanel1(false);//testConnectionsPort_1_IndicatorPane.setBackground(Color.RED);
                                        } catch (InterruptedException ex1) {
                                            ex.printStackTrace();
                                            ex1.printStackTrace();
                                        } catch (LineUnavailableException ex1) {
                                            ex1.printStackTrace();
                                        }
                                    } catch (LineUnavailableException ex) {
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                            }; // end of taskTestConnections1 = () -> {}

                            taskTestConnections2 = () -> {
                                int upCounter2 = 0;
                                while ((counter2 <= 2147483647) && !Thread.currentThread().isInterrupted()) {
                                    try {
                                        if (!testConnections2Flag.get()) {
                                            paintTestConnectionsPanel2(false); //testConnectionsPort_2_IndicatorPane.setBackground(Color.RED);
                                            System.out.println("Thread has stopped on demand");
                                            getTestConnectionsPort_2_IndicatorPane().setBackground(getMainPane().getBackground());
                                            getTestConnectionsPort_2_IndicatorLabel().setText("<html>Порт 2<br>Тест <br>не выполняется</html> "); 
                                            return;
                                        }

                                        //if (!inet.isReachable(NetworkInterface.getByName(l.getBSKPip()), 128, 1100)) { //физ.линк с конкретного айпишника
                                        if (NetworkUtil.ping(l.getIp2Bind(), l.getIp1Bind()) == false) {
                                            paintTestConnectionsPanel2(false);//testConnectionsPort_2_IndicatorPane.setBackground(Color.RED);
                                        } else {
                                            paintTestConnectionsPanel2(true);//testConnectionsPort_2_IndicatorPane.setBackground(Color.GREEN);
                                        }
                                    }
                                     catch (InterruptedException ex) {
                                        try {
                                            paintTestConnectionsPanel2(false);//testConnectionsPort_2_IndicatorPane.setBackground(Color.RED);
                                        } catch (InterruptedException ex1) {
                                            ex.printStackTrace();
                                            ex1.printStackTrace();
                                        } catch (LineUnavailableException ex1) {
                                            ex1.printStackTrace();
                                        }
                                    } catch (LineUnavailableException ex) {
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                            }; // end of taskTestConnections2 = () -> {}
                            new Thread(taskTestConnections1).start();
                            new Thread(taskTestConnections2).start();
                            System.out.println("Thread 1 has started");
                            System.out.println("Thread 2 has started");
                            testConnections1Flag.set(true);
                            testConnections2Flag.set(true);
                        }

                    });
                }
            });
        }
        return startTestConnectionsButton;
    }
        
    private JButton getStopTestConnectionsButton() {
        if (stopTestConnectionsButton == null) {
            stopTestConnectionsButton = new JButton();
            stopTestConnectionsButton.setText("Стоп тест ");
            stopTestConnectionsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                        acceptedTCcounter1 = 0;
                        failedTCtimeCounter1 = 0;
                        acceptedTCcounter2 = 0;
                        failedTCtimeCounter2 = 0;
                        testConnections1Flag.set(false);
                        testConnections2Flag.set(false);
                        
                        getStopTestConnectionsButton().setEnabled(false);
                        getStartTestConnectionsButton().setEnabled(true);
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
            getButtonsIndicatorTestDataBaudPane().setBackground(Color.GREEN);
            lastAcceptedDBtime = acceptedDBcounter;
            failedDBtimeCounter = 0;
            buttonsTestDataBaudPaneLabel.setText("Идет передача данных... " + ++acceptedDBcounter + " сек.");
        } else {
            lastFailedDBtime = failedDBtimeCounter;
            acceptedDBcounter = 0;
            buttonsTestDataBaudPaneLabel.setText("<html>Передача данных прервана<br>или не осуществляется " + ++failedDBtimeCounter + " сек. </html>");
            getButtonsIndicatorTestDataBaudPane().setBackground(Color.RED);
        }
    }

    volatile AtomicBoolean dataBaudFlag = new AtomicBoolean(true);
    Runnable task;

    private JButton getStartTestDataBaudButton() {
        if (startTestDataBaudButton == null) {
            startTestDataBaudButton = new JButton();
            startTestDataBaudButton.setText("Тест передачи данных");
            startTestDataBaudButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // SwingUtilities.invokeLater(new Runnable() {
                    //public void run() {
                    getStartTestConnectionsButton().setEnabled(false);
                    getStopTestConnectionsButton().setEnabled(false);
                    getStartTestDataBaudButton().setEnabled(false);
                    getStopTestDataBaudButton().setEnabled(true);
                    getSpecifyMCastGroupTextArea().setEnabled(false);
                    getPortNumberTextArea().setEnabled(false);
                    getSpecifyBSKP_IpAddressTextArea().setEnabled(false);
                    getHowMuchMessageToBaudCombobox().setEnabled(false);
                    task = () -> {
                        l = new PropertiesLoader("./configTestBSKP.properties");
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
                                    System.out.println("Thread has stopped on demand");
                                    return;
                                }

                                pack = new DatagramPacket(arrbytes, arrbytes.length, InetAddress.getByName(getSpecifyMCastGroupTextArea().getText()),
                                        Integer.parseInt(getPortNumberTextArea().getText()));
                                multicastSocket1 = new MulticastSocket(l.getPort()); // String value to int
                                multicastSocket2 = new MulticastSocket(l.getPort()); // String value to int
                                multicastSocket1.setInterface(InetAddress.getByName(l.getIp1Bind())); // 
                                multicastSocket2.setInterface(InetAddress.getByName(l.getIp2Bind())); // @TODO Заменить на значения комбо с айпишниками
                                multicastSocket2.joinGroup(InetAddress.getByName(getSpecifyMCastGroupTextArea().getText()));
                                multicastSocket1.setSoTimeout(l.getTimeOut());
                                multicastSocket2.setSoTimeout(l.getTimeOut());

                                counter++;
                                // Sending packets
                                multicastSocket1.send(pack);
                                TimeUnit.SECONDS.sleep(1);
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                System.out.println(counter + ". Отправка сообщения №" + counter + ": \"" + new String(arrbytes) + "\"");
                                //LogPrinterWrapper.print("Отправка сообщения №" + testDataBaudTimer + ": \"" + new String(arrbytes) + "\"", "TestBSKP.log");
                                getSendTextArea().append(String.valueOf(counter) + ". " + sdf.format(cal.getTime()) +" " + "Отправка сообщения №" + String.valueOf(counter) + ": \"" + new String(arrbytes) + "\" \n");

                                // Receiving packets
                                byte[] buf = new byte[1500];
                                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                                multicastSocket2.receive(datagramPacket);
                                System.out.print(counter + ". Сообщение №" + counter + " " + "получено:\"");
                                cal = Calendar.getInstance();
                                sdf = new SimpleDateFormat("HH:mm:ss");
                                getReceiveTextArea().append(String.valueOf(counter) + ". " + sdf.format(cal.getTime())  + " : Прием сообщения №" + counter + ": \"" + new String(arrbytes) + "\"\n");

                                byte[] arrBytes = new byte[datagramPacket.getLength()];
                                System.arraycopy(buf, 0, arrBytes, 0, arrBytes.length);
                                System.out.println(new String(arrBytes) + "\"");
                                paintTestDataBaudPanel(true);
                                if (counter % 4 == 0) {
                                    buttonsTestDataBaudPaneLabel.setText("Последнее время активации  = " + lastFailedDBtime + " сек.");
                                }

                                // For alert's window with background
                                if (counter == Integer.parseInt((String) howMuchMessageToBaud)) {
                                    UIManager UI = new UIManager();
                                    UI.put("OptionPane.background", Color.GREEN);
                                    UI.put("Panel.background", Color.GREEN);
                                    JOptionPane.showMessageDialog(null, "Тест успешно завершен!\n" + "Данные переданы и приняты через мультикаст сокет, что означает, что \n" + "функция коммутации БСКП работоспособна\n\n", "Результат теста", JOptionPane.INFORMATION_MESSAGE);
                                    UI = null;
                                }

                                //counter = 0;
                            } catch (Exception e) {

                                //try {// из-за LogPrinterWrapper.print(...)
                                getSendTextArea().append(String.valueOf(++counter) + ". " + " " + ": Отправка сообщения № " + String.valueOf(counter) + ": \"" + new String(arrbytes) + "\" \n");
                                //      LogPrinterWrapper.print(testDataBaudTimer + ".Отправка сообщения № " + testDataBaudTimer + ": \"" + new String(arrbytes) + "\"", "TestBSKP.log");
                                e.printStackTrace();
                                paintTestDataBaudPanel(false);
                                if (counter % 6 == 0) {
                                    buttonsTestDataBaudPaneLabel.setText("<html>Последнее время<br>передачи = <html>" + lastAcceptedDBtime + " сек.");
                                }
                                 receiveTextArea.append(counter + ". " + e.toString() + "\n");
 
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
                    }
                }
            });
        }
        return stopTestDataBaudButton;
    }
    
    private JButton settingsButton = null;

    private JButton getSettingsButton() { // для удобства, на скорую руку

        if (settingsButton == null) {
            settingsButton = new JButton("<html>Настройки программы</html>");
            settingsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    try {
                        Runtime.getRuntime().exec("C:\\Program Files (x86)\\Notepad++\\notepad++.exe " + "./configTestBSKP.properties");
                        
                    } catch (IOException ex) {
                        try {
                            Runtime.getRuntime().exec("C:\\Program Files\\Notepad++\\notepad++.exe " + "./configTestBSKP.properties");
                        } catch (IOException ex1) {
                            try {
                                Runtime.getRuntime().exec("C:\\Windows\\System32\\notepad.exe " + "./configTestBSKP.properties");
                            } catch (IOException ex2) {
                                Logger.getLogger(TestBSKP.class.getName()).log(Level.SEVERE, null, ex2);
                            }
                        }
                    }
                }
            });
        }
        return settingsButton;
    }

    private void enableGUIElements() {
        getStartTestConnectionsButton().setEnabled(true);
        getStopTestConnectionsButton().setEnabled(true);
        getStartTestDataBaudButton().setEnabled(true);
        getStopTestDataBaudButton().setEnabled(false);
        getSpecifyMCastGroupTextArea().setEnabled(true);
        getPortNumberTextArea().setEnabled(true);
        getSpecifyBSKP_IpAddressTextArea().setEnabled(true);
        getHowMuchMessageToBaudCombobox().setEnabled(true);
    }

    public JPanel getButtonsIndicatorTestDataBaudPane() {
        if (buttonsIndicatorTestDataBaudPane == null) {
            buttonsIndicatorTestDataBaudPane = new JPanel();
            buttonsIndicatorTestDataBaudPane.setLayout((new FlowLayout(40, 60, 35))); /// !!!!!!!!!!!!!!!!!!!!!!!/// !!!!!!!!!!!!!!!!!!!!!!!/// !!!!!!!!!!!!!!!!!!!!!!!/// !! 
            buttonsIndicatorTestDataBaudPane.add(getButtonsTestDataBaudPaneLabel());
            buttonsIndicatorTestDataBaudPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            buttonsIndicatorTestDataBaudPane.setPreferredSize(new Dimension(90, 90));
        }
        return buttonsIndicatorTestDataBaudPane;
    }

    private JLabel getButtonsTestDataBaudPaneLabel() {
        if (buttonsTestDataBaudPaneLabel == null) {
            buttonsTestDataBaudPaneLabel = new JLabel("<html>Состояние передачи данных: <br>Не выполняется!</html>");
            //buttonsTestDataBaudPaneLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return buttonsTestDataBaudPaneLabel;
    }

    private JPanel getParamsPane() {
        if (paramsPane == null) {
            paramsPane = new JPanel();
            paramsPane.setLayout(new BoxLayout(paramsPane, BoxLayout.Y_AXIS));
            //paramsPane.setLayout(new GridLayout(3 , 2, 0, 0));
            JPanel specifyMCastGroupPane = new JPanel(new FlowLayout());
            specifyMCastGroupPane.add(getSpecifyMCastGroupLabel());
            specifyMCastGroupPane.add(getSpecifyMCastGroupTextArea());
            JPanel portNumberPane = new JPanel(new FlowLayout());
            portNumberPane.add(getPortNumberLabel());
            portNumberPane.add(getPortNumberTextArea());
            JPanel specifyBSKP_IpAddressPane = new JPanel(new FlowLayout());
            specifyBSKP_IpAddressPane.add(getSpecifyBSKP_IpAddressLabel());
            specifyBSKP_IpAddressPane.add(getSpecifyBSKP_IpAddressTextArea());
            paramsPane.add(specifyMCastGroupPane);
            paramsPane.add(portNumberPane);
            paramsPane.add(specifyBSKP_IpAddressPane);
            paramsPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));

        }
        return paramsPane;
    }

    private JLabel getSpecifyMCastGroupLabel() {
        if (specifyMCastGroupLabel == null) {
            specifyMCastGroupLabel = new JLabel("Укажите IP-адрес мультикаст группы:");
        }
        return specifyMCastGroupLabel;
    }

    private JTextArea getSpecifyMCastGroupTextArea() {
        if (specifyMCastGroupTextArea == null) {
            PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties");
            specifyMCastGroupTextArea = new JTextArea(l.getGroup2());
            specifyMCastGroupTextArea.setPreferredSize(new Dimension(80, 20));
            specifyMCastGroupTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return specifyMCastGroupTextArea;
    }

    private JLabel getPortNumberLabel() {
        if (portNumberLabel == null) {
            portNumberLabel = new JLabel("Укажите номер IP порта для передачи:");

        }
        return portNumberLabel;
    }

    private JTextArea getPortNumberTextArea() {
        if (portNumberTextArea == null) {
            PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties");
            String port = null;
            portNumberTextArea = new JTextArea(port.valueOf(l.getPort())); // int to the String parameter
            portNumberTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        portNumberTextArea.setPreferredSize(new Dimension(80, 20));
        return portNumberTextArea;
    }

    private JLabel getSpecifyBSKP_IpAddressLabel() {
        if (specifyBSKP_IpAddressLabel == null) {
            specifyBSKP_IpAddressLabel = new JLabel("Укажите IP-адрес БСКП:");
        }
        return specifyBSKP_IpAddressLabel;
    }

    private JTextArea getSpecifyBSKP_IpAddressTextArea() {
        PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties");
        String bskpIP = l.getBSKPip();
        if (specifyBSKP_IpAddressTextArea == null) {
            specifyBSKP_IpAddressTextArea = new JTextArea(bskpIP);
            specifyBSKP_IpAddressTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        specifyBSKP_IpAddressTextArea.setPreferredSize(new Dimension(100, 20));
        return specifyBSKP_IpAddressTextArea;
    }

    private JPanel getSendScrollPane() {
        if (sendScrollPane == null) {
            sendScrollPane = new JPanel();
            BoxLayout b = new BoxLayout(sendScrollPane, BoxLayout.Y_AXIS);
            sendScrollPane.setLayout(b);
            sendScrollPane.add(getSendTextArea());
            sendScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));

        }
        return sendScrollPane;
    }

    private TextArea getSendTextArea() {
        if (sendTextArea == null) {
            sendTextArea = new TextArea();
            sendTextArea.setPreferredSize(new Dimension(470, 290));
            sendTextArea.setEditable(false);
           // sendTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return sendTextArea;
    }

    private JPanel getReceiveScrollPane() {
        if (receiveScrollPane == null) {
            receiveScrollPane = new JPanel();
            BoxLayout b = new BoxLayout(receiveScrollPane, BoxLayout.Y_AXIS);
            receiveScrollPane.setLayout(b);
            receiveScrollPane.add(getReceiveTextArea());
            receiveScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return receiveScrollPane;
    }

    private TextArea getReceiveTextArea() {

        if (receiveTextArea == null) {
            receiveTextArea = new TextArea();
            receiveTextArea.setPreferredSize(new Dimension(470, 290));
            receiveTextArea.setEditable(false);
            //receiveTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return receiveTextArea;
    }

    private JPanel getHowMuchMessagesToBaudPane() {
        if (howMuchMessagesToBaudPane == null) {
            howMuchMessagesToBaudPane = new JPanel();
            howMuchMessagesToBaudPane.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            howMuchMessagesToBaudPane.add(getHowMuchMessagesToBaudLabel());
            howMuchMessagesToBaudPane.add(getHowMuchMessageToBaudCombobox());
            howMuchMessagesToBaudPane.add(getSettingsButton());
            //howMuchMessagesToBaudPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
        }
        return howMuchMessagesToBaudPane;
    }

    private JComboBox getHowMuchMessageToBaudCombobox() {
        if (howMuchMessagesToBaudCombobox == null) {
            howMuchMessagesToBaudCombobox = new JComboBox(new String[]{"1", "5", "10", "20", "50", "100", "1000", "Беск."});
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
            sendReceivePane.add(getSendScrollPane());
            sendReceivePane.add(getReceiveScrollPane());
            //sendReceivePane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
            sendReceivePane.setPreferredSize(new Dimension(1000, 400));
        }
        return sendReceivePane;
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            try {
                //TestDataBaud thisClass = new TestBSKP();
                TestBSKP thisClass = new TestBSKP();
                
                thisClass.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                thisClass.setResizable(false);
                thisClass.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
         
        });
    }

} 
