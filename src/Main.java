import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.out.println("Wrong number of parameters");
            return;
        }

        GameState state = null;
        int myPort = Integer.parseInt(args[0]);
        int numberOfPlayers = 2;
        DistributedCommunicationStrategy comm = new DistributedCommunicationStrategy(myPort);
        CardHandlingStrategy chs = new CryptoCardHandlingStrategy(comm);
        UnoGame game = new UnoGame(comm, chs);

        if (args.length == 2)
            numberOfPlayers = Integer.parseInt(args[1]);
        if(args.length == 1 || args.length == 2) {
            System.out.println("I am hosting on port " + myPort);
            try {
                comm.hostNetwork(numberOfPlayers);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String otherIp = args[1];
            if (otherIp.equals("localhost"))
                otherIp = "127.0.0.1";
            int otherPort = Integer.parseInt(args[2]);
            System.out.println("I am joining " + otherIp + " with port " + otherPort);

            try {
                state = comm.joinNetwork(otherIp, otherPort);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("The state is: " + state );
        }

        if (state == null) {
            // it's a fresh game, so just run it
            game.run();
        } else {
            // we're joining an existing game
            game.join(state);
        }
    }
}
