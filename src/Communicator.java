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

    public Move receiveMove(Player playerInTurn) {
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
    }

    public void receiveKey() {
    }
}
