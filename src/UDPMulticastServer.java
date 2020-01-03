
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPMulticastServer extends Thread {

    protected DatagramSocket socket;
    protected boolean running;
    protected byte[] buf = new byte[256];

    public UDPMulticastServer() throws IOException {
        socket = new DatagramSocket(3117);
    }
    public void run() {
        running = true;
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                ByteArrayInputStream in = new ByteArrayInputStream(packet.getData());
                ObjectInputStream is = new ObjectInputStream(in);
                Message received = (Message) is.readObject();
                //String received = new String(packet.getData(), 0, packet.getLength());
                if(received!=null) {
                    //System.out.println(received.getType());
                    if (received.getStart().equals("ddd")) {
                        running = false;
                        //continue;
                    }

                    if(Character.compare(received.getType(),'1')==0){
                        received.setType('2');
                        byte[] offer = new byte[256];
                        offer = received.getBytes();
                        DatagramPacket serverPacket = new DatagramPacket(offer, offer.length, address, port);
                        socket.send(serverPacket);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}