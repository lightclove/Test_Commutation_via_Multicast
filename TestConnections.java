package ru.nppame.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TestConnections extends JFrame {
  private static final long serialVersionUID = -482967528453725691L;
  
  private JPanel mainPanel = null;
  
  private JPanel buttonsPane = null;
  
  private JPanel indiecatorsPane = null;
  
  private JPanel indiecator1Pane = null;
  
  private JPanel indiecator2Pane = null;
  
  private JLabel indiecator1Label = null;
  
  private JLabel indiecator1Timer = null;
  
  private JLabel indiecator2Label = null;
  
  private JLabel indiecator2Timer = null;
  
  private JButton startButton = null;
  
  private JButton stopButton = null;
  
  private StartButtonThread startButtonCounterThread = null;
  
  private StartButtonThread startButtonPainterCounterThread = null;
  
  private String[] portsText = new String[] { ", ", "1", "2", ", ", ", "<html>", "<br>", "</html>" };
  
  public TestConnections() {
    initialize();
  }
  
  private void initialize() {
    setSize(350, 175);
    setContentPane(getMainPanel());
    setTitle("2);
  }
  
  private JPanel getMainPanel() {
    if (this.mainPanel == null) {
      this.mainPanel = new JPanel();
      this.mainPanel.setLayout(new BoxLayout(this.mainPanel, 1));
      this.mainPanel.add(getButtonsPane());
      this.mainPanel.add(getIndiecatorsPane());
    } 
    return this.mainPanel;
  }
  
  protected JPanel getIndiecator1Pane() {
    if (this.indiecator1Pane == null) {
      this.indiecator1Pane = new JPanel();
      BoxLayout b = new BoxLayout(this.indiecator1Pane, 1);
      this.indiecator1Pane.setLayout(b);
      this.indiecator1Pane.add(getIndiecator1Label());
      this.indiecator1Pane.add(getIndiecator1Timer());
      this.indiecator1Pane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 6));
    } 
    return this.indiecator1Pane;
  }
  
  protected JPanel getIndiecator2Pane() {
    if (this.indiecator2Pane == null) {
      this.indiecator2Pane = new JPanel();
      BoxLayout b = new BoxLayout(this.indiecator2Pane, 1);
      this.indiecator2Pane.setLayout(b);
      this.indiecator2Pane.add(getIndiecator2Label());
      this.indiecator2Pane.add(getIndiecator2Timer());
      this.indiecator2Pane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 6));
    } 
    return this.indiecator2Pane;
  }
  
  private JPanel getButtonsPane() {
    if (this.buttonsPane == null) {
      this.buttonsPane = new JPanel();
      this.buttonsPane.setLayout(new FlowLayout(1, 20, 20));
      this.buttonsPane.add(getStartButton());
      this.buttonsPane.add(getStopButton());
    } 
    return this.buttonsPane;
  }
  
  private JPanel getIndiecatorsPane() {
    if (this.indiecatorsPane == null) {
      this.indiecatorsPane = new JPanel(new FlowLayout(1, 10, 20));
      this.indiecatorsPane.add(getIndiecator1Pane());
      this.indiecatorsPane.add(getIndiecator2Pane());
    } 
    return this.indiecatorsPane;
  }
  
  private JLabel getTimerLabel1() {
    return this.indiecator1Timer;
  }
  
  private JLabel getTimerLabel2() {
    return this.indiecator2Timer;
  }
  
  private JButton getStartButton() {
    if (this.startButton == null) {
      this.startButton = new JButton();
      this.startButton.setText(");
      this.startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      if (TestConnections.this.startButtonCounterThread == null) {
                        TestConnections.this.startButtonCounterThread = new StartButtonThread(TestConnections.this.getTimerLabel1(), TestConnections.this.getTimerLabel2(), TestConnections.this.getIndiecator1Pane(), TestConnections.this.getIndiecator2Pane());
                        TestConnections.this.startButtonCounterThread.start();
                        TestConnections.this.getStartButton().setEnabled(false);
                        TestConnections.this.getStopButton().setEnabled(true);
                      } 
                    }
                  });
            }
          });
    } 
    return this.startButton;
  }
  
  private JButton getStopButton() {
    if (this.stopButton == null) {
      this.stopButton = new JButton();
      this.stopButton.setText(");
      this.stopButton.setEnabled(false);
      this.stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      if (TestConnections.this.startButtonCounterThread != null) {
                        TestConnections.this.startButtonCounterThread.requestForStop();
                        TestConnections.this.startButtonCounterThread = null;
                        TestConnections.this.getStartButton().setEnabled(true);
                        TestConnections.this.getStopButton().setEnabled(false);
                      } 
                    }
                  });
            }
          });
    } 
    return this.stopButton;
  }
  
  public void dispose() {
    if (this.startButtonCounterThread != null)
      this.startButtonCounterThread.requestForStop(); 
    this.startButtonCounterThread = null;
    super.dispose();
  }
  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
          TestConnections thisClass = new TestConnections();
          thisClass.setDefaultCloseOperation(2);
          thisClass.setResizable(false);
          thisClass.setVisible(true);
        });
  }
  
  private JLabel getIndiecator1Label() {
    return this.indiecator1Label = new JLabel(this.portsText[7] + this.portsText[0] + " " + this.portsText[1] + " " + this.portsText[2] + ": " + this.portsText[8] + this.portsText[4] + " " + this.portsText[5] + " " + this.portsText[6] + this.portsText[9]);
  }
  
  private JLabel getIndiecator2Label() {
    return this.indiecator2Label = new JLabel(this.portsText[7] + this.portsText[0] + " " + this.portsText[1] + " " + this.portsText[3] + ": " + this.portsText[8] + this.portsText[4] + " " + this.portsText[5] + " " + this.portsText[6] + this.portsText[9]);
  }
  
  private JLabel getIndiecator1Timer() {
    return this.indiecator1Timer = new JLabel("00:00");
  }
  
  private JLabel getIndiecator2Timer() {
    return this.indiecator2Timer = new JLabel("00:00");
  }
  
  public synchronized void toggleIndiecatorPane1Color() {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (TestConnections.this.getIndiecator1Pane().getBackground() == Color.RED) {
              TestConnections.this.getIndiecator1Pane().setBackground(Color.GREEN);
            } else if (TestConnections.this.getIndiecator1Pane().getBackground() == Color.GREEN) {
              TestConnections.this.getIndiecator1Pane().setBackground(Color.RED);
            } 
            TestConnections.this.repaint();
          }
        });
  }
  
  public synchronized void toggleIndiecatorPane2Color() {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (TestConnections.this.getIndiecator2Pane().getBackground() == Color.RED) {
              TestConnections.this.getIndiecator2Pane().setBackground(Color.GREEN);
            } else if (TestConnections.this.getIndiecator2Pane().getBackground() == Color.GREEN) {
              TestConnections.this.getIndiecator2Pane().setBackground(Color.RED);
            } 
            TestConnections.this.repaint();
          }
        });
  }
}
