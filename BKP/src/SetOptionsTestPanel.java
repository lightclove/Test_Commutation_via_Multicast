import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Класс определюящий панель графического интерфейса для ввода параметров теста:
 * длительность, период.
 */
class SetOptionsTestPanel extends JPanel {
    /**
     * ip адресс первого сетевого интерфейса тестового пк
     */
    private String ipBind;

    /**
     * ip адресс первого сетевого интерфейса изделия
     */
    private String ipSend;

    /**
     * порт на который отправляются сообщения
     */
    private int port;

    /**
     * порт на котором принимаются сообщения
     */
    private int portR;
    private JCheckBox jCheckBox;
    /**
     * Текстовое поле содержащие значение периуда между отправкой сообщений в секундах
     */
    private JFormattedTextField periodTextFild;

    /**
     * Текстовое поле содержащие значение длительности БИ в секундах (данные сообщения)
     */
    private JFormattedTextField lastingTextFild;
    /**
     * количество
     */
    private JFormattedTextField quantityTextFild;
    /**
     * задержка отправки первого пакета
     */
    private JFormattedTextField timeouTextFild;
    /**
     * константа высоты элементов панели
     */
    private int height = 30;
    /**
     * константа ширины элементов панели
     */
    private int width = 150;
    //надписи на GUI
    private JLabel label = new JLabel("Параметры (сек):");
    private JLabel label0 = new JLabel("Задержка старта, с.");
    private JLabel label1 = new JLabel("Длительность БИ, с.");
    private JLabel label2 = new JLabel("Период отравки БИ, с.");
    private JLabel label3 = new JLabel("Количество БИ");


    /**
     *
     * @param lasting- длительность в секундах
     * @param period- периуд в секундах
     * @param ipBind
     * @param ipSend
     * @param port
     */

    public SetOptionsTestPanel(int lasting, int period, String ipBind, String ipSend, int port, int portR, String ipEth, String ipEth1) {
        setFocusable(true);
        //фильтры ввода для  JTextField(
        InputVerifier verifierFloat =new InputVerifier() {
            public boolean verify(JComponent comp) {
                JTextField textField = (JTextField) comp;
                try {
                    Float.parseFloat(textField.getText());
                    return true;
                } catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(textField, "введите число", "Ошибка ввода данных",
                            JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
        };

        InputVerifier verifierInt =new InputVerifier() {
            public boolean verify(JComponent comp) {
                JTextField textField = (JTextField) comp;
                try {
                    Integer.parseInt(textField.getText());
                    return true;
                } catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(textField, "введите чмсло", "Ошибка ввода данных",
                            JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
        };
        label.setText(ipEth+": " + ipBind);
        this.ipBind = ipBind;
        this.ipSend = ipSend;
        this.port = port;
        this.portR = portR;
        jCheckBox = new JCheckBox(ipEth1, true);
        jCheckBox.setFocusable(false);
        Dimension d = new Dimension(width, height);
        NumberFormat numberFormatFloat = new DecimalFormat("##0.00");
        NumberFormat numberFormatInt = new DecimalFormat("##0");
        timeouTextFild =  new JFormattedTextField( numberFormatFloat);
        timeouTextFild.setValue(0);
        timeouTextFild.setPreferredSize(d);
        timeouTextFild.setInputVerifier(verifierFloat);


        periodTextFild = new JFormattedTextField( numberFormatFloat);
        periodTextFild.setValue(period);
        periodTextFild.setPreferredSize(d);
        periodTextFild.setInputVerifier(verifierFloat);


        lastingTextFild = new JFormattedTextField(numberFormatInt);
        lastingTextFild.setValue(lasting);
        lastingTextFild.setPreferredSize(d);
        lastingTextFild.setInputVerifier(verifierInt);


        quantityTextFild = new JFormattedTextField(numberFormatInt);
        quantityTextFild.setValue(0);
        quantityTextFild.setPreferredSize(d);
        quantityTextFild.setInputVerifier(verifierInt);


        label0.setPreferredSize(d);
        label1.setPreferredSize(d);
        label2.setPreferredSize(d);
        label3.setPreferredSize(d);

        //компановка элеметов панели
        GridBagLayout gbl = new GridBagLayout();
        JPanel textFildPanel =new JPanel();
        textFildPanel.setLayout(gbl);
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
        textFildPanel.add(label);
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 1;//GridBagConstraints.RELATIVE;

        gbl.setConstraints(label1, gridBagConstraints);
        textFildPanel.add(label1);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 1;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(lastingTextFild, gridBagConstraints);
        textFildPanel.add(lastingTextFild);
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 2;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(label2, gridBagConstraints);
        textFildPanel.add(label2);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 2;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(periodTextFild, gridBagConstraints);
        textFildPanel.add(periodTextFild);
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 3;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(label3, gridBagConstraints);
        textFildPanel.add(label3);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 3;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(quantityTextFild, gridBagConstraints);
        textFildPanel.add(quantityTextFild);
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 4;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(label0, gridBagConstraints);
        textFildPanel.add(label0);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 4;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(timeouTextFild, gridBagConstraints);
        textFildPanel.add(timeouTextFild);

        GridBagLayout gbl1 = new GridBagLayout();
        setLayout(gbl1);

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();

        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.fill = GridBagConstraints.NONE;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.gridwidth = 1;//GridBagConstraints.REMAINDER;
        gridBagConstraints1.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints1.gridy = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints1.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints1.ipadx = 0;
        gridBagConstraints1.ipady = 0;
        gridBagConstraints1.weightx = 0.0;
        gridBagConstraints1.weighty = 0.0;

        gbl1.setConstraints(jCheckBox, gridBagConstraints1);
        add(jCheckBox);
        gridBagConstraints1.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints1.gridy = 1;//GridBagConstraints.RELATIVE;
        gbl1.setConstraints(textFildPanel, gridBagConstraints1);
        add(textFildPanel);


    }


    public String getIpBind() {
        return ipBind;
    }

    public String getIpSend() {
        return ipSend;
    }

    public int getPortR() {
        return portR;
    }

    public int getPort() {
        return port;
    }

    /**
     *
     * @return использовать интерфейс при тестировании
     *  true- использовать
     *  false- не использовать
     */
    public boolean isUsed(){
        return jCheckBox.isSelected();
    }
    /**
     *
     * @param b true- разрешить редактирование параметров
     *          false- запретить редактирование параметров
     */
     void editability(boolean b){
         periodTextFild.setEditable(b);
         lastingTextFild.setEditable(b);
         quantityTextFild.setEditable(b);
         jCheckBox.setEnabled(b);
         requestFocus();

    }
    /**
     * метод возвращающий длительности БИ из текстового поля
     *
     * @return целое число согласно методу
     * @link #SetOptionsTestPane#lastingTextFild;
     * @see SetOptionsTestPanel#returnIntOfText(String st)
     */
     int getLasting()throws NumberFormatException  {
        return returnIntOfText(lastingTextFild.getText());
    }

    /**
     * метод возвращающий период БИ из текстового поля
     *
     * @return целое число согласно методу
     * @link #SetOptionsTestPane#periodTextFild
     * @see SetOptionsTestPanel#returnIntOfText(String st)
     */
     float getPeriod()throws NumberFormatException  {
        return returnFloat(periodTextFild.getText());
    }
    /**
     * метод возвращающий количестово отправлений сообщений БИ
     * @return целое число согласно методу
     * @link #SetOptionsTestPane#periodTextFild
     * @see SetOptionsTestPanel#returnIntOfText(String st)
     */
    int getQuantity()throws NumberFormatException {
        return returnIntOfText(quantityTextFild.getText());
    }

    /**
     * метод возвращает TimeOut при отправке первого сообщения в секундах
     * @return
     * @throws NumberFormatException
     */
    float getTimeOut()throws NumberFormatException {
        return returnFloat(timeouTextFild.getText());
    }



    /**
     * Метод возвращающий целочисленное значение из строки и создает диологовое окно
     * если строка не содержит целого числа или целое число < 1
     *
     * @param st - строчное представление целого числа
     * @return целое число i >=1,!!!!!!!!!!!!!!!!!!!!!!!!!
     * либо i=-1, если i<1 или st не содержит целого числа
     */

    private int returnIntOfText(String st)throws NumberFormatException  {
        int i = -1;

            i = Integer.parseInt(st);
            if (i >= 0) {
                return i;
            }else {
                return -1;
            }

    }
    private  float returnFloat(String st){
        float f = -1;
        f= Float.parseFloat(st);
        if (f >= 0) {
            return f;
        }else {
            return -1;
        }
    }
}