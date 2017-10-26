import java.io.IOException;

public class RemotePlayer extends Player {

    private PeerInfo peerInfo;

    public RemotePlayer(PeerInfo peerInfo){
        this.peerInfo = peerInfo;
    }

    /**
     * Uses communicator to wait for a message over the network.
     * @param communicator
     * @return
     */
    @Override
    public Move receiveMove(Communicator communicator) {
        try {
            return (Move) communicator.receiveObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PeerInfo getPeerInfo() {
        return peerInfo;
    }
}
