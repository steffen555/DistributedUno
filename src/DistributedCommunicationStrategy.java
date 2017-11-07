
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

    private ServerSocket serverSocket;
    private PeerInfo myInfo;
    private ArrayList<Player> players;
    private MoveValidator moveValidator;
    private List<Object> receiveQueue;

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
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myInfo = new PeerInfo(myIP(), port);
        players = new ArrayList<>();
        receiveQueue = new ArrayList<>();
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

    private Move doReceiveMove(Player player) {
        MoveMessage moveMessage = (MoveMessage) receiveObject(MoveMessage.class);
        return new Move(player, moveMessage.getMoveType(), moveMessage.getIndex());
    }

    private Object doReceiveObject() {
        Socket socket;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            return null;
        }
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            Object object;
            object = inputStream.readObject();
            outputStream.writeObject("Object received");
            System.out.println("This object was received: " + object);
            socket.close();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object receiveObject(Class c) {
        // first try to find the object in the queue
        for (Object o : receiveQueue) {
            if (c.isInstance(o)) {
                receiveQueue.remove(o);
                return o;
            }
        }

        // if it's not in the queue, keep receiving objects until we find it
        while (true) {
            Object o = doReceiveObject();
            if (c.isInstance(o)) {
                return o;
            } else {
                // put it in the queue for later if it's not a c
                receiveQueue.add(o);
            }
        }
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
    public Card.Color getColorFromPlayer(Player player) {
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
        public Card.Color receiveColor() {
            Card.Color color = getColorFromLocalUser();
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
        public Card.Color receiveColor() {
            return (Card.Color) receiveObject(Card.Color.class);
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

        // System.out.println("These are my peers" + peerInfos);
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
        // System.out.println("These are my peers: " + peerInfos);
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

    private Card.Color getColorFromLocalUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Which color should the card be? (red, green, blue, yellow)");
        String reply = scanner.next();
        switch (reply) {
            case "red":
            case "r":
                return Card.Color.RED;
            case "green":
            case "g":
                return Card.Color.GREEN;
            case "blue":
            case "b":
                return Card.Color.BLUE;
            case "yellow":
            case "y":
                return Card.Color.YELLOW;
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
//            System.out.println("Trying to send " + object);
            outputStream.writeObject(object);
//            System.out.println("I have sent this object: " + object);
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            String returnMessage = (String) inputStream.readObject();
            // System.out.println("The return message: " + returnMessage);
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void decryptCardWithKeysFromOtherPlayers(Player player, Card card) {
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
                card.decrypt(ck);
            }
        }
    }

    public void sendPlayersKeyForCardToOtherPlayers(Player player, Card card) {
        if (player instanceof LocalPlayer) {
            for (Player p1 : players) {
                if (p1 != player)
                    sendObjectToPlayer(p1, card.getMyKey());
            }
        } else if (player instanceof RemotePlayer) {
            CryptoKey ck = (CryptoKey) receiveObject(CryptoKey.class);
            card.decrypt(ck);
        }
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