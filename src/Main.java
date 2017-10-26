import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        boolean host = false;
        Communicator comm = null;
        if(args.length == 1) {
            host = true;
            int myPort = Integer.parseInt(args[0]);
            comm = new Communicator(myPort);
            try {
                comm.hostNetwork();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("I am hosting on port " + myPort);
        } else if (args.length == 3){
            host = false;
            int myPort = Integer.parseInt(args[0]);
            comm = new Communicator(Integer.parseInt(args[0]));
            String otherIp = args[1];
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


//        UnoGame game = new UnoGame(comm);
//        game.run();
    }
}
