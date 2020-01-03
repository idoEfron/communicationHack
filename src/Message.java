import java.io.*;

public class Message implements Serializable {
    private char[] teamName;
    private char type;
    private char[] hash;
    private char originalLength;
    private String start;
    private String end;

    public void setType(char type) {
        this.type = type;
    }

    public void setTeamName(char[] teamName) {
        this.teamName = teamName;
    }

    public void setHash(char[] hash) {
        this.hash = hash;
    }

    public void setOriginalLength(char originalLength) {
        this.originalLength = originalLength;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Message(char[] teamName, char type, char[] hash, char originalLength, String start, String end) {
        if(teamName.length==32) {
            this.teamName = teamName;
        }
        this.teamName = teamName;//todo delete
        if(Integer.parseInt(String.valueOf(type))>0&&Integer.parseInt(String.valueOf(type))<6) {
            this.type = type;
        }
        if(hash.length==40) {
            this.hash = hash;
        }
        this.hash = hash;//todo delete
        if(Character.isDigit(type)){
            this.originalLength = originalLength;
        }
        if(start.length()>0&&start.length()<256) {
            this.start = start;
        }
        if(end.length()>0&&end.length()<256) {
            this.end = end;
        }
    }

    public char[] getTeamName() {
        return teamName;
    }

    public char getType() {
        return type;
    }

    public char[] getHash() {
        return hash;
    }

    public char getOriginalLength() {
        return originalLength;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        byte [] data = bos.toByteArray();
        return data;
    }
    static public Message getMessage(byte [] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        Message message = (Message) is.readObject();
        is.close();
        return message;

    }
}
