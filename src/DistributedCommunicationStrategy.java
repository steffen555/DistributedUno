
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DistributedCommunicationStrategy implements CommunicationStrategy {

    private ServerSocket serverSocket;
    private PeerInfo myInfo;
    private ArrayList<Player> players;

    public DistributedCommunicationStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myInfo = new PeerInfo("localhost", port);
        players = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Move getNextMoveFromPlayer(Player player) {
        return player.receiveMove(this);
    }

    public void sendObjectToPlayer(Player player, Serializable object) {
        sendObject(player.getPeerInfo(), object);
    }

    private Move receiveMove(Player player) {
        MoveMessage moveMessage = (MoveMessage) receiveObject();
        return new Move(player, moveMessage.getMoveType(), moveMessage.getIndex());
    }

    public Object receiveObject() {
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

    private class LocalPlayer extends Player {
        @Override
        public Move receiveMove(DistributedCommunicationStrategy communicator) {
            return communicator.receiveMoveFromLocalUser(this);
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

        public Move receiveMove(DistributedCommunicationStrategy communicator) {
            return communicator.receiveMove(this);
        }

        public PeerInfo getPeerInfo() {
            return peerInfo;
        }

        public String toString() {
            return "Remote player: " + peerInfo.toString();
        }
    }

    public void hostNetwork(int numberOfPlayers) throws IOException, ClassNotFoundException {
        players.add(new LocalPlayer());
        int counter = numberOfPlayers - 1;
        while (counter > 0) {
            PeerInfo peerInfo = (PeerInfo) receiveObject();
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

        ArrayList<PeerInfo> peerInfos = (ArrayList<PeerInfo>) receiveObject();
        for (PeerInfo pi : peerInfos)
            if (pi.equals(myInfo))
                players.add(new LocalPlayer());
            else
                players.add(new RemotePlayer(pi));
        // System.out.println("These are my peers: " + peerInfos);
    }

    private Move receiveMoveFromLocalUser(Player playerInTurn) {
        MoveType moveType = getMoveTypeFromUser();
        int cardIndex = 0;
        if (moveType.equals(MoveType.PLAY))
            cardIndex = getCardFromUser();
        Move move = new Move(playerInTurn, moveType, cardIndex);
        broadcastMove(move);
        return move;
    }

    private MoveType getMoveTypeFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("It's your turn. Would you like to draw or play a card? (d/p): ");
        String reply = scanner.next();
        switch (reply) {
            case "d":
                return MoveType.DRAW;
            case "p":
                return MoveType.PLAY;
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
                CryptoKey ck = (CryptoKey) receiveObject();
                card.decrypt(ck);
            }
        }
    }

    public void sendPlayersKeyForCardToOtherPlayers(Player player, Card card) {
        for (Player p : players) {
            if (p == player) {
                if (p instanceof LocalPlayer) {
                    for (Player p1 : players) {
                        if (p1 != p)
                            sendObjectToPlayer(p1, card.getMyKey());
                    }
                } else if (p instanceof RemotePlayer) {
                    CryptoKey ck = (CryptoKey) receiveObject();
                    card.decrypt(ck);
                }
            }
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