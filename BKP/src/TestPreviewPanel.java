import javax.swing.*;
import java.awt.*;

/**
 * Класс определюящий панель графического интерфейса визуализирующию работу программы и показывающий результаты теста работы с сетью БКП
 */
class TestPreviewPanel extends JPanel {
    /**
     * количество отпраленых запросов тишины
     */
    private int messageSMR;
    /**
     * количество принятых правельных квитанций
     */
    private int trueMesReceipt;
    /**
     * количество принятых неправильных квитанций
     */
    private int falseMesReceip;
    /**
     * элемент GUI отпражающий количество отпраленых сообщений запроса режима тишины
     */
    private JTextField numbersMessageSMRTextField;
    /**
     * элемент GUI отпражающий количество принятых квитанций в соответствии с протоколом обмена
     */
    private JTextField trueNumbersMesReceiptTextField;
    /**
     * элемент GUI отпражающий количество ошибок
     */
    private JTextField falseMesReceiptextField;
    /**
     * таймер (оживляет окно приложения)
     */
    private TimerLabel timerLabel;
    /**
     * константа высоты элементов панели
     */
    private int height = 30;
    /**
     * константа ширины элементов панели
     */
    private int width = 150;
    //надписи на GUI
    private JLabel label=new JLabel("");
    private JLabel label1=new JLabel("Таймер (мин:сек)");
    private JLabel label2=new JLabel("Выдано команд");
    private JLabel label3=new JLabel("Принятых подтверждений");
    private JLabel label4=new JLabel("Непринятых подтверждений");
    public TestPreviewPanel() {
        messageSMR=0;
        trueMesReceipt=0;
        falseMesReceip=0;
        Dimension d = new Dimension(width, height);
        timerLabel=new TimerLabel(20);
        timerLabel.setPreferredSize(d);
        numbersMessageSMRTextField=new JTextField("0");
        numbersMessageSMRTextField.setPreferredSize(d);
        numbersMessageSMRTextField.setEditable(false);
        numbersMessageSMRTextField.setFocusable(false);
        trueNumbersMesReceiptTextField=new JTextField("0");
        trueNumbersMesReceiptTextField.setPreferredSize(d);
        trueNumbersMesReceiptTextField.setEditable(false);
        trueNumbersMesReceiptTextField.setEditable(false);
        falseMesReceiptextField=new JTextField("0");
        falseMesReceiptextField.setPreferredSize(d);
        falseMesReceiptextField.setEditable(false);
        falseMesReceiptextField.setFocusable(false);

        //компановка элеметов панели
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;//GridBagConstraints.REMAINDER;
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;

        gbl.setConstraints(label, gridBagConstraints);
        add(label);
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 1;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(label1, gridBagConstraints);
        add(label1);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 1;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(timerLabel, gridBagConstraints);
        add(timerLabel);
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 2;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(label2, gridBagConstraints);
        add(label2);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 2;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(numbersMessageSMRTextField, gridBagConstraints);
        add(numbersMessageSMRTextField);
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 3;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(label3, gridBagConstraints);
        //add(label3);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 3;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(trueNumbersMesReceiptTextField, gridBagConstraints);
        //add(trueNumbersMesReceiptTextField);
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 4;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(label4, gridBagConstraints);
        add(label4);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 4;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(falseMesReceiptextField, gridBagConstraints);
        add(falseMesReceiptextField);

    }

    /**
     * установка значения количества отпраленных сообщений запроса тишины
     */
    public void addMessageSMR() {
        messageSMR++;
        numbersMessageSMRTextField.setText(Integer.toString(messageSMR));
    }
    /**
     * установка значения количества принятых квитанций
     */
    public void addTrueNumbersMesReceipt() {
        trueMesReceipt++;
        trueNumbersMesReceiptTextField.setText(Integer.toString(trueMesReceipt));
    }

    /**
     * установка значения количества ошибок при приеме квитанций
     */
    public void addFalseMesReceiptext() {
        falseMesReceip++;
        falseMesReceiptextField.setText(Integer.toString(falseMesReceip));
    }

    /**
     * старт / стоп  таймера
     * @param b -true старт
     *          -false стоп
     */
    public void stopStartTimerLabel(boolean b){
        if(b)
            timerLabel.start();
        else
            try {
                timerLabel.stop();
            }catch (NullPointerException e){

            }
    }
    public void clean(){
        messageSMR=0;
        numbersMessageSMRTextField.setText(Integer.toString(messageSMR));
        falseMesReceip=0;
        falseMesReceiptextField.setText(Integer.toString(falseMesReceip));
        trueMesReceipt=0;
        trueNumbersMesReceiptTextField.setText(Integer.toString(trueMesReceipt));
    }
}
