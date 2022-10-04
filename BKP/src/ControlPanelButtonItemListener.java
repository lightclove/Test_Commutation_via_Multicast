import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Обработчик нажатия кнопок ControlButtonPanel
 */
class ControlPanelButtonItemListener implements ItemListener {
    private ExecutorService executor1;
    private ExecutorService executor2;
    private TestBI testBI1;
    private TestBI testBI2;

    ControlPanelButtonItemListener(TestBI testBI1,TestBI testBI2) {
        this.testBI1 = testBI1;
        this.testBI2 = testBI2;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        JToggleButton btn = (JToggleButton) e.getSource();
        //определяем изменение состояния кнопки-выбрана
        if (btn.isSelected()) {
            //System.out.println(btn.getName()+ " focus: "+btn.isFocusPainted());
            //выполнение действия в соответствии с выбранной кнопкой
            switch (btn.getName()) {
                case "start": {

                    // action start
                    executor1 = Executors.newSingleThreadExecutor();
                    executor2 = Executors.newSingleThreadExecutor();
                    System.out.println("start test");
                    executor1.submit(testBI1);
                    executor2.submit(testBI2);
                    break;
                }
                case "stop": {

                    testBI1.stopReceive();
                    testBI2.stopReceive();
                    testBI1.flag.set(false);
                    testBI1.optionsTestPanel.editability(true);
                    testBI2.optionsTestPanel.editability(true);
                    // action stop
                    System.out.println("stop test");
                    executor1.shutdownNow();
                    executor2.shutdownNow();
                    break;
                }
                default:
                    System.out.println("I do not know this key");
            }
        }
    }
}