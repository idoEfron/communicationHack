import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class UDPMulticastClient implements Runnable {
    private String hash ="";
    private String wordLength = "";



    public static void main(String[] args) {
        Thread t = new Thread(new UDPMulticastClient());
        t.start();
    }

    public void receiveUDPMessage(String ip, int port) throws
            IOException {
        byte[] buffer = new byte[1024];
        MulticastSocket socket = new MulticastSocket(3117);
        InetAddress group = InetAddress.getByName("255.255.255.255");
        socket.joinGroup(group);
        while (true) {
            System.out.println("Waiting for multicast message...");
            DatagramPacket packet = new DatagramPacket(buffer,
                    buffer.length);
            socket.receive(packet);
            String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
            System.out.println("[Multicast UDP message received] >> " + msg);
            if ("OK".equals(msg)) {
                System.out.println("No more message. Exiting : " + msg);
                break;
            }
        }
        socket.leaveGroup(group);
        socket.close();
    }

    @Override
    public void run() {
        try {
            receiveUDPMessage("255.255.255.255", 3117);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void enterVariables() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to bacardi. Please enter the hash:");
        hash = scan.next();
        System.out.println("Please enter the input string length:");
        wordLength = scan.next();

    }
}