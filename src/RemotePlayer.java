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
        return (Move) communicator.receiveObject();
    }

    @Override
    public PeerInfo getPeerInfo() {
        return peerInfo;
    }
}
