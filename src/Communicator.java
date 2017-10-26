import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Communicator {
    private List<Player> players;

    public Communicator() {
        // TODO: this is a mock-up, fix it
        players = new ArrayList<Player>();
        players.add(new LocalPlayer());
        players.add(new RemotePlayer("1.2.3.4", 1337));
    }

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

    public Move receiveMoveOverNetwork(String ip, int port, Player playerInTurn) {
        return receiveMoveFromLocalUser(playerInTurn); // TODO fixme
    }

    public Move receiveMoveFromLocalUser(Player playerInTurn) {
        MoveType moveType = getMoveTypeFromUser();
        int cardIndex = 0;
        if (moveType.equals(MoveType.PLAY))
            cardIndex = getCardFromUser();

        return new Move(moveType, cardIndex);
    }

    private static MoveType getMoveTypeFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Would you like to draw or play a card? (d/p): ");
        String reply = scanner.next();
        if (reply.equals("d"))
            return MoveType.DRAW;
        else if (reply.equals("p"))
            return MoveType.PLAY;
        else {
            System.out.println("Failed to parse");
            return getMoveTypeFromUser();
        }
    }

    private int getCardFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Which card from your hand would you like to play?: ");
        String reply = scanner.next();
        try {
            return Integer.parseInt(reply);
        } catch (NumberFormatException nfe) {
            System.out.println("Failed to parse");
            return getCardFromUser();
        }
    }

    public void broadcastKey(int card) {
    }

    // TODO: we should really put a 'Player' in here and ensure that only they may send to us for security.
    public Object receiveObject() {
        // TODO
        return null;
    }

    public void sendObject(Player p, Object o) {
        // TODO
    }

    public void broadcastObject(Object o) {
        // TODO
    }

    public List<Player> getPlayers() {
        return players;
    }
}
