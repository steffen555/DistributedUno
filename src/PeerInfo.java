import java.io.Serializable;

public class PeerInfo implements Serializable{
    private String ip;
    private int port;

    public PeerInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
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
}
