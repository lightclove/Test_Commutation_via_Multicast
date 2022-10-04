import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * класс отвечающий за создание сообщения "запрос на режим тешины" и проверку ответной квитанции
 */
class MessageSilenceModeRequest {
    /**
     * условное имя отправителя (программы TestBI)
     */
    private byte src = (byte) 0xCA;
    /**
     * условное имя получателя (БКП)
     */
    private byte dst = (byte) 0xCB;
    /**
     * длина сообщения запроса тишины
     */
    private int lengthMessageSMR = 13;

    /**
     * создание сообщения запроса тишины
     *
     * @param sequence -циклический номер сообщения
     * @param silence  -длительность режима тишины
     * @return массив байт соообщеия Запрос режима тишины
     */
    byte[] createMessage(int sequence, int silence) {
        ByteBuffer bb = ByteBuffer.allocate(lengthMessageSMR).order(ByteOrder.BIG_ENDIAN);
        bb.position(0);

        //индификатор сообщения запроса тишины
        short idMessageSMR = 11;
        bb.put(src).put(dst).putShort(idMessageSMR).putInt(sequence).putInt(silence);

        return bb.array();
    }
    byte[] createMessage(int sequence, int silence,byte setDevais) {
        ByteBuffer bb = ByteBuffer.allocate(lengthMessageSMR).order(ByteOrder.BIG_ENDIAN);
        bb.position(0);

        //индификатор сообщения запроса тишины
        short idMessageSMR = 11;
        bb.put(src).put(dst).putShort(idMessageSMR).putInt(sequence).putInt(silence).put(setDevais);

        return bb.array();
    }
    /**
     * метод проверяющийм правильность квитанции
     *
     * @param mesSilenceModeRequest - отправленное сообщение запроса тишины
     * @param mesReceipt            - квитанция полученная на сообщение запроса тишины
     * @return true - когда квитанция соотетствует фортату и поле тип квитанции 1
     * false - в остальных случаях
     */
    boolean checkMessage(byte[] mesSilenceModeRequest, byte[] mesReceipt) {

        //проверяем длину сообщения
        //длина сообщения квитанции
        int lengthMessageReceipt = 10;
        System.out.println("lengthMessageReceipt= "+mesReceipt.length );
        if (mesReceipt.length == lengthMessageReceipt) {

            //разбор сообщения mesReceipt
            ByteBuffer bbReceipt = ByteBuffer.allocate(mesReceipt.length).order(ByteOrder.BIG_ENDIAN);
            bbReceipt.position(0);
            bbReceipt.put(mesReceipt);
            bbReceipt.position(0);
            byte srcRequest = bbReceipt.get();
            byte dstRequest = bbReceipt.get();
            short idRequest = bbReceipt.getShort();
            short typeRequest = bbReceipt.getShort();
            int sequenceRequest = bbReceipt.getInt();

            // получаем циклический номер сообщения запроса тишины (mesSilenceModeRequest)
            ByteBuffer bbSMR = ByteBuffer.allocate(lengthMessageSMR).order(ByteOrder.BIG_ENDIAN);
            bbSMR.position(0);
            bbSMR.put(mesSilenceModeRequest);
            bbSMR.position(4);
            int sequenceSMR = bbSMR.getInt();

            //проверка коректности квитанция (если все услоия true, то кокектная квитанция)
            //индификатор сообщения квитанции

            short idMessageReceipt = 12;
            if ((srcRequest == dst) &                   //квитанция отправил получатель запроса тишины
                    (dstRequest == src) &              //получательм квитанции является тестовый ПК (мы)
                    (idRequest == idMessageReceipt) &  //в квитанции указан правельный индификатор
                    (typeRequest == (short)1) &               //тип квитанции == 1
                    (sequenceRequest == sequenceSMR)) //циклическии номера сообщений равны
                return true;
        }
        //
        return false;

    }

}
