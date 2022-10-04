import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Класс JLabel с секундомером
 */
public class TimerLabel extends JLabel {
    private Timer timer;
    private TimerTask timerTask;
    public TimerLabel(int fontSize) {
        setFont(new Font(this.getFont().getFontName(), this.getFont().getStyle(), fontSize));
        //TimerLabel.this.setText(String.format("%02d:%02d", 0 / 60, 0 % 60));
        setTime(0);
    }

    public void start() {
        timer = new Timer();
        timerTask = new myTimerTask(this);
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void stop() {
        timer.cancel();
    }
    public void setTime(int t){
        setText(String.format("%02d:%02d", t / 60, t % 60));
    }


    private class myTimerTask extends TimerTask {
        private TimerLabel timerLabel;
        private volatile int time = -1;

        public myTimerTask(TimerLabel timerLabel) {
            this.timerLabel = timerLabel;
        }

        private Runnable refresher = new Runnable() {
            @Override
            public void run() {
                int t = time;
                timerLabel.setTime(t);
            }
        };

        @Override
        public void run() {
            time++;
            SwingUtilities.invokeLater(refresher);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TimerLabel timerLabel = new TimerLabel(36);
        JButton button2 = new JButton("stop");
        JButton button1 = new JButton("start");

        frame.getContentPane().setLayout(new FlowLayout());
        frame.add(timerLabel);

        frame.add(button1);
        frame.add(button2);


        frame.pack();
        frame.setVisible(true);

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timerLabel.stop();
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timerLabel.start();
            }
        });
    }
}
