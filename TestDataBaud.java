package ru.nppame.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
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
import javax.swing.text.DefaultCaret;

import ru.nppame.utility.PropertiesLoader;

public class TestDataBaud extends JFrame {
    private static final long serialVersionUID = 0L;

    private PropertiesLoader l = null;

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

    private JLabel port1UptimeLabel = null;

    private JLabel port2UptimeLabel = null;

    private int testDataBaudTimer = 0;

    volatile AtomicBoolean testConnectionsFlag1;

    volatile AtomicBoolean testConnectionsFlag2;

    Runnable taskTestConnections1;

    Runnable taskTestConnections2;

    int counter1;

    int counter2;

    volatile String lastErrorCount1;

    volatile String lastErrorCount2;

    private JPanel buttonsCounterStorageTestDataBaudPane;

    volatile AtomicBoolean dataBaudFlag;

    Runnable task;

    private JPanel port1UptimePane;

    private JPanel port2UptimePane;

    private void initialize() {
        setLocationRelativeTo(null);
        setSize(1000, 625);
        setContentPane(getMainPane());
        setLocationByPlatform(true);
        setTitle(");
                addWindowListener(new WindowListener() {
                    public void windowClosing(WindowEvent event) {
                        Object[] options = {", "};
                        int n = JOptionPane.showOptionDialog(event.getWindow(), ", ", 0, 3, null, options, options[0]);
                        if (n == 0) {
                            event.getWindow().setVisible(false);
                            System.exit(0);
                        }
                    }

                    public void windowOpened(WindowEvent e) {
                        System.out.println("window opened");
                    }

                    public void windowClosed(WindowEvent e) {
                        System.out.println("window closed");
                    }

                    public void windowIconified(WindowEvent e) {
                        System.out.println("window iconified");
                    }

                    public void windowDeiconified(WindowEvent e) {
                        System.out.println("Window deiconified");
                    }

                    public void windowActivated(WindowEvent e) {
                        TestDataBaud.this.testDataBaudPane.setBackground(TestDataBaud.this.mainPanel.getBackground());
                        System.out.println("Window activated");
                    }

                    public void windowDeactivated(WindowEvent e) {
                        System.out.println("Window deactivated");
                    }
                });
    }

    private JPanel getMainPane() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new BoxLayout(this.mainPanel, 1));
            this.mainPanel.add(getTopPane());
            this.mainPanel.add(getHowMuchMessagesToBaudPane());
            this.mainPanel.add(getSendReceivePane());
        }
        return this.mainPanel;
    }

    private JPanel getTopPane() {
        if (this.topPane == null) {
            this.topPane = new JPanel();
            this.topPane.setLayout(new FlowLayout(1, 10, 10));
            this.topPane.add(getTestConnectionsPane());
            this.topPane.add(new JPanel(new FlowLayout(5, 5, 5)));
            this.topPane.add(getTestDataBaudPane());
            this.topPane.setPreferredSize(new Dimension(1000, 220));
        }
        return this.topPane;
    }

    private JPanel getTestConnectionsPane() {
        if (this.testConnectionsPane == null) {
            this.testConnectionsPane = new JPanel();
            BoxLayout b = new BoxLayout(this.testConnectionsPane, 1);
            this.testConnectionsPane.setLayout(b);
            this.testConnectionsPane.add(getButtonsTestConnectionPane());
            this.testConnectionsPane.add(getIndicatorsTestConnectionPane());
            this.testConnectionsPane.setPreferredSize(new Dimension(300, 150));
        }
        return this.testConnectionsPane;
    }

    private JPanel getButtonsTestConnectionPane() {
        if (this.buttonsTestConnectionPane == null) {
            this.buttonsTestConnectionPane = new JPanel();
            this.buttonsTestConnectionPane.setLayout(new FlowLayout(20, 10, 5));
            this.buttonsTestConnectionPane.add(getStartTestConnectionsButton());
            this.buttonsTestConnectionPane.add(getStopTestConnectionsButton());
            this.buttonsTestConnectionPane.setPreferredSize(new Dimension(300, 390));
        }
        return this.buttonsTestConnectionPane;
    }

    public TestDataBaud() {
        this.testConnectionsFlag1 = new AtomicBoolean(true);
        this.testConnectionsFlag2 = new AtomicBoolean(true);
        this.counter1 = 0;
        this.counter2 = 0;
        this.buttonsCounterStorageTestDataBaudPane = null;
        this.dataBaudFlag = new AtomicBoolean(true);
        this.port1UptimePane = null;
        this.port2UptimePane = null;
        initialize();
    }

    private JButton getStartTestConnectionsButton() {
        if (this.startTestConnectionsButton == null) {
            this.startTestConnectionsButton = new JButton();
            this.startTestConnectionsButton.setText(");
            this.startTestConnectionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            TestDataBaud.this.getStartTestConnectionsButton().setEnabled(false);
                            TestDataBaud.this.l = new PropertiesLoader("./configTestBSKP.properties");
                            TestDataBaud.this.taskTestConnections1 = (() -> {
                                int upCounter1 = 0;
                                while (TestDataBaud.this.counter1 <= Integer.MAX_VALUE && !Thread.currentThread().isInterrupted()) {
                                    try {
                                        if (!TestDataBaud.this.testConnectionsFlag1.get()) {
                                            TestDataBaud.this.testConnectionsPort_1_IndicatorPane.setBackground(Color.RED);
                                            System.out.println("Thread has stopped on demand");
                                            return;
                                        }
                                        InetAddress inet = InetAddress.getByName(TestDataBaud.this.l.getIp1Bind());
                                        if (!inet.isReachable(NetworkInterface.getByName(TestDataBaud.this.l.getBSKPip()), 128, 1000)) {
                                            if (upCounter1 == 90) {
                                                System.out.println("" + TestDataBaud.this.l.getBSKPip() + " );
                                                        TestDataBaud.this.setPort1UptimeLabel(new JLabel("<html> <br> <br> = " + upCounter1 + "<br> </html>"));
                                                TestDataBaud.this.testConnectionsPort_1_IndicatorPane.setBackground(Color.RED);
                                            }
                                            TestDataBaud.this.testConnectionsPort_1_IndicatorLabel.setText("<html>1 <br> <br>" + ++upCounter1 + " + " < / html > ");
                                            TestDataBaud.this.testConnectionsPort_1_IndicatorPane.setBackground(Color.RED);
                                            System.out.println("" + upCounter1 + " );
                                        }
                                        if (inet.isReachable(NetworkInterface.getByName(TestDataBaud.this.l.getBSKPip()), 128, 1000)) {
                                            System.out.println(++upCounter1 + ". ############### 1 " + upCounter1 + " #################");
                                            TestDataBaud.this.testConnectionsPort_1_IndicatorPane.setBackground(Color.GREEN);
                                            TestDataBaud.this.testConnectionsPort_1_IndicatorLabel.setText("<html>1 <br> " + upCounter1 + " );
                                        }
                                    } catch (UnknownHostException ex) {
                                        ex.printStackTrace();
                                        TestDataBaud.this.testConnectionsPort_1_IndicatorPane.setBackground(Color.RED);
                                        TestDataBaud.this.testConnectionsPort_1_IndicatorLabel.setText("<html>1 <br> <br>  );
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        TestDataBaud.this.testConnectionsPort_1_IndicatorPane.setBackground(Color.RED);
                                        TestDataBaud.this.testConnectionsPort_1_IndicatorLabel.setText("<html>1 <br> <br> );
                                    }
                                }
                            });
                            TestDataBaud.this.taskTestConnections2 = (() -> {
                                int upCounter2 = 0;
                                while (TestDataBaud.this.counter2 <= Integer.MAX_VALUE && !Thread.currentThread().isInterrupted()) {
                                    if (!TestDataBaud.this.testConnectionsFlag2.get()) {
                                        TestDataBaud.this.testConnectionsPort_2_IndicatorPane.setBackground(Color.RED);
                                        System.out.println("Thread has stopped on demand");
                                        return;
                                    }
                                    try {
                                        InetAddress inet = InetAddress.getByName(TestDataBaud.this.l.getIp2Bind());
                                        if (!inet.isReachable(NetworkInterface.getByName(TestDataBaud.this.l.getBSKPip()), 128, 1000)) {
                                            if (upCounter2 == 90) {
                                                System.out.println("" + TestDataBaud.this.l.getBSKPip() + " );
                                                        TestDataBaud.this.setPort2UptimeLabel("<html> <br> <br> = " + upCounter2 + "<br> </html>");
                                                TestDataBaud.this.testConnectionsPort_2_IndicatorPane.setBackground(Color.RED);
                                            }
                                            TestDataBaud.this.testConnectionsPort_2_IndicatorLabel.setText("<html>2 <br> <br>" + ++upCounter2 + " + " < / html > ");
                                            TestDataBaud.this.testConnectionsPort_2_IndicatorPane.setBackground(Color.RED);
                                            System.out.println("" + upCounter2 + " );
                                        }
                                        if (inet.isReachable(NetworkInterface.getByName(TestDataBaud.this.l.getBSKPip()), 128, 1000)) {
                                            System.out.println(++upCounter2 + ". ############### 2 " + upCounter2 + " #################");
                                            TestDataBaud.this.testConnectionsPort_2_IndicatorPane.setBackground(Color.GREEN);
                                            TestDataBaud.this.testConnectionsPort_2_IndicatorLabel.setText("<html>2 <br> " + upCounter2 + " );
                                        }
                                    } catch (UnknownHostException ex) {
                                        ex.printStackTrace();
                                        TestDataBaud.this.testConnectionsPort_2_IndicatorPane.setBackground(Color.RED);
                                        TestDataBaud.this.testConnectionsPort_2_IndicatorLabel.setText("<html>2 <br> <br>  );
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        TestDataBaud.this.testConnectionsPort_2_IndicatorPane.setBackground(Color.RED);
                                        TestDataBaud.this.testConnectionsPort_2_IndicatorLabel.setText("<html>2 <br> <br> );
                                    }
                                }
                            });
                            (new Thread(TestDataBaud.this.taskTestConnections1)).start();
                            (new Thread(TestDataBaud.this.taskTestConnections2)).start();
                            System.out.println("Thread 1 has started");
                            System.out.println("Thread 2 has started");
                        }
                    });
                }
            });
        }
        return this.startTestConnectionsButton;
    }

    public void windowClosing(WindowEvent event) {
        Object[] options = {", "};
        int n = JOptionPane.showOptionDialog(event.getWindow(), ", ", 0, 3, null, options, options[0]);
        if (n == 0) {
            event.getWindow().setVisible(false);
            System.exit(0);
        }
    }

    private void disablerGUIelements() {
        getStartTestDataBaudButton().setEnabled(false);
        getStopTestDataBaudButton().setEnabled(false);
        getStopTestConnectionsButton().setEnabled(true);
        getStopTestDataBaudButton().setEnabled(false);
        getSpecifyMCastGroupTextArea().setEnabled(false);
        getPortNumberTextArea().setEnabled(false);
        getSpecifyBSKP_IpAddressTextArea().setEnabled(false);
        getHowMuchMessageToBaudCombobox().setEnabled(false);
    }

    private JButton getStopTestConnectionsButton() {
        if (this.stopTestConnectionsButton == null) {
            this.stopTestConnectionsButton = new JButton();
            this.stopTestConnectionsButton.setText("");
            this.stopTestConnectionsButton.setEnabled(true);
            this.stopTestConnectionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (TestDataBaud.this.stopTestConnectionsButton != null) {
                                TestDataBaud.this.testConnectionsFlag1.set(false);
                                TestDataBaud.this.testConnectionsFlag2.set(false);
                                System.out.println("Program must rerun by running script");
                                System.exit(-555);
                            }
                        }
                    });
                }
            });
        }
        return this.stopTestConnectionsButton;
    }

    private void enablerGUIelements() {
        getStartTestConnectionsButton().setEnabled(true);
        getStopTestConnectionsButton().setEnabled(false);
        getStartTestDataBaudButton().setEnabled(true);
        getStopTestDataBaudButton().setEnabled(true);
        getSpecifyMCastGroupTextArea().setEnabled(true);
        getPortNumberTextArea().setEnabled(true);
        getSpecifyBSKP_IpAddressTextArea().setEnabled(true);
        getHowMuchMessageToBaudCombobox().setEnabled(true);
    }

    private JPanel getIndicatorsTestConnectionPane() {
        if (this.indicatorsTestConnectionPane == null) {
            this.indicatorsTestConnectionPane = new JPanel();
            this.indicatorsTestConnectionPane.setLayout(new GridLayout(1, 2, 5, 5));
            this.indicatorsTestConnectionPane.add(getTestConnectionsPort_1_IndicatorPane());
            this.indicatorsTestConnectionPane.add(getTestConnectionsPort_2_IndicatorPane());
        }
        return this.indicatorsTestConnectionPane;
    }

    private JPanel getTestConnectionsPort_1_IndicatorPane() {
        if (this.testConnectionsPort_1_IndicatorPane == null) {
            this.testConnectionsPort_1_IndicatorPane = new JPanel(new FlowLayout(0, 20, 25));
            this.testConnectionsPort_1_IndicatorPane.add(getTestConnectionsPort_1_IndicatorLabel());
            this.testConnectionsPort_1_IndicatorPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
            this.testConnectionsPort_1_IndicatorPane.setPreferredSize(new Dimension(120, 120));
        }
        return this.testConnectionsPort_1_IndicatorPane;
    }

    private JLabel getTestConnectionsPort_1_IndicatorLabel() {
        if (this.testConnectionsPort_1_IndicatorLabel == null) {
            this.testConnectionsPort_1_IndicatorLabel = new JLabel("<html> <br> 1 );
            this.testConnectionsPort_1_IndicatorLabel.setPreferredSize(new Dimension(120, 75));
        }
        return this.testConnectionsPort_1_IndicatorLabel;
    }

    private JPanel getTestConnectionsPort_2_IndicatorPane() {
        if (this.testConnectionsPort_2_IndicatorPane == null) {
            this.testConnectionsPort_2_IndicatorPane = new JPanel(new FlowLayout(0, 20, 25));
            this.testConnectionsPort_2_IndicatorPane.add(getTestConnectionsPort_2_IndicatorLabel());
            this.testConnectionsPort_2_IndicatorPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
            this.testConnectionsPort_2_IndicatorPane.setPreferredSize(new Dimension(120, 90));
        }
        return this.testConnectionsPort_2_IndicatorPane;
    }

    private JLabel getTestConnectionsPort_2_IndicatorLabel() {
        if (this.testConnectionsPort_2_IndicatorLabel == null) {
            this.testConnectionsPort_2_IndicatorLabel = new JLabel("<html> <br> 2 );
            this.testConnectionsPort_2_IndicatorLabel.setPreferredSize(new Dimension(120, 75));
        }
        return this.testConnectionsPort_2_IndicatorLabel;
    }

    private JPanel getTestDataBaudPane() {
        if (this.testDataBaudPane == null) {
            this.testDataBaudPane = new JPanel();
            this.testDataBaudPane.setLayout(new BoxLayout(this.testDataBaudPane, 1));
            this.testDataBaudPane.add(getButtonsTestDataBaudPane());
            this.testDataBaudPane.add(getButtonsIndicatorTestDataBaudPane());
            this.testDataBaudPane.add(getButtonsCounterStorageTestDataBaudPane());
            this.testDataBaudPane.add(new JPanel());
            this.testDataBaudPane.setPreferredSize(new Dimension(300, 150));
        }
        return this.testDataBaudPane;
    }

    private JPanel getButtonsTestDataBaudPane() {
        if (this.buttonsTestDataBaudPane == null) {
            this.buttonsTestDataBaudPane = new JPanel();
            this.buttonsTestDataBaudPane.setLayout(new FlowLayout());
            this.buttonsTestDataBaudPane.add(getStartTestDataBaudButton());
            this.buttonsTestDataBaudPane.add(getStopTestDataBaudButton());
        }
        return this.buttonsTestDataBaudPane;
    }

    private JPanel getButtonsCounterStorageTestDataBaudPane() {
        if (this.buttonsCounterStorageTestDataBaudPane == null) {
            this.buttonsCounterStorageTestDataBaudPane = new JPanel();
            this.buttonsCounterStorageTestDataBaudPane.setLayout(new FlowLayout());
        }
        return this.buttonsTestDataBaudPane;
    }

    private void paintPanel(boolean isTestDataBaudThreadRunning) {
        if (isTestDataBaudThreadRunning == true) {
            getButtonsIndicatorTestDataBaudPane().setBackground(Color.GREEN);
            this.buttonsTestDataBaudPaneLabel.setText(");
        } else {
            getButtonsIndicatorTestDataBaudPane().setBackground(Color.RED);
            this.buttonsTestDataBaudPaneLabel.setText(");
        }
    }

    private void startDataBaud() {
        this.task = (() -> {
            this.l = new PropertiesLoader("./configTestBSKP.properties");
            MulticastSocket multicastSocket1 = null;
            MulticastSocket multicastSocket2 = null;
            String howMuchMessageToBaud = (String) getHowMuchMessageToBaudCombobox().getSelectedItem();
            if ( howMuchMessageToBaud.equals("))
                    howMuchMessageToBaud = new String("2147483647");
            byte[] arrbytes = this.l.getMessageToBaud().getBytes();
            DatagramPacket pack = null;
            int counter = 0;
            while (counter <= Integer.parseInt(howMuchMessageToBaud) - 1 && !Thread.currentThread().isInterrupted()) {
                try {
                    if (!this.dataBaudFlag.get()) {
                        paintPanel(false);
                        enableGUIElements();
                        System.out.println("Thread has stopped on demand");
                        return;
                    }
                    pack = new DatagramPacket(arrbytes, arrbytes.length, InetAddress.getByName(getSpecifyMCastGroupTextArea().getText()), Integer.parseInt(getPortNumberTextArea().getText()));
                    multicastSocket1 = new MulticastSocket(Integer.parseInt(getPortNumberTextArea().getText()));
                    multicastSocket2 = new MulticastSocket(Integer.parseInt(getPortNumberTextArea().getText()));
                    multicastSocket1.setInterface(InetAddress.getByName(this.l.getIp1Bind()));
                    multicastSocket2.setInterface(InetAddress.getByName(this.l.getIp2Bind()));
                    multicastSocket2.joinGroup(InetAddress.getByName(getSpecifyMCastGroupTextArea().getText()));
                    multicastSocket1.setSoTimeout(this.l.getTimeOut());
                    multicastSocket2.setSoTimeout(this.l.getTimeOut());
                    counter++;
                    multicastSocket1.send(pack);
                    TimeUnit.SECONDS.sleep(1L);
                    System.out.println(counter + ". + counter + ": \"" + new String(arrbytes) + "\"");
                    getSendTextArea().append(String.valueOf(counter) + ". " + " " + "+ String.valueOf(counter) + ": \
                    "" + new String(arrbytes) + "\" \n");
                    byte[] buf = new byte[1500];
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    multicastSocket2.receive(datagramPacket);
                    System.out.print(counter + ". + counter + "" + "\"");
                    getReceiveTextArea().append(String.valueOf(counter) + ". " + " " + ": + counter + ": \
                    "" + new String(arrbytes) + "\"\n");
                    byte[] arrBytes = new byte[datagramPacket.getLength()];
                    System.arraycopy(buf, 0, arrBytes, 0, arrBytes.length);
                    System.out.println(new String(arrBytes) + "\"");
                    paintPanel(true);
                    if (counter == Integer.parseInt(howMuchMessageToBaud)) {
                        UIManager UI = new UIManager();
                        UIManager.put("OptionPane.background", Color.GREEN);
                        UIManager.put("Panel.background", Color.GREEN);
                        JOptionPane.showMessageDialog(null, "\n\n\n\n", ", 1);
                                UI = null;
                    }
                    counter = 0;
                } catch (Exception e) {
                    getSendTextArea().append(String.valueOf(++counter) + ". " + " " + ": " + String.valueOf(counter) + ": \"" + new String(arrbytes) + "\" \n");
                    e.printStackTrace();
                    paintPanel(false);
                    this.buttonsTestDataBaudPaneLabel.setText("<html>" + counter + " </html>");
                    this.receiveTextArea.append(counter + ". " + e.toString() + "\n");
                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException ex) {
                    }
                }
            }
            paintPanel(false);
            enableGUIElements();
            System.out.println("Thread has stopped on counter");
        });
        this.dataBaudFlag.set(true);
        (new Thread(this.task)).start();
        System.out.println("Thread has started");
    }

    private JButton getStartTestDataBaudButton() {
        if (this.startTestDataBaudButton == null) {
            this.startTestDataBaudButton = new JButton();
            this.startTestDataBaudButton.setText(");
            this.startTestDataBaudButton.addActionListener(arg0 -> {
                getStopTestConnectionsButton().setEnabled(false);
                getStartTestDataBaudButton().setEnabled(false);
                getStopTestDataBaudButton().setEnabled(true);
                getSpecifyMCastGroupTextArea().setEnabled(false);
                getPortNumberTextArea().setEnabled(false);
                getSpecifyBSKP_IpAddressTextArea().setEnabled(false);
                getHowMuchMessageToBaudCombobox().setEnabled(false);
                startDataBaud();
            });
        }
        return this.startTestDataBaudButton;
    }

    private JButton getStopTestDataBaudButton() {
        if (this.stopTestDataBaudButton == null) {
            this.stopTestDataBaudButton = new JButton();
            this.stopTestDataBaudButton.setText(");
            this.stopTestDataBaudButton.setEnabled(false);
            this.stopTestDataBaudButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (TestDataBaud.this.stopTestConnectionsButton != null)
                        TestDataBaud.this.dataBaudFlag.set(false);
                }
            });
        }
        return this.stopTestDataBaudButton;
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
        if (this.buttonsIndicatorTestDataBaudPane == null) {
            this.buttonsIndicatorTestDataBaudPane = new JPanel();
            this.buttonsIndicatorTestDataBaudPane.setLayout(new FlowLayout(40, 60, 35));
            this.buttonsIndicatorTestDataBaudPane.add(getButtonsTestDataBaudPaneLabel());
            this.buttonsIndicatorTestDataBaudPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
            this.buttonsIndicatorTestDataBaudPane.setPreferredSize(new Dimension(90, 90));
        }
        return this.buttonsIndicatorTestDataBaudPane;
    }

    private JLabel getButtonsTestDataBaudPaneLabel() {
        if (this.buttonsTestDataBaudPaneLabel == null)
            this.buttonsTestDataBaudPaneLabel = new JLabel("<html><br>); 
        return this.buttonsTestDataBaudPaneLabel;
    }

    private JPanel getParamsPane() {
        if (this.paramsPane == null) {
            this.paramsPane = new JPanel();
            JPanel specifyMCastGroupPane = new JPanel(new FlowLayout());
            specifyMCastGroupPane.add(getSpecifyMCastGroupLabel());
            specifyMCastGroupPane.add(getSpecifyMCastGroupTextArea());
            JPanel portNumberPane = new JPanel(new FlowLayout());
            portNumberPane.add(getPortNumberLabel());
            portNumberPane.add(getPortNumberTextArea());
            JPanel specifyBSKP_IpAddressPane = new JPanel(new FlowLayout());
            specifyBSKP_IpAddressPane.add(getSpecifyBSKP_IpAddressLabel());
            specifyBSKP_IpAddressPane.add(getSpecifyBSKP_IpAddressTextArea());
            this.paramsPane.add(specifyMCastGroupPane);
            this.paramsPane.add(portNumberPane);
            this.paramsPane.add(specifyBSKP_IpAddressPane);
            this.paramsPane.setPreferredSize(new Dimension(350, 100));
        }
        return this.paramsPane;
    }

    private JLabel getSpecifyMCastGroupLabel() {
        if (this.specifyMCastGroupLabel == null)
            this.specifyMCastGroupLabel = new JLabel("IP-); 
        return this.specifyMCastGroupLabel;
    }

    private JTextArea getSpecifyMCastGroupTextArea() {
        if (this.specifyMCastGroupTextArea == null) {
            PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties");
            this.specifyMCastGroupTextArea = new JTextArea(l.getGroup2());
            this.specifyMCastGroupTextArea.setPreferredSize(new Dimension(80, 20));
            this.specifyMCastGroupTextArea.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        }
        return this.specifyMCastGroupTextArea;
    }

    private JLabel getPortNumberLabel() {
        if (this.portNumberLabel == null)
            this.portNumberLabel = new JLabel("IP ); 
        return this.portNumberLabel;
    }

    private JTextArea getPortNumberTextArea() {
        if (this.portNumberTextArea == null) {
            PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties");
            String port = null;
            this.portNumberTextArea = new JTextArea(String.valueOf(l.getPort()));
            this.portNumberTextArea.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        }
        this.portNumberTextArea.setPreferredSize(new Dimension(80, 20));
        return this.portNumberTextArea;
    }

    private JLabel getSpecifyBSKP_IpAddressLabel() {
        if (this.specifyBSKP_IpAddressLabel == null)
            this.specifyBSKP_IpAddressLabel = new JLabel("IP-); 
        return this.specifyBSKP_IpAddressLabel;
    }

    private JTextArea getSpecifyBSKP_IpAddressTextArea() {
        PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties");
        String bskpIP = l.getBSKPip();
        if (this.specifyBSKP_IpAddressTextArea == null) {
            this.specifyBSKP_IpAddressTextArea = new JTextArea(bskpIP);
            this.specifyBSKP_IpAddressTextArea.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        }
        this.specifyBSKP_IpAddressTextArea.setPreferredSize(new Dimension(100, 20));
        return this.specifyBSKP_IpAddressTextArea;
    }

    private JPanel getSendScrollPane() {
        if (this.sendScrollPane == null) {
            this.sendScrollPane = new JPanel();
            BoxLayout b = new BoxLayout(this.sendScrollPane, 1);
            this.sendScrollPane.setLayout(b);
            this.sendScrollPane.add(new JLabel("));
            this.sendScrollPane.add(getSendTextArea());
            this.sendScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        }
        return this.sendScrollPane;
    }

    class NotEditableOutputArea extends JTextArea {
        private final Font defaultFont = new Font("monospaced", 0, 12);

        public NotEditableOutputArea() {
            setEditable(false);
            DefaultCaret caret = (DefaultCaret) getCaret();
            caret.setUpdatePolicy(2);
            setFont(this.defaultFont);
            setCaretPosition(getDocument().getLength());
            setLineWrap(true);
        }
    }

    private TextArea getSendTextArea() {
        if (this.sendTextArea == null) {
            this.sendTextArea = new TextArea();
            this.sendTextArea.setPreferredSize(new Dimension(470, 290));
        }
        return this.sendTextArea;
    }

    private JPanel getReceiveScrollPane() {
        if (this.receiveScrollPane == null) {
            this.receiveScrollPane = new JPanel();
            BoxLayout b = new BoxLayout(this.receiveScrollPane, 1);
            this.receiveScrollPane.setLayout(b);
            this.receiveScrollPane.add(new JLabel("));
            this.receiveScrollPane.add(getReceiveTextArea());
            this.receiveScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        }
        return this.receiveScrollPane;
    }

    private TextArea getReceiveTextArea() {
        if (this.receiveTextArea == null) {
            this.receiveTextArea = new TextArea();
            this.receiveTextArea.setPreferredSize(new Dimension(470, 290));
            this.receiveTextArea.setEditable(false);
        }
        return this.receiveTextArea;
    }

    private JPanel getHowMuchMessagesToBaudPane() {
        if (this.howMuchMessagesToBaudPane == null) {
            this.howMuchMessagesToBaudPane = new JPanel();
            this.howMuchMessagesToBaudPane.setLayout(new FlowLayout(1, 10, 10));
            this.howMuchMessagesToBaudPane.add(getHowMuchMessagesToBaudLabel());
            this.howMuchMessagesToBaudPane.add(getHowMuchMessageToBaudCombobox());
        }
        return this.howMuchMessagesToBaudPane;
    }

    private JComboBox getHowMuchMessageToBaudCombobox() {
        if (this.howMuchMessagesToBaudCombobox == null) {
            this.howMuchMessagesToBaudCombobox = new JComboBox<>(new String[]{"1", "5", "10", "20", "30", "50", "100", "1000", "});
                    this.howMuchMessagesToBaudCombobox.setSelectedItem(this.howMuchMessagesToBaudCombobox.getItemAt(4));
    }
            this.howMuchMessagesToBaudCombobox.setPreferredSize(new Dimension(60, 20));
            return this.howMuchMessagesToBaudCombobox;
        }

        private JLabel getHowMuchMessagesToBaudLabel () {
            if (this.howMuchMessagesToBaudLabel == null)
                this.howMuchMessagesToBaudLabel = new JLabel("); 
            return this.howMuchMessagesToBaudLabel;
        }

        private JPanel getSendReceivePane () {
            if (this.sendReceivePane == null) {
                this.sendReceivePane = new JPanel();
                this.sendReceivePane.setLayout(new FlowLayout(1, 15, 15));
                this.sendReceivePane.add(getSendScrollPane());
                this.sendReceivePane.add(getReceiveScrollPane());
                this.sendReceivePane.setPreferredSize(new Dimension(1000, 400));
            }
            return this.sendReceivePane;
        }

        public static void main (String[]args){
            SwingUtilities.invokeLater(() -> {
                TestDataBaud thisClass = new TestDataBaud();
                thisClass.setDefaultCloseOperation(2);
                thisClass.setResizable(false);
                thisClass.setVisible(true);
            });
        }

        public void setPort1UptimeLabel (JLabel port1UptimeLabel){
            this.port1UptimeLabel = port1UptimeLabel;
        }

        private JLabel getPort1UptimeLabel () {
            return this.port1UptimeLabel;
        }

        private JPanel getPort1UptimePane () {
            if (this.port1UptimePane == null)
                this.port1UptimePane = new JPanel(new FlowLayout());
            return this.port1UptimePane;
        }

        public JLabel getPort2UptimeLabel () {
            return this.port2UptimeLabel;
        }

        public JLabel setPort1UptimeLabel (String text){
            if (this.port1UptimeLabel == null)
                this.port1UptimeLabel = new JLabel(text);
            this.port2UptimeLabel = new JLabel(text);
            return this.port1UptimeLabel;
        }

        public JLabel setPort2UptimeLabel (String text){
            if (this.port2UptimeLabel == null)
                this.port2UptimeLabel = new JLabel(text);
            this.port2UptimeLabel = new JLabel(text);
            return this.port2UptimeLabel;
        }

        private JPanel getPort2UptimePane () {
            if (this.port2UptimePane == null)
                this.port2UptimePane = new JPanel(new FlowLayout());
            return this.port2UptimePane;
        }

        private void setPort1UptimePane (JLabel jLabel){
            throw new UnsupportedOperationException("Feature is under construction!");
        }

        private void setPort2UptimePane (JLabel jLabel){
            throw new UnsupportedOperationException("Feature is under construction!");
        }
    }
