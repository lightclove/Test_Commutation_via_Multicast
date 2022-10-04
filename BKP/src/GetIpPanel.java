import javax.swing.*;
import java.awt.*;

/**
 * Класс определюящий панель графического интерфейса для указания интерфейса
 * с которого исполняется тестирование и ip адрес изделия на который отправляется сообщение
 * во время теста
 */
class GetIpPanel extends JPanel {
    /**
     * ip адресс первого сетевого интерфейса тестового пк
     */
    private String ip1Bind;
    /**
     * ip адресс второго сетевого интерфейса тестового пк
     */
    private String ip2Bind;
    /**
     * ip адресс первого сетевого интерфейса изделия
     */
    private String ip1Send;
    /**
     * ip адресс второго сетевого интерфейса изделия
     */
    private String ip2Send;
    /**
     * порт на который отправляются сообщения
     */
    private int port;
    private JCheckBox jCheckBox1;
    private JCheckBox jCheckBox2;

    /**
     * конструктор экземпляра класса
     *
     * @param ip1Bind string- ip адресс первого сетевого интерфейса тестового пк
     * @param ip2Bind string- ip адресс второго сетевого интерфейса тестового пк
     * @param ip1Send string- ip адресс первого сетевого интерфейса изделия
     * @param ip2Send string- ip адресс второго сетевого интерфейса изделия
     * @param port-   порт на который отправляются сообщения
     */
    GetIpPanel(String ip1Bind, String ip2Bind, String ip1Send, String ip2Send, int port) {
        this.ip1Bind = ip1Bind;
        this.ip2Bind = ip2Bind;
        this.ip1Send = ip1Send;
        this.ip2Send = ip2Send;
        this.port = port;

        jCheckBox1 = new JCheckBox("Канал 1: " + ip1Bind, true);
        jCheckBox2 = new JCheckBox("Канал 2: " + ip2Bind, true);



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
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        JLabel label=new JLabel("Выбор канала:");
        gbl.setConstraints(label, gridBagConstraints);
        add(label);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gbl.setConstraints(jCheckBox1, gridBagConstraints);
        add(jCheckBox1);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;

        gbl.setConstraints(jCheckBox2, gridBagConstraints);
        add(jCheckBox2);
    }
    /**
     *
     * @param b true- разрешить выбор канала
     *          false- запретить выбор канала
     */
    public void editability(boolean b){
        jCheckBox1.setEnabled(b);
        jCheckBox2.setEnabled(b);

    }
    /**
     * метод для возвращающий ip адрес на котором создается сокет для взаимодействия с изделием
     *
     * @return String- ip адрес на котором создается сокет для взаимодействия с изделием
     */
    String getIpBind() {
        if (jCheckBox1.isSelected())
            return ip1Bind;
        else
            return ip2Bind;
    }

    /**
     * метод для возвращающий ip адрес изделия на который отправляются сообщения
     *
     * @return String- p адрес на котором создается сокет для взаимодействия с изделием
     */
    String getIpSend() {
        if (jCheckBox1.isSelected())
            return ip1Send;
        else
            return ip2Send;
    }

    /**
     * метод возвращает порт на который отправляются сообщения
     *
     * @return int - порт на который отправляются сообщения
     */
    int getPort() {
        return port;
    }

    public String getIp1Bind() {
        return ip1Bind;
    }

    public String getIp2Bind() {
        return ip2Bind;
    }

    public String getIp1Send() {
        return ip1Send;
    }

    public String getIp2Send() {
        return ip2Send;
    }
}
