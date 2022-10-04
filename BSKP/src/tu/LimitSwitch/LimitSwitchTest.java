package tu.LimitSwitch;

import components.ControlButton;
import components.NotEditableOutputArea;
import components.TopTitledBorder;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by user on 26.10.16.
 */
public class LimitSwitchTest extends JFrame{
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    private final JLabel kssLabel = new JLabel() {{
        setOpaque(true);
        setPreferredSize(new Dimension(300, 50));
        setHorizontalAlignment(CENTER);
        setBorder(new TopTitledBorder("Состояние"));
    }};
    private final JLabel alarmLabel = new JLabel() {{
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setPreferredSize(new Dimension(300, 50));
        setBorder(new TopTitledBorder("Крышка"));
    }};
    private Future kssFuture;
    private Future alarmFuture;

    private final ControlButton start = new ControlButton("Старт",true){{
        setPreferredSize(new Dimension(getPreferredSize().width, 50));
    }};
    private final ControlButton stop = new ControlButton("Стоп",false){{
        setPreferredSize(new Dimension(getPreferredSize().width, 50));
    }};

    private final NotEditableOutputArea kssOutput = new NotEditableOutputArea();
    private final NotEditableOutputArea alarmOutput = new NotEditableOutputArea();
    private final JComboBox<LimitSwitch> switchSelector = new JComboBox<LimitSwitch>(){{
        for(LimitSwitch limitSwitch : LimitSwitch.values()){
            addItem(limitSwitch);
        }
    }};

    private void startTest() {
        LimitSwitch limitSwitch = (LimitSwitch) switchSelector.getSelectedItem();
        try {
            InetAddress address = InetAddress.getByName(Util.extractProperty(Util.readProperties("config.properties"), "host.ip.main"));
            kssFuture = executor.submit(new KSSHandler(limitSwitch, address,kssOutput, kssLabel));
            alarmFuture = executor.submit(new AlarmHandler(limitSwitch, address,alarmOutput, alarmLabel));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(LimitSwitchTest.this,e.getLocalizedMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }

        start.setEnabled(false);
        stop.setEnabled(true);
    }

    private void stopTest() {
        start.setEnabled(true);
        stop.setEnabled(false);
        if(kssFuture != null) kssFuture.cancel(true);
        if(alarmFuture != null) alarmFuture.cancel(true);
    }

    private LimitSwitchTest(){
        JPanel contentPanel = new JPanel(new GridLayout(1,2)){{
            add(new JPanel(new BorderLayout()){{
                setBorder(new TopTitledBorder("Прием КСС"));
                add(new JScrollPane(kssOutput),BorderLayout.CENTER);
                add(new JButton("Очистка"){{
                    setPreferredSize(new Dimension(getPreferredSize().width, 50));
                    addActionListener(e -> {
                        kssOutput.setText(null);
                        kssLabel.setBackground(null);
                    });
                }},BorderLayout.SOUTH);
                add(new JPanel(new BorderLayout()){{
                    add(kssLabel,BorderLayout.CENTER);
                }},BorderLayout.NORTH);
            }});
            add(new JPanel(new BorderLayout()){{
                setBorder(new TopTitledBorder("Прием сигнала тревоги"));
                add(new JScrollPane(alarmOutput),BorderLayout.CENTER);
                add(new JButton("Очистка"){{
                    setPreferredSize(new Dimension(getPreferredSize().width, 50));
                    addActionListener(e -> {
                        alarmOutput.setText(null);
                        alarmLabel.setBackground(null);
                    });
                }},BorderLayout.SOUTH);
                add(new JPanel(new BorderLayout()){{
                    add(alarmLabel,BorderLayout.CENTER);
                }},BorderLayout.NORTH);
            }});
        }};

        JPanel controlPanel = new JPanel(new BorderLayout()){{
            add(switchSelector,BorderLayout.CENTER);
            add(new JPanel(new GridLayout(1,2)){{
                add(start);
                add(stop);
            }},BorderLayout.WEST);
        }};

        setContentPane(new JPanel(new BorderLayout()){{
            add(controlPanel,BorderLayout.NORTH);
            add(contentPanel,BorderLayout.CENTER);
        }});


        start.addActionListener(actionEvent -> startTest());
        stop.addActionListener(actionEvent -> stopTest());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Тест БСКП");
        setSize(new Dimension(1600,900));
        setResizable(false);
    }


    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LimitSwitchTest limitSwitchTest = new LimitSwitchTest();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            limitSwitchTest.setLocation(dim.width/2-limitSwitchTest.getSize().width/2, dim.height/2-limitSwitchTest.getSize().height/2);
            limitSwitchTest.setVisible(true);
        });
    }
}
