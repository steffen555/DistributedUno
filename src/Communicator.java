public class Communicator {
    public void hostNetwork(int port) {
    }

    public void joinNetwork(String ip, int port) {
    }

    public void broadcastMove(Move move) {
    }

    /**
     * If the Player is LocalPlayer, the player is prompted for a move.
     * If the pPlayer is RemotePlayer, we wait for a message from that.
     */
    public Move receiveMove(Player playerInTurn) {
        return playerInTurn.receiveMove(this);
    }

    public Move receiveMoveOverNetwork(String ip, int port) {
        return null;
    }

    public Move receiveMoveFromLocalUser() {
        return null;
    }

    public void broadcastKey(Card card) {
    }
}
