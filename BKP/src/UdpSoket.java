import java.io.IOException;
import java.net.*;

/**
 * класс для приема отправки сообщений
 */
class UdpSoket {
    /**
     * порт на который отправляется сообщение
     */
    int port;
    /**
     * порт на котором принимаются сообщения сообщение
     */
    int portR;
    /**
     * ip адресс сетевого интерфейса с которого отправляются и принимаются сообщения
     */
    String bindIp;
    /**
     * ip на который отправяются соощения
     */
    String sendIp;
    /**
     * сокет для отправки и приема сообщений
     */
    DatagramSocket datagramSocket;

    /**
     * конструктор экземпляра класса с бесконечным временем ожиданием во времея приема сообщения
     *
     * @param port   порт на который отправляется  и принимаются сообщениясообщение
     * @param bindIp ip адрес сетевого интерфейса  с котрого осуществляется прием отправка сообщений
     * @param sendIp ip адрес куда отправляется сообщение
     * @throws UnknownHostException
     * @throws SocketException
     */
    public UdpSoket(int port,int portR, String bindIp, String sendIp) throws UnknownHostException, SocketException,BindException {
        this.port = port;
        this.portR = portR;
        this.bindIp = bindIp;
        this.sendIp = sendIp;
        datagramSocket = new DatagramSocket(portR, InetAddress.getByName(bindIp));
        System.out.println(datagramSocket.getLocalAddress().toString());

    }

    /**
     * конструктор экземпляра класса с ограниченным временем ожиданием во времея приема сообщения
     *
     * @param port                порт на который отправляется  и принимаются сообщениясообщение
     * @param bindIp              ip адрес сетевого интерфейса  с котрого осуществляется прием отправка сообщений
     * @param sendIp              ip адрес куда отправляется сообщение
     * @param millisecondsTimeout milliseconds -время ожидания сообщенияsendIp ip адрес куда отправляется сообщение
     * @throws UnknownHostException
     * @throws SocketException
     */
    public UdpSoket(int port,int portR, String bindIp, String sendIp, int millisecondsTimeout) throws UnknownHostException, SocketException {
        this.port = port;
        this.portR = portR;
        this.bindIp = bindIp;
        this.sendIp = sendIp;
       // datagramSocket =new DatagramSocket(new InetSocketAddress(InetAddress.getByName(bindIp),portR));// new DatagramSocket(port, InetAddress.getByName(bindIp));
        datagramSocket =new DatagramSocket(portR, InetAddress.getByName(bindIp));
        datagramSocket.setSoTimeout(millisecondsTimeout);


    }

    /**
     *
     * @return InetAddress locacalhost  bind soket
     */
    public InetAddress getLocalAddress(){
        return datagramSocket.getLocalAddress();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setBindIp(String bindIp) {
        this.bindIp = bindIp;
    }

    public void setSendIp(String sendIp) {
        this.sendIp = sendIp;
    }

    /**
     * метод отправки сообшения
     *
     * @param message отправляемое сообщение
     * @throws IOException "что то пошло не так, вот незадача"
     */
    public void send(byte[] message) throws IOException {
        DatagramPacket pack = new DatagramPacket(message, message.length,
                InetAddress.getByName(sendIp), port);
        datagramSocket.send(pack);
    }

    /**
     * прием сообщения
     *
     * @return принятое сообщение
     * @throws IOException SocketTimeoutException-если сообщение не пришло за промежуток времени
     *                     прочие ошибки ""что то пошло не так, вот незадача"
     */
    public byte[] receive() throws IOException {
        byte[] buf = new byte[1500];
        DatagramPacket r = new DatagramPacket(buf, buf.length);
        datagramSocket.receive(r);
        byte[] arrByte = new byte[r.getLength()];
        System.arraycopy(buf, 0, arrByte, 0, arrByte.length);
        return arrByte;
    }

    /**
     * метод для закрытия сокета
     */
    public void close() {
        datagramSocket.close();
    }
}
