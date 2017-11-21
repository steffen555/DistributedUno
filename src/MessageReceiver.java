import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
            if (o != null)
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
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // take an object which is one of the saught-after classes from the queue
    private synchronized Object takeFromQueue(List<Class> classes) {
        for (Object o : receiveQueue) {
            for (Class c : classes) {
                if (c.isInstance(o)) {
                    receiveQueue.remove(o);
                    return o;
                }
            }
        }
        return null;
    }

    public Object receiveObject(Class c, boolean mayBlock) {
        return receiveObject(Arrays.asList(c), mayBlock);
    }

    public Object receiveObject(Class c) {
        return receiveObject(Arrays.asList(c));
    }

    public Object receiveObject(List<Class> classes, boolean mayBlock) {
        while (true) {
            Object o = takeFromQueue(classes);
            if (o != null)
                return o;

            if (!mayBlock)
                return null;

            // we don't have this type of object in the queue yet, so sleep before retrying
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Object receiveObject(List<Class> classes) {
        return receiveObject(classes, true);
    }

    public JoinRequestMessage receiveJoinRequestMessage(boolean isMyTurn) {
        while (true) {

            // if it is my turn, we want to find any new join requests
            if (isMyTurn) {
                // if this returns null, that's okay, then we'll do a move ourselves
                return (JoinRequestMessage) receiveObject(JoinRequestMessage.class, false);
            }

            // otherwise, if it's someone else's turn, only take out forwarded join requests.
            else {
                for (Object o : receiveQueue) {
                    if (o instanceof JoinRequestMessage) {
                        JoinRequestMessage m = (JoinRequestMessage) o;
                        if (m.isRelayed()) {
                            receiveQueue.remove(o);
                            return m;
                        }
                    }
                }
            }

            // if there is a Move in the queue, return null, because then
            // no joins can occur until next turn.
            if (queueContains(MoveMessage.class))
                return null;

            // Otherwise, wait until one of those happens.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    private boolean queueContains(Class c) {
        for (Object o : receiveQueue) {
            if (c.isInstance(o)) {
                return true;
            }
        }
        return false;
    }
}
