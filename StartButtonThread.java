package ru.nppame.view;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class StartButtonThread extends Thread {
    private volatile boolean requestForStop = false;

    private JLabel timerLabel1;

    private JLabel timerLabel2;

    private JPanel indie1;

    private JPanel indie2;

    private int counter1 = 0;

    private int counter2 = 0;

    private boolean isUp1 = false;

    private boolean isUp2 = false;

    public StartButtonThread(JLabel timerLabel1, JLabel timerLabel2, JPanel indie1, JPanel indie2) {
        this.timerLabel1 = timerLabel1;
        this.timerLabel2 = timerLabel2;
        this.indie1 = indie1;
        this.indie2 = indie2;
    }

    public void setTime(JLabel counter, int t) {
        counter.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(t / 60), Integer.valueOf(t % 60)}));
    }

    public synchronized boolean requestForStop() {
        System.out.println("requestForStop: entry");
        if (!this.requestForStop) {
            this.requestForStop = true;
            interrupt();
            System.out.println("requestForStop: exit");
            return true;
        }
        return false;
    }

    private synchronized void fireCounterChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (StartButtonThread.this.timerLabel1 != null && StartButtonThread.this.timerLabel2 != null) {
                    StartButtonThread.this.setTime(StartButtonThread.this.timerLabel1, StartButtonThread.this.counter1);
                    StartButtonThread.this.setTime(StartButtonThread.this.timerLabel2, StartButtonThread.this.counter2);
                }
            }
        });
    }

    private synchronized void paintPanel(final JPanel indie, final boolean isUp) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isUp == true)
                    indie.setBackground(Color.GREEN);
                if (!isUp)
                    indie.setBackground(Color.RED);
            }
        });
    }

    public void run() {
        System.out.println("run: entry");
        try {
            while (!interrupted() && !this.requestForStop) {
                if (this.isUp1 == true) {
                    paintPanel(this.indie1, this.isUp1);
                    fireCounterChanged();
                    sleep(1000L);
                } else {
                    paintPanel(this.indie1, this.isUp1);
                    this.counter1++;
                    fireCounterChanged();
                    sleep(1000L);
                }
                if (this.isUp2 == true) {
                    paintPanel(this.indie2, this.isUp2);
                    fireCounterChanged();
                    sleep(1000L);
                    continue;
                }
                paintPanel(this.indie2, this.isUp2);
                this.counter2++;
                fireCounterChanged();
                sleep(1000L);
            }
        } catch (InterruptedException ex) {
            System.out.println("run: thread interrupted!");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        System.out.println("run: exit");
    }
}
