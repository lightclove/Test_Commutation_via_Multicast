import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by user on 25.05.16.
 */
public class RSLineSwitcher extends JFrame{
    private SerialPort serialPort;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture future;

    private JCheckBox rts = new JCheckBox("RTS");
    private JCheckBox dtr= new JCheckBox("DTR");
    private JComboBox<String> ports = new JComboBox<>(SerialPortList.getPortNames());

    private JTextField delay = new JTextField(){{
        setPreferredSize(new Dimension(100, (int) getPreferredSize().getHeight()));
    }};
    private JTextField duration = new JTextField(){{
        setPreferredSize(new Dimension(100, (int) getPreferredSize().getHeight()));
    }};

    private enum State{DELAY,DURATION}

    private JButton start = new JButton("START"){{
        addActionListener(e -> {
            start.setEnabled(false);
            stop.setEnabled(true);
            if(future != null && !future.isDone()){
                future.cancel(true);
            }

            if(serialPort != null){
                try {
                    if(serialPort.isOpened()){
                        serialPort.closePort();
                    }

                } catch (SerialPortException spe) {
                    displayError(spe);
                }
            }

            serialPort = new SerialPort(String.valueOf(ports.getSelectedItem()));

            try {
                serialPort.openPort();

                try{
                    final AtomicLong estimated = new AtomicLong(Long.parseLong(delay.getText()));
                    final AtomicReference<State> currentState = new AtomicReference<>(State.DELAY);
                    future = executorService.scheduleAtFixedRate(() -> {
                        if(estimated.decrementAndGet() <= 0L){
                            switch (currentState.get()){
                                case DELAY:
                                    currentState.set(State.DURATION);
                                    estimated.set(Long.parseLong(duration.getText()));
                                    try {
                                        serialPort.setDTR(false);
                                        serialPort.setRTS(false);
                                    } catch (SerialPortException spe) {
                                        cancelAll();
                                        displayError(spe);
                                    }


                                    break;
                                case DURATION:
                                    currentState.set(State.DELAY);
                                    estimated.set(Long.parseLong(delay.getText()));
                                    try {
                                        serialPort.setDTR(dtr.isSelected());
                                        serialPort.setRTS(rts.isSelected());
                                    } catch (SerialPortException spe) {
                                        cancelAll();
                                        displayError(spe);
                                    }

                                    break;
                                default:
                                    System.out.println("WTF?");
                                    break;
                            }
                        }
                    },0,1, TimeUnit.MILLISECONDS);
                }catch (NumberFormatException nfe){
                    cancelAll();
                    displayError(nfe);
                }
            } catch (SerialPortException spe) {
                cancelAll();
                displayError(spe);
            }
        });
    }


    };

    private void cancelAll() {
        stop.setEnabled(false);
        start.setEnabled(true);
        if(future != null) future.cancel(true);
    }

    private JButton stop = new JButton("STOP"){{
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAll();

                if(serialPort != null){
                    try {
                        serialPort.closePort();
                    } catch (SerialPortException spe) {
                        displayError(spe);
                    }
                }
            }
        });
    }};

    RSLineSwitcher(){
        setTitle("SerialPort RTS and DTR Line Switcher");
        setLayout(new BorderLayout());
        add(new JPanel(){{
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();

            gridBagConstraints.fill = MAXIMIZED_BOTH;

            //gridBagConstraints.weightx = 0.2;

            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            add(rts,gridBagConstraints);

            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            add(dtr,gridBagConstraints);

            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            add(ports,gridBagConstraints);

            //gridBagConstraints.weightx = 0.2;

            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            add(delay,gridBagConstraints);

            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            add(duration,gridBagConstraints);

            //gridBagConstraints.weightx = 0.5;

            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            add(new JLabel("Delay, ms",SwingConstants.LEFT),gridBagConstraints);

            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            add(new JLabel("Duration, ms",SwingConstants.LEFT),gridBagConstraints);

        }},BorderLayout.CENTER);

        add(new JPanel(){{
            setLayout(new GridLayout(0,2));
            add(start);
            add(stop);
        }},BorderLayout.SOUTH);

        pack();
    }

    private void displayError(Exception e) {
        JOptionPane.showMessageDialog(this,e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        SwingUtilities.invokeLater(() -> {
            RSLineSwitcher switcher = new RSLineSwitcher();
            switcher.setLocationRelativeTo(null);
            switcher.setVisible(true);
        });
    }
}
