import java.io.Serializable;

public class PeerInfo implements Serializable{
    private String ip;
    private int port;
    private String name;

    public PeerInfo(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "ip address: " + this.ip + ", port: " + this.port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PeerInfo) {
            return ((PeerInfo) obj).getIp().equals(this.ip) && (((PeerInfo) obj).getPort() == this.port);
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }
}
