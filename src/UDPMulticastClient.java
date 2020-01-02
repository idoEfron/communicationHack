import java.io.IOException;
import java.net.*;
import java.util.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.stream.Collectors;

public class UDPMulticastClient implements Runnable {
    private DatagramSocket socket;
    private DatagramSocket client;
    private InetAddress address;
    private int expectedServerCount;
    private byte[] buf;
    private Set<InetAddress> servers;


    public UDPMulticastClient(int expectedServerCount) throws Exception {
        servers = new HashSet<>();
        this.expectedServerCount = expectedServerCount;
        this.address = InetAddress.getByName("255.255.255.255");
        client = new DatagramSocket(3117, InetAddress.getByName("localhost"));
    }
    public int discoverServers(Message msg) throws IOException, InterruptedException {
        initializeSocketForBroadcasting();
        copyMessageOnBuffer(msg);

        // When we want to broadcast not just to local network, call listAllBroadcastAddresses() and execute broadcastPacket for each value.
        broadcastPacket(address);

        Thread.sleep(700);
        return receivePackets();
    }

    List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            broadcastList.addAll(networkInterface.getInterfaceAddresses().stream().filter(address -> address.getBroadcast() != null)
                    .map(address -> address.getBroadcast())
                    .collect(Collectors.toList()));
        }
        return broadcastList;
    }

    private void initializeSocketForBroadcasting() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);

    }
    private void copyMessageOnBuffer(Message msg) throws IOException {
        buf = msg.getBytes();
    }
    private void broadcastPacket(InetAddress address) throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 3117);
        socket.send(packet);
    }

    private int receivePackets() throws IOException {
        int serversDiscovered = 0;
        while (serversDiscovered != expectedServerCount) {
            receivePacket();
            serversDiscovered++;
        }
        return serversDiscovered;
    }

    private void receivePacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        servers.add(packet.getAddress());
    }

    public void close() {
        socket.close();
    }

    public void enterVariables() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to bacardi. Please enter the hash:");
        String hash = scan.next();
        System.out.println("Please enter the input string length:");
        String wordLength = scan.next();

    }

    @Override
    public void run() {
        try {
            char[] ido= {'i','d','o'};
            int numOfServers = discoverServers(new Message(ido,'1',ido,'3',"ddd","ooo"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        close();
    }
}