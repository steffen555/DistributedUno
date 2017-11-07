import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        DistributedCommunicationStrategy comm = null;
        if (args.length == 1) {
            int myPort = Integer.parseInt(args[0]);
            comm = new DistributedCommunicationStrategy(myPort);
            System.out.println("I am hosting on port " + myPort);
            try {
                comm.hostNetwork(2);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (args.length == 3) {
            int myPort = Integer.parseInt(args[0]);
            comm = new DistributedCommunicationStrategy(Integer.parseInt(args[0]));
            String otherIp = args[1];
            if (otherIp.equals("localhost"))
                otherIp = "127.0.0.1";
            int otherPort = Integer.parseInt(args[2]);
            try {
                comm.joinNetwork(otherIp, otherPort);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("I am joining " + otherIp + " with port " + otherPort);
        } else {
            System.out.println("Wrong number of parameters");
        }
//        CommunicationStrategy comm = new SimpleCommunicationStrategy(4);

        UnoGame game = new UnoGame(comm, new CryptoCardHandlingStrategy(comm));
        game.run();
    }
}
