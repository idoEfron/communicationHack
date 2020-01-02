import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        UDPMulticastServer ser1 = new UDPMulticastServer();
        UDPMulticastServer ser2 = new UDPMulticastServer();
        Thread severThread1 = new Thread(ser1);
        severThread1.start();
        UDPMulticastClient client = new UDPMulticastClient(1);
        //client.enterVariables();
        Thread t = new Thread(client);
        t.start();

    }
}
