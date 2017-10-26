import java.util.Scanner;

public class Main {
    private static boolean host = true;

    public static void main(String[] args) {
        Communicator comm = new Communicator();
        // create a scanner so we can read the command-line input
        Scanner scanner = new Scanner(System.in);

        //  prompt for the user's name
        System.out.print("Would you like to host? (y/n): ");

        // get their input as a String
        boolean host;
        String reply = scanner.next();
        if (reply.equals("y"))
            host = true;
        else if (reply.equals("n"))
            host = false;
        else {
            System.out.println("Not understood, goodbye!");
            return;
        }
        if (host)
            comm.hostNetwork(1337);
        else
            comm.joinNetwork("localhost", 1337);
        UnoGame game = new UnoGame(comm);
        game.run();
    }
}
