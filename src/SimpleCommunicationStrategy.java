import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimpleCommunicationStrategy implements CommunicationStrategy {

    private List<Player> players;

    public SimpleCommunicationStrategy(int numberOfPlayers) {
        players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player() {
                @Override
                Move receiveMove() {
                    throw new NotImplementedException();
                }

                @Override
                public CardColor receiveColor() {
                    throw new NotImplementedException();
                }
            });
        }
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public Move getNextMoveFromPlayer(Player player) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Would you like to draw or play a card? (d/p): ");
        String reply = scanner.next();
        switch (reply) {
            case "d":
                return new Move(player, MoveType.DRAW, 0);
            case "p":
                System.out.print("Which card from your hand would you like to play?: ");
                reply = scanner.next();
                try {
                    return new Move(player, MoveType.PLAY, Integer.parseInt(reply));
                } catch (NumberFormatException nfe) {
                    System.out.println("Failed to parse");

                }
                break;
            default:
                System.out.println("Failed to parse");
                break;
        }
        return null;
    }

    @Override
    public void sendObjectToPlayer(Player player, Serializable object) {

    }

    @Override
    public Object receiveObject(Class c) {
        return null;
    }

    @Override
    public void broadcastObject(Serializable object) {

    }

    @Override
    public Card decryptCardWithKeysFromOtherPlayers(Player player, EncryptedCard card) {
        return null;
    }

    @Override
    public Card sendPlayersKeyForCardToOtherPlayers(Player player, Card card) {

        return null;
    }

    @Override
    public void setMoveValidator(MoveValidator v) {

    }

    @Override
    public CardColor getColorFromPlayer(Player player) {
        return null;
    }

    @Override
    public void handleJoiningPlayers(Player currentPlayer, GameStateSupplier game) {
        // TODO
    }

    @Override
    public void setPlayers(List<PeerInfo> peerInfos) {
        // TODO
    }

    @Override
    public void addSelfToPlayersList() {
        // TODO
    }

    @Override
    public PeerInfo getLocalPeerInfo() {
        return null;
    }

    @Override
    public Player getLocalPlayer() {
        return null;
    }
}
