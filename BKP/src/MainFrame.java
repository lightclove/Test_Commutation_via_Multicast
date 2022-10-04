import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Главное окно приложения
 */
public class MainFrame extends JFrame  {

    MainFrame(LoaderCheckProperties properties) throws HeadlessException {

       // se(bLocale);
        //Панель выбора ip для тестирования
        //GetIpPanel getIpPanel = new GetIpPanel(properties.getIp1Bind(), properties.getIp2Bind(), properties.getIp1Send(), properties.getIp2Send(), properties.getPort());
        //Панель установки параметров адаптера 1
        SetOptionsTestPanel setOptionsTestPanel1 = new SetOptionsTestPanel(1, 2,properties.getIp1Bind(),
                properties.getIp1Send(),
                properties.getPort(),
                properties.getPortR(),
                "IP Eth1",
                "Основной канал ПК");
        //Панель установки параметров адаптера 2
        SetOptionsTestPanel setOptionsTestPanel2 = new SetOptionsTestPanel(1, 2,properties.getIp2Bind(),
                properties.getIp2Send(),
                properties.getPort(),
                properties.getPortR(),
                "IP Eth2",
                "Резервный канал ПК");
        //панель отображения прохождения теста первый адаптер
        TestPreviewPanel testPreviewPanel1=new TestPreviewPanel();
        //панель отображения прохождения теста первый адаптер
        TestPreviewPanel testPreviewPanel2=new TestPreviewPanel();

        //класс реализующий тест БКП
        AtomicBoolean flag =new AtomicBoolean(false);
        AtomicBoolean flagError =new AtomicBoolean(false);

        //панель выставления битов для устройств
        ArrayList<String > stringListNameDevaice =new ArrayList<String>();
        stringListNameDevaice.add("MP-231-3");
        stringListNameDevaice.add("MP-231");
        stringListNameDevaice.add("CCПК");
        stringListNameDevaice.add("МФ-РЛК МР");
        stringListNameDevaice.add("МФ-РЛК L");
        stringListNameDevaice.add("МФ-РЛК ЗИХ");
        stringListNameDevaice.add("МФ-РЛК ЗИК");
        stringListNameDevaice.add("РЛК-X");

        SetBitPanel setBitPanel1= new SetBitPanel(stringListNameDevaice,properties.isTest(),properties.getBiDevices());
        SetBitPanel setBitPanel2= new SetBitPanel(stringListNameDevaice,properties.isTest(),properties.getBiDevices());


        TestBI testBI1 = new TestBI(setOptionsTestPanel1,testPreviewPanel1,flag, flagError,setBitPanel1);
        TestBI testBI2 = new TestBI(setOptionsTestPanel2,testPreviewPanel2, flag,flagError,setBitPanel2);
        //Обработчик нажатия кнопок ControlButtonPanel
        ControlPanelButtonItemListener itemListener = new ControlPanelButtonItemListener(testBI1,testBI2);
        //Панель с управляющими элементами
        ControlButtonPanel controlButtonPanel = new ControlButtonPanel(itemListener);

        testBI1.setControlButtonPanel(controlButtonPanel);
        testBI2.setControlButtonPanel(controlButtonPanel);


        //компановка элеметов окна
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;//GridBagConstraints.REMAINDER;
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.insets = new Insets(10, 30,10, 30);
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;

        //gbl.setConstraints(getIpPanel, gridBagConstraints);
        //add(getIpPanel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(setOptionsTestPanel1, gridBagConstraints);
        add(setOptionsTestPanel1);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(setOptionsTestPanel2, gridBagConstraints);
        add(setOptionsTestPanel2);


        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gbl.setConstraints(setBitPanel1, gridBagConstraints);
        add(setBitPanel1);


        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gbl.setConstraints(setBitPanel2, gridBagConstraints);
        add(setBitPanel2);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(testPreviewPanel1, gridBagConstraints);
        add(testPreviewPanel1);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(testPreviewPanel2, gridBagConstraints);
        add(testPreviewPanel2);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;//GridBagConstraints.RELATIVE;
        gbl.setConstraints(controlButtonPanel, gridBagConstraints);
        add(controlButtonPanel);


        // старт стоп теста по кнопке
        setFocusable(true);
        //addKeyListener(keyAdapter);
        //функционл окна приложения
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        MyDispatcher myDispatcher= new MyDispatcher();
        myDispatcher.setControlButtonPanel(controlButtonPanel);
        manager.addKeyEventDispatcher(myDispatcher);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("TestBKP");
    }
    private class MyDispatcher implements KeyEventDispatcher {
        ControlButtonPanel controlButtonPanel;

        public void setControlButtonPanel(ControlButtonPanel controlButtonPanel) {
            this.controlButtonPanel = controlButtonPanel;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID()==KeyEvent.KEY_RELEASED&&e.getKeyCode()==KeyEvent.VK_SPACE &&e.isControlDown()) {
                System.out.println("tester");
                controlButtonPanel.keyPressed();
            //} else if (e.getID() == KeyEvent.KEY_RELEASED) {
           //     System.out.println("2test2");
           // } else if (e.getID() == KeyEvent.KEY_TYPED) {
            //    System.out.println("3test3");
            }
            return false;
        }
    }

}
