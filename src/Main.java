public class Main {
    public static void main(String[] args) throws Exception {
        UDPBroadCastServer ser1 = new UDPBroadCastServer();
        Thread severThread1 = new Thread(ser1);
        severThread1.start();
        UDPBroadCastClient client = new UDPBroadCastClient(1);
        //client.enterVariables();
        Thread t = new Thread(client);
        t.start();

    }
}
