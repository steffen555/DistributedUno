public class RemotePlayer implements Player {

    private String ip;
    private int port;

    public RemotePlayer(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    /**
     * Uses communicator to wait for a message over the network.
     * @param communicator
     * @return
     */
    @Override
    public Move receiveMove(Communicator communicator) {
        return communicator.receiveMoveOverNetwork(ip, port);
    }
}
