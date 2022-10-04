import components.ControlButton;
import components.NotEditableOutputArea;
import components.TopTitledBorder;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by user on 19.10.16.
 */
public class PortTest extends JFrame{

    private final ScheduledThreadPoolExecutor channelTestsExecutor = new ScheduledThreadPoolExecutor(2);
    private final ExecutorService testExecutor = Executors.newSingleThreadExecutor();

    private final JLabel receiverUptime = new JLabel(){{
        setBorder(new TopTitledBorder("Кабель 2"));
        setOpaque(true);
        setPreferredSize(new Dimension(300,50));
        setHorizontalAlignment(CENTER);
    }};
    private final JLabel senderUptime = new JLabel(){{
        setBorder(new TopTitledBorder("Кабель 1"));
        setOpaque(true);
        setPreferredSize(new Dimension(300,50));
        setHorizontalAlignment(CENTER);
    }};
    private final JLabel forwarding = new JLabel(){{
        setBorder(new TopTitledBorder("Передача с порта на порт"));
        setOpaque(true);
        setPreferredSize(new Dimension(300,50));
        setHorizontalAlignment(CENTER);
    }};

    private Future testFuture;
    private Future ioFuture;

    private final ControlButton start = new ControlButton("Старт",true);
    private final ControlButton stop = new ControlButton("Стоп",false);
    private final JTextField iterationsField = new JTextField("10"){{
        setBorder(new TopTitledBorder("Количество проходов"));
    }};

    private final JProgressBar progressBar = new JProgressBar(){{
        setPreferredSize(new Dimension(getPreferredSize().width, 50));
        setStringPainted(true);
    }};

    private final NotEditableOutputArea senderOutput = new NotEditableOutputArea(){{
        setBorder(new TopTitledBorder("Отправка"));
    }};
    private final NotEditableOutputArea receiverOutput = new NotEditableOutputArea(){{
        setBorder(new TopTitledBorder("Прием"));
    }};

    private PortTest() throws IOException {
        JPanel controlPanel = new JPanel(new BorderLayout()) {{
            add(new JPanel(new GridLayout(1,2)){{
                add(start);
                add(stop);
            }}, BorderLayout.WEST);
            add(new JPanel(new BorderLayout()){{
               /* add(new JPanel(new BorderLayout()){{
                    setPreferredSize(new Dimension(150,getPreferredSize().height));
                }},BorderLayout.WEST);*/
                add(iterationsField,BorderLayout.CENTER);
            }}, BorderLayout.CENTER);
            add(new JPanel(new GridLayout(1,3)){{
                add(senderUptime);
                add(receiverUptime);
                add(forwarding);
            }},BorderLayout.EAST);
        }};

        JPanel displayPanel = new JPanel(new GridLayout(1, 2)) {{
            add(new JScrollPane(senderOutput));
            add(new JScrollPane(receiverOutput));
        }};

        JPanel contentPanel = new JPanel(new BorderLayout()){{
            add(controlPanel,BorderLayout.NORTH);
            add(displayPanel,BorderLayout.CENTER);
            add(progressBar,BorderLayout.SOUTH);
        }};

        setContentPane(contentPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Тест БСКП");
        setSize(new Dimension(1600,900));
        setResizable(false);

        start.addActionListener(actionEvent -> startTest());
        stop.addActionListener(actionEvent -> stopTest());


        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                MulticastSocket sender = null;
                MulticastSocket receiver = null;
                try {
                    Properties properties = Util.readProperties("config.properties");
                    final InetAddress group = InetAddress.getByName(Util.extractProperty(properties, "group"));
                    final int port = Integer.parseInt(Util.extractProperty(properties, "port")) + 1;
                    final InetAddress senderAddress = InetAddress.getByName(Util.extractProperty(properties, "host.ip.main"));
                    final InetAddress receiverAddress = InetAddress.getByName(Util.extractProperty(properties, "host.ip.standby"));

                    sender = new MulticastSocket(null);
                    receiver= new MulticastSocket(port);

                    sender.bind(new InetSocketAddress(senderAddress,port));
                    receiver.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(receiverAddress));
                    receiver.setSoTimeout(300);

                    byte [] data = new byte[10];
                    DatagramPacket out = new DatagramPacket(data,data.length,group,port);
                    sender.send(out);
                    byte [] temp = new byte[1400];
                    DatagramPacket in = new DatagramPacket(temp,temp.length);
                    receiver.receive(in);
                    forwarding.setBackground(Color.green);
                    forwarding.setText("Выполняется");
                } catch (Exception e) {
                    forwarding.setBackground(Color.red);
                    forwarding.setText("Не выполняется");
                    e.printStackTrace();
                }
                if(sender!=null) sender.close();
                if(receiver!=null) receiver.close();
            }
        },0,1,TimeUnit.SECONDS);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            AtomicLong senderUptimeCounter = new AtomicLong(0);
            AtomicLong receiverUptimeCounter = new AtomicLong(0);
            @Override
            public void run() {
                try {
                    Properties properties = Util.readProperties("config.properties");
                    NetworkInterface sender = NetworkInterface.getByInetAddress(InetAddress.getByName(Util.extractProperty(properties, "host.ip.main")));
                    NetworkInterface receiver = NetworkInterface.getByInetAddress(InetAddress.getByName(Util.extractProperty(properties, "host.ip.standby")));

                    if(sender.isUp()){
                        senderUptimeCounter.incrementAndGet();
                        senderUptime.setBackground(Color.green);
                        senderUptime.setText("Подключен " + senderUptimeCounter.get() + " секунд");
                    }else {
                        senderUptimeCounter.set(0);
                        senderUptime.setBackground(Color.red);
                        senderUptime.setText("Отключен");
                    }

                    if(receiver.isUp()){
                        receiverUptimeCounter.incrementAndGet();
                        receiverUptime.setBackground(Color.green);
                        receiverUptime.setText("Подключен " + receiverUptimeCounter.get() + " секунд");
                    }else {
                        receiverUptimeCounter.set(0);
                        receiverUptime.setBackground(Color.red);
                        receiverUptime.setText("Отключен");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,1,TimeUnit.SECONDS);
    }

    private void stopTest() {
        ioFuture.cancel(true);
        if(testFuture != null) testFuture.cancel(true);
        start.setEnabled(true);
        stop.setEnabled(false);
    }



    private void startTest() {
        start.setEnabled(false);
        stop.setEnabled(true);

        if(testFuture!= null) testFuture.cancel(true);

        final AtomicInteger sendErrors = new AtomicInteger(0);
        final AtomicInteger receiveErrors = new AtomicInteger(0);

        testFuture = testExecutor.submit(() -> {
            try {
                byte [] data = Files.readAllBytes(Paths.get("messages.txt"));
                Properties properties = Util.readProperties("config.properties");

                final InetAddress group = InetAddress.getByName(Util.extractProperty(properties, "group"));
                final int port = Integer.parseInt(Util.extractProperty(properties, "port"));

                final InetAddress senderAddress = InetAddress.getByName(Util.extractProperty(properties, "host.ip.main"));
                final InetAddress receiverAddress = InetAddress.getByName(Util.extractProperty(properties, "host.ip.standby"));

                final Integer iterations = Integer.parseInt(iterationsField.getText());

                ioFuture = channelTestsExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        senderOutput.setText("");
                        receiverOutput.setText("");
                        progressBar.setMaximum(iterations);

                        MulticastSocket sender = null;
                        MulticastSocket receiver = null;

                        try {
                            sender = new MulticastSocket(null);
                            receiver = new MulticastSocket(port);

                            sender.bind(new InetSocketAddress(senderAddress,port));
                            receiver.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(receiverAddress));
                            receiver.setSoTimeout(500);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        //try{
                            for (int iteration = 0; iteration < iterations; ++iteration) {
                                StringBuilder senderString = new StringBuilder("\n# Сообщение № : " + iteration + "\n");
                                StringBuilder receiverString = new StringBuilder("\n#  Сообщение № : " + iteration + "\n");

                                DatagramPacket out = new DatagramPacket(data,data.length,group,port);
                                try {
                                    senderString.append("Отправка : ").append(Arrays.toString(data)).append("\n");
                                    sender.send(out);
                                    senderString.append("# Ошибок нет\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    senderString.append("# Ошибка сети : ").append(e.getClass().getName()).append(" - ").append(e.getMessage()).append("\n");
                                    sendErrors.incrementAndGet();
                                }

                                byte[] buffer = new byte[1400];
                                DatagramPacket in = new DatagramPacket(buffer, buffer.length);
                                try {
                                    receiver.receive(in);
                                    byte[] received = new byte[in.getLength()];
                                    System.arraycopy(in.getData(), 0, received, 0, received.length);
                                    receiverString.append("Прием :\t").append(Arrays.toString(received)).append("\n");

                                    if (!in.getAddress().equals(senderAddress)) {
                                        receiverString.append("# Ошибка : IP отправителя не верен\n");
                                        receiveErrors.incrementAndGet();
                                    } else if (!Arrays.equals(data, received)) {
                                        receiverString.append("# Ошибка : данные не идентичны\n");
                                        receiveErrors.incrementAndGet();
                                    } else {
                                        receiverString.append("# Ошибок нет\n");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    receiverString.append("# Ошибка сети : ").append(e.getClass().getName()).append(" - ").append(e.getMessage()).append("\n");
                                    receiveErrors.incrementAndGet();
                                }

                                senderOutput.addColorText(senderString.toString(),Color.black);
                                receiverOutput.addColorText(receiverString.toString(),Color.black);
                                progressBar.setValue(iteration + 1);

                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    senderOutput.addColorText("\n---------\n\nТЕСТ ПРЕРВАН",Color.red);
                                    receiverOutput.addColorText("\n---------\n\nТЕСТ ПРЕРВАН",Color.red);
                                    if (sender != null) sender.close();
                                    if( receiver != null) receiver.close();
                                    return;
                                }


                            }
                        /*} catch (Exception e) {
                            e.printStackTrace();
                            //output.append("# Неизвестная ошибка : ").append(e.getClass().getName()).append(" - ").append(e.getMessage()).append("\n");
                            //sendErrors.incrementAndGet();
                        }*/

                        if (sendErrors.get() == 0) {
                            senderOutput.addColorText("\n---------\n\nТЕСТ ПРОЙДЕН",Color.green);
                        } else {
                            senderOutput.addColorText("\n---------\n\nТЕСТ НЕ ПРОЙДЕН",Color.green);
                        }

                        if (receiveErrors.get() == 0) {
                            receiverOutput.addColorText("\n---------\n\nТЕСТ ПРОЙДЕН",Color.red);
                        } else {
                            receiverOutput.addColorText("\n---------\n\nТЕСТ НЕ ПРОЙДЕН",Color.red);
                        }
                    }


                });

                ioFuture.get();

                start.setEnabled(true);
                stop.setEnabled(false);

                if((sendErrors.get() == 0) && (receiveErrors.get() == 0)){
                    JOptionPane.showMessageDialog(
                            PortTest.this,
                            "Тест завершен, тест пройден\n\n" +
                            "Ошибок отправки : " + sendErrors.get() + "\n" +
                            "Ошибок приема : " + receiveErrors.get() + "\n",
                            "Результат", JOptionPane.INFORMATION_MESSAGE);
                }else {
                    JOptionPane.showMessageDialog(
                            PortTest.this,
                            "Тест завершен, тест не пройден\n\n" +
                                    "Ошибок отправки : " + sendErrors.get() + "\n" +
                                    "Ошибок приема : " + receiveErrors.get() + "\n",
                            "Результат", JOptionPane.WARNING_MESSAGE);
                }
            } catch (ExecutionException ex) {
                ex.printStackTrace();
                Throwable throwable = ex.getCause();

                start.setEnabled(true);
                stop.setEnabled(false);

                JOptionPane.showMessageDialog(
                        PortTest.this,
                        "Тест прерван из зв внутренней ощибки, тест не пройден\n\n"+
                                throwable.getClass().getName() + " :\n" + throwable.getMessage(),
                        "Результат",
                        JOptionPane.ERROR_MESSAGE);
            } catch (CancellationException | InterruptedException cie){
                cie.printStackTrace();

                start.setEnabled(true);
                stop.setEnabled(false);

                JOptionPane.showMessageDialog(
                        PortTest.this,
                        "Тест прерван оператором, тест не пройден\n\n" +
                                "Ошибок отправки : " + sendErrors.get() + "\n" +
                                "Ошибок приема : " + receiveErrors.get() + "\n",
                        "Результат", JOptionPane.WARNING_MESSAGE);
            } catch (Throwable throwable) {
                throwable.printStackTrace();

                start.setEnabled(true);
                stop.setEnabled(false);

                JOptionPane.showMessageDialog(
                        PortTest.this,
                        "Тест прерван из за непредвиденной, тест не пройден\n\n"+
                                throwable.getClass().getName() + " :\n" + throwable.getMessage(),
                        "Результат",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }



    public static void main(String[] args) throws IOException {
        try {
            //UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PortTest testBSEIKS = null;
                try {
                    testBSEIKS = new PortTest();
                    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                    testBSEIKS.setLocation(dim.width/2-testBSEIKS.getSize().width/2, dim.height/2-testBSEIKS.getSize().height/2);
                    testBSEIKS.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
