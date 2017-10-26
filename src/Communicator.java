
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Communicator{

    private ServerSocket serverSocket;
    private PeerInfo myInfo;
    private List<PeerInfo> peerInfos;


    public Communicator(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myInfo = new PeerInfo("localhost", port);
        peerInfos = new ArrayList<PeerInfo>();
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

        System.out.println("These are my peers" + peerInfos);
    }

    public void joinNetwork(String ip, int port) throws IOException, ClassNotFoundException {
        PeerInfo hostInfo = new PeerInfo(ip, port);
        sendObject(hostInfo, myInfo);

        peerInfos = (List<PeerInfo>) receiveObject();
        System.out.println("These are my peers: " + peerInfos);
    }

    /**
     * If the Player is LocalPlayer, the player is prompted for a move.
     * If the Player is RemotePlayer, we wait for a message from that.
     */
    public Move receiveMove(Player playerInTurn) {
        return playerInTurn.receiveMove(this);
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

    public Object receiveObject() throws IOException, ClassNotFoundException {
        Socket socket = serverSocket.accept();
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        Object object = inputStream.readObject();
        outputStream.writeObject("Object received");
        System.out.println("This object was received: " + object);
        socket.close();
        return object;
    }

    public void sendObject(PeerInfo peerInfo, Object object) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(peerInfo.getIp(), peerInfo.getPort());
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(object);
        System.out.println("I have sent this object: " + object);
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        String returnMessage = (String) inputStream.readObject();
        System.out.println("The return message: " + returnMessage);
        socket.close();
    }

    public void broadcastObject(Object object) {
        for (PeerInfo peerInfo: peerInfos) {
            if (!(peerInfo.equals(myInfo))) {
                try {
                    sendObject(peerInfo, object);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<Player>();
        for (PeerInfo peerInfo: peerInfos) {
            if (peerInfo.equals(myInfo)) {
                players.add(new LocalPlayer());
            } else {
                players.add(new RemotePlayer(peerInfo));
            }
        }
        return players;
    }
}
