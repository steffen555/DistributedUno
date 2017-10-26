import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Communicator comm = new Communicator();
        if (host())
            comm.hostNetwork(1337);
        else
            comm.joinNetwork("localhost", 1337);
        UnoGame game = new UnoGame(comm);
        game.run();
    }

    private static boolean host() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Would you like to host? (y/n): ");
        String reply = scanner.next();
        if (reply.equals("y"))
            return true;
        else if (reply.equals("n"))
            return false;
        else {
            System.out.println("Failed to parse");
            return host();
        }
    }
}
