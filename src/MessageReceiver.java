import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
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
    private List<Transmission> receiveQueue;
    private ServerSocket serverSocket;

    public MessageReceiver(int port) {
        receiveQueue = new ArrayList<>();
        this.port = port;
        initServerSocket();
    }

    public void run() {
        while (true) {
            Transmission t = doReceiveObject();
            if (t != null)
                addToQueue(t);
        }
    }

    private synchronized void addToQueue(Transmission t) {
        receiveQueue.add(t);
    }

    private void initServerSocket() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Transmission doReceiveObject() {
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

            // only ever allow Transmission objects, so we know who the sender is
            if (!(object instanceof Transmission))
                return null;

            Transmission t = (Transmission) object;

            // validate IP address of sender with that in 't'
            InetAddress senderIP = socket.getInetAddress();
            InetAddress peerInfoIP = InetAddress.getByName(t.getPeerInfo().getIp());
            if (senderIP.equals(InetAddress.getLocalHost()) || senderIP.equals(InetAddress.getByName("127.0.0.1"))) {
                // if it's sent from this machine, trust it; this lets us run
                // multiple clients on one machine without problems.
            }
            else if (!senderIP.equals(peerInfoIP)) {
                System.out.println("Got an impersonated object!");
                return null;
            }

            return t;
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

    // take an object which is one of the sought-after classes from the queue
    private synchronized Object takeFromQueue(List<Class> classes, PeerInfo peerInfo) {
        for (Transmission t : receiveQueue) {
            if (peerInfo != null && !(t.getPeerInfo().equals(peerInfo))) {
                continue; // came from wrong sender
            }

            for (Class c : classes) {
                if (c.isInstance(t.getObject())) {
                    receiveQueue.remove(t);
                    return t.getObject();
                }
            }
        }
        return null;
    }

    public Object receiveObject(Class c, boolean mayBlock) {
        return receiveObject(Arrays.asList(c), mayBlock, null);
    }

    public Object receiveObject(Class c) {
        return receiveObject(Arrays.asList(c));
    }

    public Object receiveObject(List<Class> classes, boolean mayBlock, PeerInfo peerInfo) {
        while (true) {
            Object o = takeFromQueue(classes, peerInfo);
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
        return receiveObject(classes, true, null);
    }

    // TODO: this should be specific to the player in turn, we should not accept joins from
    // anyone else until it's their turn.
    public JoinRequestMessage receiveJoinRequestMessage(boolean isMyTurn) {
        while (true) {

            // if it is my turn, we want to find any new join requests
            if (isMyTurn) {
                // if this returns null, that's okay, then we'll do a move ourselves
                return (JoinRequestMessage) receiveObject(JoinRequestMessage.class, false);
            }

            // otherwise, if it's someone else's turn, only take out forwarded join requests.
            else {
                for (Transmission t : receiveQueue) {
                    if (t.getObject() instanceof JoinRequestMessage) {
                        JoinRequestMessage m = (JoinRequestMessage) t.getObject();
                        if (m.isRelayed()) {
                            receiveQueue.remove(t);
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
        for (Transmission t : receiveQueue) {
            if (c.isInstance(t.getObject())) {
                return true;
            }
        }
        return false;
    }

    public Object receiveObjectFrom(Class c, PeerInfo peerInfo) {
        return receiveObject(Arrays.asList(c), true, peerInfo);
    }
}
