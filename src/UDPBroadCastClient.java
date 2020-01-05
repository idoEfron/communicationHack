import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.stream.Collectors;

public class UDPBroadCastClient implements Runnable {
    private HelperFunctions hf;
    private DatagramSocket socket;
    private InetAddress address;
    private int expectedServerCount;
    private byte[] buf;
    private List<InetAddress> servers;
    private String team;
    private char length;
    private String hash;


    public UDPBroadCastClient(int expectedServerCount) throws Exception {
        team = new String("        inon    &     ido       ");
        hf = new HelperFunctions();
        servers = new ArrayList<>();
        this.expectedServerCount = expectedServerCount;
        this.address = InetAddress.getByName("255.255.255.255");
    }

    public int discoverServers(Message msg) throws IOException, ClassNotFoundException, InterruptedException {
        initializeSocketForBroadcasting();
        copyMessageOnBuffer(msg);

        // When we want to broadcast not just to local network, call listAllBroadcastAddresses() and execute broadcastPacket for each value.
        broadcastPacket(address);
        //Thread.sleep(100);

        return receivePackets();
    }

    private void initializeSocketForBroadcasting() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        //socket.setSoTimeout(1000);


    }

    private void copyMessageOnBuffer(Message msg) throws IOException {
        buf = msg.getBytes();
    }

    private void broadcastPacket(InetAddress address) throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 3117);
        socket.send(packet);
    }

    private int receivePackets() throws IOException, ClassNotFoundException, InterruptedException {
        int serversDiscovered = 0;
        while (serversDiscovered != expectedServerCount) {
            receivePacket();
            serversDiscovered++;
        }
        return serversDiscovered;
    }

    private void receivePacket() throws IOException, ClassNotFoundException, InterruptedException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        socket.receive(packet);
        servers.add(packet.getAddress());
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        Message message = Message.getMessage(packet.getData());
        request(message,port);

    }

    public void request(Message message,int port) throws IOException, InterruptedException {
        //todo
        Thread.sleep(1000);
        //socket.setSoTimeout();
        String[] StringDistribute = hf.divideToDomains(Integer.parseInt(String.valueOf(length)),expectedServerCount);
        DatagramPacket packetClient = null;
        int i =0;
        for(InetAddress server: servers){
            Message messageClient = new Message(message.getTeamName(), '3', message.getHash(), message.getOriginalLength(), StringDistribute[i], StringDistribute[i+1]);
            copyMessageOnBuffer(messageClient);
            packetClient = new DatagramPacket(buf, buf.length, server, port);
            socket.send(packetClient);
            i++;
        }
    }


    public void close() {
        socket.close();
    }

    @Override
    public void run() {
        // hash input
        System.out.println("Welcome to " + team + ". Please enter the hash:");
        Scanner sc = new Scanner(System.in);
        String hash = sc.nextLine();
        if (hash.length() != 40) {
            System.out.println("invalid input. terminating program.");
            System.exit(0);
        }

        // length input
        System.out.println("Please enter the input string length:");
        char length ='N';
        try {
            String lengthS = sc.nextLine();
            if(lengthS.length() ==1){
                length = lengthS.charAt(0);
                if(!Character.isDigit(length)){
                    throw new NumberFormatException();
                }
            }
            else{
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("invalid input. terminating program.");
            System.exit(0);

        }

        //broadcast

        try {
            String start="";
            String end="";
            for(int i=0;i<Integer.parseInt(String.valueOf(length));i++){
                start = start+"a";
                end = end +"z";
            }
            this.length = length;
            this.hash = hash;
            Message message = new Message(team.toCharArray(), '1', hash.toCharArray(), length, start, end);
            int numOfServers = discoverServers(message);
            //socket.setBroadcast(false);
            String word = getAnswer();
            if(word ==null){
                System.out.println("no result. closing program");
                System.exit(0);
            }
            else{
                System.out.println("The input string is "+word);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        close();
    }

    private String getAnswer() {
        int i=0;
        while(i!=expectedServerCount){
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                Message message = Message.getMessage(packet.getData());
                if(Character.compare(message.getType(),'4')==0){
                    return message.getStart();
                }
                i++;

            } catch (IOException e) {
                System.out.println("can't get answer from server. terminating program.");
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("can't get answer from server. terminating program.");
                System.exit(0);
            }
        }
        return null;
    }
}