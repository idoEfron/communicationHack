
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPBroadCastServer extends Thread {

    private HelperFunctions hf;
    protected DatagramSocket socket;
    protected boolean running;
    protected byte[] buf = new byte[1024];

    public UDPBroadCastServer() throws IOException {
        socket = new DatagramSocket(3117);
        hf = new HelperFunctions();

    }
    public void run() {
        running = true;
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                if(packet == null || packet.getData() ==null || packet.getAddress() == null){
                    System.out.println("server encountered corrupted packet. terminating program");
                    System.exit(0);
                }
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                Message received = Message.getMessage(packet.getData());
                //String received = new String(packet.getData(), 0, packet.getLength());
                if(received!=null) {
                    if (received.getStart().equals("end")) {
                        running = false;
                        //continue;
                    }

                    if(Character.compare(received.getType(),'1')==0){
                        received.setType('2');
                        byte[] offer = new byte[1024];
                        offer = received.getBytes();
                        DatagramPacket serverPacket = new DatagramPacket(offer, offer.length);
                        address = packet.getAddress();
                        port = packet.getPort();
                        serverPacket = new DatagramPacket(offer, offer.length, address, port);
                        socket.send(serverPacket);
                    }
                    if(Character.compare(received.getType(),'3')==0){
                        address = packet.getAddress();
                        port = packet.getPort();
                        String word = hf.tryDeHash(received.getStart(),received.getEnd(),new String(received.getHash()));
                        if(word == null){
                            //NAck
                            DatagramPacket serverPacket = new DatagramPacket(buf, buf.length);
                            Message messageClient = new Message(received.getTeamName(), '5', received.getHash(), received.getOriginalLength(), "NA", "NA");
                            buf = messageClient.getBytes();
                            serverPacket= new DatagramPacket(buf, buf.length, address, port);
                            socket.send(serverPacket);
                            running=false;
                        }
                        else{
                            //ACK
                            DatagramPacket serverPacket = new DatagramPacket(buf, buf.length);
                            Message messageClient = new Message(received.getTeamName(), '4', received.getHash(), received.getOriginalLength(), word, word);
                            buf = messageClient.getBytes();
                            serverPacket = new DatagramPacket(buf, buf.length, address, port);
                            socket.send(serverPacket);
                            running=false;
                        }
                    }
                    /*if(Character.compare(received.getType(),'3') !=0 && Character.compare(received.getType(),'1') !=0){
                        System.out.println("invalid message type. terminating program.");
                        System.exit(0);
                    }*/
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