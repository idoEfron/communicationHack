import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        UDPMulticastServer ser1 = new UDPMulticastServer();
        UDPMulticastServer ser2 = new UDPMulticastServer();
        Thread severThread1 = new Thread(ser1);
        Thread severThread2 = new Thread(ser2);
        severThread1.start();
        severThread2.start();
        UDPMulticastClient client = new UDPMulticastClient(2);
        //client.enterVariables();
        Thread t = new Thread(client);
        t.start();

    }
}
