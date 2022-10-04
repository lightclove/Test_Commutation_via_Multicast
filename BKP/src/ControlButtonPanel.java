import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;

/**
 * Класс определюящий панель графического интерфейса c управляющими элементами
 * (кнопки старт и стоп)
 */
class ControlButtonPanel extends JPanel {
    /**
     * обработчик нажатия кнопок
     */
    private ItemListener itemListener;


    /**
     * кнопка Старт
     */
    private JToggleButton buttonStart;
    /**
     * кнопка Стоп
     */
    private JToggleButton buttonStop;

    /**
     * константа высоты элементов панели
     */
    private int height = 30;
    /**
     * константа ширины элементов панели
     */
    private int width = 150;

    /**
     * Конструктор экземпляра класса
     *
     * @param itemListener -обработчик изменения состояний кнопок
     */
    ControlButtonPanel(ItemListener itemListener) {
        this.itemListener = itemListener;

        ControlButtonPanel();
    }
    void setStopTest(){
        buttonStart.setSelected(false);
        buttonStop.setSelected(true);
    }
    public void keyPressed(){
        if(buttonStart.isSelected())
           buttonStop.setSelected(true);
        else
            buttonStart.setSelected(true);
    }

    private void ControlButtonPanel() {
        JLabel label =new JLabel("Старт/Стоп: Ctrl +Space");
        Dimension d = new Dimension(width, height);

        buttonStart = new JToggleButton("Старт");
        buttonStart.setPreferredSize(d);
        buttonStart.setName("start");
        buttonStart.addItemListener(itemListener);
        //buttonStart.setFocusable(true);
        //buttonStart.addKeyListener(keyAdapter);

        buttonStop = new JToggleButton("Стоп");
        buttonStop.setPreferredSize(d);
        buttonStop.setName("stop");
        buttonStop.setSelected(true);
        buttonStop.addItemListener(itemListener);
        //buttonStop.setFocusable(true);
        //buttonStop.addKeyListener(keyAdapter);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(buttonStart);
        buttonGroup.add(buttonStop);

        //компановка элеметов панели
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor = GridBagConstraints.NORTH;
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



        gbl.setConstraints(buttonStart, gridBagConstraints);
        add(buttonStart);
        gridBagConstraints.gridx = 1;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 0;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(buttonStop, gridBagConstraints);
        add(buttonStop);

        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 1;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(label, gridBagConstraints);
        add(label);
    }
}
