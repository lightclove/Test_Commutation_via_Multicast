package ru.nppame.view;

import ru.nppame.utility.NetworkUtil;
import ru.nppame.utility.PropertiesLoader;
import java.awt.Color;
import java.io.IOException;
import static java.lang.Thread.interrupted;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author www.vk.com/FocusedSense
 */
public class PaintPanelsTaskThread extends Thread implements Runnable {

    private AtomicBoolean requestForStop
            = new AtomicBoolean(false);
    private JPanel indie1;
    private JPanel indie2;

    /**
     * @return the indie1
     */
    public JPanel getIndie1() {
        return indie1;
    }

    /**
     * @param indie1 the indie1 to set
     */
    public void setIndie1(JPanel indie1) {
        this.indie1 = indie1;
    }

    /**
     * @return the indie2
     */
    public JPanel getIndie2() {
        return indie2;
    }

    /**
     * @param indie2 the indie2 to set
     */
    public void setIndie2(JPanel indie2) {
        this.indie2 = indie2;
    }

    public PaintPanelsTaskThread(JPanel indie1, JPanel indie2) {
        indie1 = new JPanel();
        this.indie1 = indie1;
        indie1.setBackground(Color.RED);
        this.indie2 = indie2;
        indie2 = new JPanel();
        indie2.setBackground(Color.RED);
    }

    public synchronized void requestForStop() {
        System.out.println("requestForStop: entry");
        if (!requestForStop.get() && !interrupted()) {
            requestForStop.set(true);
            interrupt();
            System.out.println("requestForStop: exit");
        }
    }

    @Override
    public void run() {
        PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties");
        String senderAddress = l.getIp1Bind();
        String receiverAddress = l.getIp2Bind();
        String BSKPip = l.getBSKPip();
        String group1 = l.getGroup1();
        String group2 = l.getGroup2();
        int port = l.getPort();
        SwingUtilities.invokeLater(() -> {

            while (!this.requestForStop.get() && !interrupted()) {

                try {
                    if (NetworkUtil.mCastSendReceive(senderAddress, BSKPip, port, group1, "This is sended Message", 3000) == true) {
                        getIndie1().setBackground(Color.GREEN);
                        requestForStop.set(true);
                        System.out.println("timerLabelPort1TimerPanel.setBackground(Color.GREEN)"); // Debug Message will be replaced
                    } else {
                        getIndie1().setBackground(Color.RED);
                        requestForStop.set(false);
                        System.out.println("timerLabelPort1TimerPanel.setBackground(Color.RED"); // Debug Message will be replaced
                    }
                    if (NetworkUtil.mCastSendReceive(receiverAddress, BSKPip, port, group2, "This is sended Message", 3000) == true) {
                        getIndie2().setBackground(Color.GREEN);
                        requestForStop.set(true);
                        System.out.println("timerLabelPort2TimerPanel.setBackground(Color.GREEN)"); // Debug Message will be replaced
                    } else {
                        getIndie2().setBackground(Color.RED);
                        requestForStop.set(false);
                        System.out.println("timerLabelPort2TimerPanel.setBackground(Color.RED"); // Debug Message will be replaced
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                    requestForStop.set(false);
                }
            }
        });
    }
}
