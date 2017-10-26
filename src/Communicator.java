import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class Communicator{

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private int myPort = 0;


    public Communicator(int port) {
        myPort = port;
    }

    public void hostNetwork() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(myPort);
        socket = serverSocket.accept();
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        String message = (String) inputStream.readObject();
        outputStream.writeObject("I received the following message " + message);
        System.out.println("This message was received: " + message);
        socket.close();
    }

    public void joinNetwork(String ip, int port) throws IOException, ClassNotFoundException {
        socket = new Socket(ip, port);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Ok");
        String message = "Hello World!";
        outputStream.writeObject(message);
        System.out.println("The message " + message + " has been sent");

        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        String returnMessage = (String) inputStream.readObject();
        System.out.println("A return message has been read and this is it: " + returnMessage);
        socket.close();
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
        return receiveMoveFromLocalUser(playerInTurn);
    }

    public Move receiveMoveFromLocalUser(Player playerInTurn) {
        MoveType moveType = getMoveTypeFromUser();
        if (moveType.equals(MoveType.PLAY)) {
            int cardIndex = getCardFromUser();
        }
        return null;
    }

    public void broadcastKey(Card card) {
        try {
            socket = new Socket();
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            int key = ((EncryptedCard) card).getKey();
            outputStream.writeObject(key);
            System.out.println("I sent the key");
        } catch (IOException e) {
            e.printStackTrace();
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

    public Object receiveObject() {
        // TODO
        return null;
    }

    public void sendObject(Object o) {
        // TODO
    }

    public void broadcastObject(Object o) {
        // TODO
    }
}
