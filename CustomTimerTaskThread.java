package ru.nppame.view;

import ru.nppame.utility.NetworkUtil;
import ru.nppame.utility.PropertiesLoader;
import java.awt.Color;
import java.io.IOException;
import static java.lang.Thread.interrupted;
import java.util.TimerTask;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author @codethief SmAl
 * @editor @codethief www.vk.com/FocusedSense
 *
 */
class CustomTimerTaskThread extends TimerTask {

    private TestConnections_Old timerLabel;
    private JPanel timerLabelPort1TimerPanel;
    private JPanel timerLabelPort2TimerPanel;
    private volatile int time = -1;

    public CustomTimerTaskThread(TestConnections_Old timerLabel, JPanel timerLabelPort1TimerPanel, JPanel timerLabelPort2TimerPanel) {
        this.timerLabel = timerLabel;
        this.timerLabelPort1TimerPanel = timerLabelPort1TimerPanel;
        this.timerLabelPort2TimerPanel = timerLabelPort2TimerPanel;
//        timerLabelPort1TimerPanel = new JPanel();
//        timerLabelPort2TimerPanel = new JPanel();
    }

    private Runnable refresher = new Runnable() {
        @Override
        public void run() {
            int t = time;
            timerLabel.setTime(t);
//            PropertiesLoader l = new PropertiesLoader("./configTestBSKP.properties"); // Temporary - will be replaced to combobox values
//            String senderAddress = l.getIp1Bind();
//            String receiverAddress = l.getIp2Bind();
//            String BSKPip = l.getBSKPip();
//            String group = l.getGroup();
//            int port = l.getPort();
//            timerLabelPort1TimerPanel = new JPanel();
//            timerLabelPort2TimerPanel = new JPanel();
//            
//            while (!interrupted()) {
//                try {
//                    if (NetworkUtil.mCastSendReceive(senderAddress, BSKPip, port, group, "This is sended Message", 3000) == true) {
//                        timerLabelPort1TimerPanel.setBackground(Color.GREEN);
//                        System.out.println("timerLabelPort1TimerPanel.setBackground(Color.GREEN)");
//                    } else {
//                        timerLabelPort1TimerPanel.setBackground(Color.RED);
//                        System.out.println("timerLabelPort1TimerPanel.setBackground(Color.RED");
//                    }
//                    if (NetworkUtil.mCastSendReceive(receiverAddress, BSKPip, port, group, "This is sended Message", 3000) == true) {
//                        timerLabelPort2TimerPanel.setBackground(Color.GREEN);
//                        System.out.println("timerLabelPort2TimerPanel.setBackground(Color.GREEN)");
//                    } else {
//                        timerLabelPort1TimerPanel.setBackground(Color.RED);
//                        System.out.println("timerLabelPort2TimerPanel.setBackground(Color.RED");
//                    }
//                } catch (IOException | InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
        }

    };

    public void run() {
        time++;
        SwingUtilities.invokeLater(refresher);
    }
}
