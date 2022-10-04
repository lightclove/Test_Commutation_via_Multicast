import javax.swing.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * класс реализующий тест БКП
 */
class TestBI implements Runnable {
    private  SetBitPanel setBitPanel;

    SetOptionsTestPanel optionsTestPanel;

    private TestPreviewPanel testPreviewPanel;
    private ControlButtonPanel controlButtonPanel;
    AtomicBoolean flagError;
    AtomicBoolean flag;
    private int quantityMes;
    /**
     * счетчик (циклический номер сообшения)
     */
    private int i;
    private AtomicBoolean receivBool;
    /**
     * флаг для остановки теста
     */
    private AtomicBoolean runBool;
    /**
     * класс для работы с сообщением
     */
    private MessageSilenceModeRequest messageSilenceModeRequest;
    /**
     * класс работы с сокетом
     */
    private UdpSoket udpSoket1;



    TestBI(SetOptionsTestPanel optionsTestPanel, TestPreviewPanel testPreviewPanel,AtomicBoolean flag,AtomicBoolean flagError,SetBitPanel setBitPanel) {
        this.setBitPanel=setBitPanel;
        quantityMes = 0;
        runBool = new AtomicBoolean();
        receivBool = new AtomicBoolean();
        int portSend=optionsTestPanel.getPort();
        int portReceiv=optionsTestPanel.getPortR();

        int millisecondsTimeout = 30; //время ожидания квитанции на сообщение
        this.optionsTestPanel = optionsTestPanel;
        this.flag=flag;
        this.flagError=flagError;
        this.testPreviewPanel = testPreviewPanel;
        this.controlButtonPanel = null;
        messageSilenceModeRequest = new MessageSilenceModeRequest();
        try {
            udpSoket1 = new UdpSoket(portSend,portReceiv, optionsTestPanel.getIpBind(), optionsTestPanel.getIpSend(), millisecondsTimeout);

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

        i = 0;
        runBool.set(false);
        receivBool.set(false);
    }

    public void setControlButtonPanel(ControlButtonPanel controlButtonPanel) {
        this.controlButtonPanel = controlButtonPanel;
    }

    public void stopReceive() {


        testPreviewPanel.stopStartTimerLabel(false);
        if (runBool.get()) {
            i = 0;
            runBool.set(false);
        }
    }

    @Override
    public void run() {
        //получение параметров сообщения
        flagError.set(true);
        long timeSleep = 0;
        int silence = 0;
        try {
            quantityMes = optionsTestPanel.getQuantity();
            timeSleep = (long) (optionsTestPanel.getPeriod() * 1000);
            System.err.println(timeSleep);
            //период между сообщениями в милисекундах
            silence = optionsTestPanel.getLasting();//длительность режима тишины

        //проверка перед началом теста коректности параметров тестирования
        if ((timeSleep >= 50) & (silence >= 1) & (quantityMes >= 0)) {
            //получение и установка параметров теста
            i = 0;
            runBool.set(true);

            optionsTestPanel.editability(false);

            {
                testPreviewPanel.clean();
                if (optionsTestPanel.isUsed() )
                    receivRequest(timeSleep, silence, udpSoket1);

            }


        }
     } catch (NumberFormatException e) {
            flag.set(true);
        controlButtonPanel.setStopTest();
        flagError.set(false);
        JOptionPane.showMessageDialog(null, "Тест прекращен", "указаны неверные параметры", JOptionPane.ERROR_MESSAGE);

    }}

    //метод
    private void  receivRequest(long timeSleep, int silence, UdpSoket udpSoket) {
        try {
            Thread.sleep((long) (optionsTestPanel.getTimeOut() * 1000));
            testPreviewPanel.stopStartTimerLabel(true);

            while (runBool.get()&flagError.get()) {
                i++;
                if (!((quantityMes >= 1) & (quantityMes < i))) {
                    System.out.println("send " + i);
                    byte[] messageSMR = messageSilenceModeRequest.createMessage(i, silence,setBitPanel.getByte());

                    udpSoket.send(messageSMR);

                    testPreviewPanel.addMessageSMR();
                    long timeEndReceive = System.currentTimeMillis() + timeSleep;//время до следующей отправки сообщения
                    boolean flagMesReceipt = false;//flag квитанция принята
                    receivBool.set(true);
                    while (receivBool.get()) {
                        try {
                            byte[] mesReceipt = udpSoket.receive();
                            System.out.println("reciv");
                            //если квитанция еще не принята
                            if (!flagMesReceipt) {
                                boolean check = messageSilenceModeRequest.checkMessage(messageSMR, mesReceipt);
                                if (check) {
                                    //принято коректное сообшение
                                    testPreviewPanel.addTrueNumbersMesReceipt();
                                    System.out.println(check);
                                    flagMesReceipt = true;
                                }
                                //принято некоректное сообшение
                                else {
                                    flagMesReceipt = true;
                                    testPreviewPanel.addFalseMesReceiptext();
                                    System.out.println("error message format");
                                }
                            }
                            //квитанция уже получена
                            else {
                                testPreviewPanel.addFalseMesReceiptext();
                                System.out.println("error mes re receipt");
                            }
                        } catch (SocketTimeoutException e) {
                            if (!flagMesReceipt) {
                                System.out.println("timeout receiv");
                                flagMesReceipt = true;//20 двадцать милисикунд прошли а квитанция не пришла
                                testPreviewPanel.addFalseMesReceiptext();
                            }
                            try {
                                Thread.sleep(10);
                                //Exception бросается только при executor.shutdownNow()
                            } catch (InterruptedException e1) {
                                runBool.set(false);
                                receivBool.set(false);
                                stopReceive();
                            }
                            //проверка отправки следующего сообщения
                            if (System.currentTimeMillis() >= timeEndReceive)
                                receivBool.set(false);
                        }
                    }
                } else {

                    stopReceive();
                    if (flag.get())
                        controlButtonPanel.setStopTest();
                    else
                        flag.set(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            stopReceive();
        }
    }
}
