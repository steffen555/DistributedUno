
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DistributedCommunicationStrategy implements CommunicationStrategy {

    private ServerSocket serverSocket;
    private PeerInfo myInfo;
    private List<PeerInfo> peerInfos;

    public DistributedCommunicationStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myInfo = new PeerInfo("localhost", port);
        peerInfos = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (PeerInfo peerInfo : peerInfos) {
            if (peerInfo.equals(myInfo)) {
                players.add(new LocalPlayer());
            } else {
                players.add(new RemotePlayer(peerInfo));
            }
        }
        return players;
    }

    public Move getNextMoveFromPlayer(Player player) {
        return player.receiveMove(this);
    }

    public void sendObjectToPlayer(Player player, Object object) {
        sendObject(player.getPeerInfo(), object);
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
            // System.out.println("This object was received: " + object);
            socket.close();
            return object;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void broadcastObject(Object object) {
        for (PeerInfo peerInfo : peerInfos) {
            if (!(peerInfo.equals(myInfo))) {
                sendObject(peerInfo, object);
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
            return (Move) communicator.receiveObject();
        }

        public PeerInfo getPeerInfo() {
            return peerInfo;
        }

        public String toString() {
            return "Remote player: " + peerInfo.toString();
        }
    }

    public void hostNetwork(int numberOfPlayers) throws IOException, ClassNotFoundException {
        peerInfos.add(myInfo);
        int counter = numberOfPlayers - 1;
        while (counter > 0) {
            PeerInfo peerInfo = (PeerInfo) receiveObject();
            peerInfos.add(peerInfo);
            counter--;
        }

        broadcastObject(peerInfos);

        // System.out.println("These are my peers" + peerInfos);
    }

    public void joinNetwork(String ip, int port) throws IOException, ClassNotFoundException {
        PeerInfo hostInfo = new PeerInfo(ip, port);
        sendObject(hostInfo, myInfo);

        peerInfos = (List<PeerInfo>) receiveObject();
        // System.out.println("These are my peers: " + peerInfos);
    }

    private Move receiveMoveFromLocalUser(Player playerInTurn) {
        MoveType moveType = getMoveTypeFromUser();
        int cardIndex = 0;
        if (moveType.equals(MoveType.PLAY))
            cardIndex = getCardFromUser();
        Move move = new Move(playerInTurn, moveType, cardIndex);
        broadcastObject(move);
        return move;
    }

    private MoveType getMoveTypeFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Would you like to draw or play a card? (d/p): ");
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

    private void sendObject(PeerInfo peerInfo, Object object) {
        try {
            Socket socket = new Socket(peerInfo.getIp(), peerInfo.getPort());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(object);
            // System.out.println("I have sent this object: " + object);
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            String returnMessage = (String) inputStream.readObject();
            // System.out.println("The return message: " + returnMessage);
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
