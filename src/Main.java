public class Main {
    private static boolean host = false;
    public static void main(String[] args) {
        Communicator comm = new Communicator();
        if(host)
            comm.hostNetwork(1337);
        else
            comm.joinNetwork("localhost", 1337);
        UnoGame game = new UnoGame(comm);
        game.run();
    }
}
