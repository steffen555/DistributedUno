import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
    This class is responsible for receiving objects from the network.
    It runs in a separate thread to ensure that messages can always be received,
    including requests from new players to join the game.
*/
public class MessageReceiver extends Thread {
    private final int port;
    private List<Object> receiveQueue;
    private ServerSocket serverSocket;

    public MessageReceiver(int port) {
        receiveQueue = new ArrayList<>();
        this.port = port;
        initServerSocket();
    }

    public void run() {
        while (true) {
            Object o = doReceiveObject();
            addToQueue(o);
        }
    }

    private synchronized void addToQueue(Object o) {
        receiveQueue.add(o);
    }

    private void initServerSocket() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            outputStream.writeObject("Object received"); // otherwise we get an EOFError
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

    private synchronized Object takeFromQueue(Class c) {
        for (Object o : receiveQueue) {
            if (c.isInstance(o)) {
                receiveQueue.remove(o);
                return o;
            }
        }
        return null;
    }

    public Object receiveObject(Class c) {
        while (true) {
            Object o = takeFromQueue(c);
            if (o != null)
                return o;

            // we don't have this type of object in the queue yet, so sleep before retrying
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
