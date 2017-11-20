import java.io.IOException;

public class MultiGame {

    public MultiGame() {

    }

    public void run(int numberOfPlayers) {
        if (numberOfPlayers < 2)
            System.out.println("There have to be 2 or more players");
        else {
            HostThread hostThread = new HostThread(numberOfPlayers);
            hostThread.start();
            for (int i = 1; i < numberOfPlayers; i++) {
                int myPort = 5000 + i;
                System.out.println("Joiner " + i + "'s port is: " + myPort);
                JoinThread joinThread = new JoinThread(myPort);
                joinThread.start();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            MultiGame multiGame = new MultiGame();
            multiGame.run(Integer.parseInt(args[0]));
        }
    }

    private class HostThread extends Thread {
        int numberOfPlayers;

        public HostThread(int numberOfPlayers) {
            this.numberOfPlayers = numberOfPlayers;
        }

        public void run() {
            DistributedCommunicationStrategy comm = new DistributedCommunicationStrategy(5000);
            try {
                comm.hostNetwork(numberOfPlayers);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            UnoGame hostingGame = new UnoGame(comm, new CryptoCardHandlingStrategy(comm));
            hostingGame.run();
        }
    }

    private class JoinThread extends Thread {
        int myPort;

        public JoinThread(int myPort) {
            this.myPort = myPort;
        }

        public void run() {
            DistributedCommunicationStrategy comm = new DistributedCommunicationStrategy(myPort);
            try {
                comm.joinNetwork("127.0.0.1", 5000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            UnoGame joiningGame = new UnoGame(comm, new CryptoCardHandlingStrategy(comm));
            joiningGame.run();
        }
    }
}


