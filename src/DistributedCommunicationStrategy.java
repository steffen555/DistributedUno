
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class DistributedCommunicationStrategy implements CommunicationStrategy {

    private final MessageReceiver messageReceiver;
    private PeerInfo myInfo;
    private ArrayList<Player> players;
    private MoveValidator moveValidator;

    // TODO: handle the case when this fails better, e.g. if we have two IPs
    private String myIP() {
        Enumeration e;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            return null;
        }

        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (i.isSiteLocalAddress())
                    return i.getHostAddress();
            }
        }

        return null;
    }

    public DistributedCommunicationStrategy(int port) {
        System.out.println("My IP is: " + myIP());
        myInfo = new PeerInfo(myIP(), port);
        players = new ArrayList<>();

        messageReceiver = new MessageReceiver(port);
        messageReceiver.start();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Move getNextMoveFromPlayer(Player player) {
        return player.receiveMove();
    }

    public void sendObjectToPlayer(Player player, Serializable object) {
        sendObject(player.getPeerInfo(), object);
    }

    @Override
    public Object receiveObject(Class c) {
        return messageReceiver.receiveObject(c);
    }

    private Move doReceiveMove(Player player) {
        MoveMessage moveMessage = (MoveMessage) receiveObject(MoveMessage.class);
        return new Move(player, moveMessage.getMoveType(), moveMessage.getIndex());
    }


    public void broadcastObject(Serializable object) {
        for (Player player : players) {
            if (player instanceof RemotePlayer) {
                sendObject(player.getPeerInfo(), object);
            }
        }
    }

    private void broadcastMove(Move move) {
        for (Player player : players) {
            if (player instanceof RemotePlayer) {
                sendMove(player.getPeerInfo(), move);
            }
        }
    }

    public void setMoveValidator(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    @Override
    public CardColor getColorFromPlayer(Player player) {
        return player.receiveColor();
    }

    private class LocalPlayer extends Player {
        @Override
        public Move receiveMove() {
            Move move;
            do {
                move = receiveMoveFromLocalUser(this);
            } while (!moveValidator.isLegal(move));
            broadcastMove(move);
            return move;
        }

        @Override
        public CardColor receiveColor() {
            CardColor color = getColorFromLocalUser();
            broadcastObject(color);
            return color;
        }

        public String toString() {
            return "Local player";
        }
    }

    private class RemotePlayer extends Player {

        private PeerInfo peerInfo;

        public RemotePlayer(PeerInfo peerInfo) {
            this.peerInfo = peerInfo;
        }

        public Move receiveMove() {
            return doReceiveMove(this);
        }

        public PeerInfo getPeerInfo() {
            return peerInfo;
        }

        @Override
        public CardColor receiveColor() {
            return (CardColor) receiveObject(CardColor.class);
        }

        public String toString() {
            return "Remote player: " + peerInfo.toString();
        }
    }

    public void hostNetwork(int numberOfPlayers) throws IOException, ClassNotFoundException {
        players.add(new LocalPlayer());
        int counter = numberOfPlayers - 1;
        while (counter > 0) {
            PeerInfo peerInfo = (PeerInfo) receiveObject(PeerInfo.class);
            players.add(new RemotePlayer(peerInfo));
            counter--;
        }
        ArrayList<PeerInfo> peerInfos = new ArrayList<>();
        for (Player p : players)
            if (p instanceof LocalPlayer)
                peerInfos.add(myInfo);
            else
                peerInfos.add(p.getPeerInfo());
        broadcastObject(peerInfos);
    }

    public void joinNetwork(String ip, int port) throws IOException, ClassNotFoundException {
        PeerInfo hostInfo = new PeerInfo(ip, port);
        sendObject(hostInfo, myInfo);

        ArrayList<PeerInfo> peerInfos = (ArrayList<PeerInfo>) receiveObject(ArrayList.class);
        for (PeerInfo pi : peerInfos)
            if (pi.equals(myInfo))
                players.add(new LocalPlayer());
            else
                players.add(new RemotePlayer(pi));
    }

    private Move receiveMoveFromLocalUser(Player playerInTurn) {
        if (!moveValidator.legalMoveExists())
            return new Move(playerInTurn, MoveType.END_TURN, 0);
        MoveType moveType = getMoveTypeFromUser();
        int cardIndex = 0;
        if (moveType.equals(MoveType.PLAY))
            cardIndex = getCardFromUser();
        return new Move(playerInTurn, moveType, cardIndex);
    }

    private CardColor getColorFromLocalUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Which color should the card be? (red, green, blue, yellow)");
        String reply = scanner.next();
        switch (reply) {
            case "red":
            case "r":
                return CardColor.RED;
            case "green":
            case "g":
                return CardColor.GREEN;
            case "blue":
            case "b":
                return CardColor.BLUE;
            case "yellow":
            case "y":
                return CardColor.YELLOW;
            default:
                System.out.println("Failed to parse");
                return getColorFromLocalUser();
        }
    }

    private MoveType getMoveTypeFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("It's your turn. Would you like to draw, play a card or end your turn? (d/p/e): ");
        String reply = scanner.next();
        switch (reply) {
            case "d":
                return MoveType.DRAW;
            case "p":
                return MoveType.PLAY;
            case "e":
                return MoveType.END_TURN;
            default:
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

    private void sendMove(PeerInfo peerInfo, Move move) {
        sendObject(peerInfo, new MoveMessage(move.getType(), move.getCardIndex()));
    }

    private void sendObject(PeerInfo peerInfo, Serializable object) {
        try {
            Socket socket = new Socket(peerInfo.getIp(), peerInfo.getPort());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(object);
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            String returnMessage = (String) inputStream.readObject();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Card decryptCardWithKeysFromOtherPlayers(Player player, EncryptedCard card) {
        Card result = card;
        for (Player p : players) {
            if (p == player) {
                continue;
            } else if (p instanceof LocalPlayer) {
                for (Player p1 : players) {
                    if (p != p1) {
                        sendObjectToPlayer(p1, card.getMyKey());
                    }
                }
            } else if (p instanceof RemotePlayer) {
                CryptoKey ck = (CryptoKey) receiveObject(CryptoKey.class);
                result = ((EncryptedCard) result).decrypt(ck);
            }
        }
        return result;
    }

    public Card sendPlayersKeyForCardToOtherPlayers(Player player, Card card) {
        if (player instanceof LocalPlayer) {
            for (Player p1 : players) {
                if (p1 != player)
                    sendObjectToPlayer(p1, card.getMyKey());
            }
            return card;
        } else if (player instanceof RemotePlayer) {
            CryptoKey key = (CryptoKey) receiveObject(CryptoKey.class);
            return ((EncryptedCard) card).decrypt(key);
        }
        return null;
    }
}

class MoveMessage implements Serializable {
    private MoveType moveType;
    private int index;

    public MoveMessage(MoveType moveType, int index) {
        this.moveType = moveType;
        this.index = index;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public int getIndex() {
        return index;
    }
}