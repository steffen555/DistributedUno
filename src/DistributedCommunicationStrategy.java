
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DistributedCommunicationStrategy implements CommunicationStrategy {

    private final MessageReceiver messageReceiver;
    private final Logger logger;
    private LocalPlayer localPlayer;
    private PeerInfo myInfo;
    private ArrayList<Player> players;
    private MoveValidator moveValidator;
    private boolean autoplay = false;
    private int numSent;
    private int numBroadcast;

    // TODO: handle the case when this fails better, e.g. if we have two IPs
    private String myIP() {
        Enumeration e;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            return null;
        }

        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (i.isSiteLocalAddress())
                    return i.getHostAddress();
            }
        }

        return null;
    }

    public DistributedCommunicationStrategy(int port) {
        System.out.println("My IP is: " + myIP());
        myInfo = new PeerInfo(myIP(), port, getStringFromUser("What is your name?"));
        players = new ArrayList<>();

        numBroadcast = 0;
        numSent = 0;

        localPlayer = new LocalPlayer(myInfo.getName());

        messageReceiver = new MessageReceiver(port);
        messageReceiver.start();

        logger = new Logger("DistributedCommunicationStrategy",
                                    "log.txt", Logger.DEBUG);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Move getNextMoveFromPlayer(Player player) {
        return player.receiveMove();
    }

    public void sendObjectToPlayer(Player player, Serializable object) {
        sendObject(player.getPeerInfo(), object);
    }

    @Override
    public Object receiveObject(Class c) {
        return messageReceiver.receiveObject(c);
    }

    private Move doReceiveMove(Player player) {
        MoveMessage moveMessage = (MoveMessage) receiveObject(MoveMessage.class);
        return new Move(player, moveMessage.getMoveType(), moveMessage.getIndex(), moveMessage.getUno());
    }


    public void broadcastObject(Serializable object) {
        numBroadcast++;
        for (Player player : players) {
            if (player instanceof RemotePlayer) {
                sendObject(player.getPeerInfo(), object);
            }
        }
    }

    private void broadcastMove(Move move) {
        numBroadcast++;
        for (Player player : players) {
            if (player instanceof RemotePlayer) {
                sendMove(player.getPeerInfo(), move);
            }
        }
    }

    public void setMoveValidator(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    @Override
    public CardColor getColorFromPlayer(Player player) {
        return player.receiveColor();
    }

    // continuously receive a JoinRequestMessage until we see a Move, then return it.
    @Override
    public void handleJoiningPlayers(Player currentPlayer, GameStateSupplier gameStateSupplier) {
        boolean isMyTurn = currentPlayer instanceof LocalPlayer;

        JoinRequestMessage m;
        while (true) {
            m = messageReceiver.receiveJoinRequestMessage(isMyTurn);
            if (m == null)
                break;

            logger.info("Handling new join request message: " + m);
            handleJoinRequest(m, gameStateSupplier);
            logger.info("Handled the message " + m);
        }
    }

    @Override
    public void setPlayers(List<PeerInfo> peerInfos) {
        for (PeerInfo pi : peerInfos)
            if (pi.equals(myInfo))
                players.add(getLocalPlayer());
            else
                players.add(new RemotePlayer(pi));
    }

    @Override
    public void addSelfToPlayersList() {
        addToPlayersList(getLocalPlayer());
    }

    @Override
    public PeerInfo getLocalPeerInfo() {
        return myInfo;
    }

    @Override
    public Player getLocalPlayer() {
        return localPlayer;
    }

    @Override
    public void indicateFinished() {
        // the game is finished, so log the number of communications
        logger.info("Number of messages sent: " + numSent);
        logger.info("Number of messages broadcast: " + numBroadcast);
    }

    private void handleJoinRequest(JoinRequestMessage m, GameStateSupplier game) {
        Player newPlayer = new RemotePlayer(m.getPeerInfo());
        logger.debug("This player is joining: " + newPlayer);
        if (!m.isRelayed()) {
            logger.debug("Got a non-relayed join request");

            // the new player joined us directly, so notify everyone else
            m.setRelayed();
            broadcastObject(m);

            // tell the player that the game is in progress
            sendObject(m.getPeerInfo(), new GameInProgressMessage());

            // also send the player the game state
            sendObject(m.getPeerInfo(), game.getState());
        }
        else {
            logger.debug("Got a relayed join request");
        }

        // add the player to the players list.
        addToPlayersList(newPlayer);

        // cooperate with the others to reset the deck
        logger.debug("Initializing new deck");
        game.initializeNewDeck();
        logger.debug("Initialized new deck");

        // let the new player draw a hand
        for (int i = 0; i < Hand.CARDS_PER_HAND; i++) {
            logger.debug("Helping the player draw a card");
            game.getCardHandlingStrategy().drawCardFromDeckForPlayer(newPlayer);
        }
    }

    private void addToPlayersList(Player player) {
        players.add(player);
    }

    public String getStringFromUser(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(message);
        return scanner.nextLine();
    }

    private class LocalPlayer extends Player {

        public LocalPlayer(String name) {
            super(name);
        }

        @Override
        public Move receiveMove() {
            Move move;
            lastAttemptedMove = 0;
            do {
                move = receiveMoveFromLocalUser(this);
            } while (!moveValidator.isLegal(move));
            broadcastMove(move);
            return move;
        }

        @Override
        public CardColor receiveColor() {
            CardColor color = getColorFromLocalUser();
            broadcastObject(color);
            return color;
        }

        public String toString() {
            return getName() + " (local)";
        }

        public boolean equals(Object o) {
            if (o instanceof LocalPlayer)
                return true;

            return false;
        }

        public int hashCode() {
            return toString().hashCode();
        }
    }

    private class RemotePlayer extends Player {

        private PeerInfo peerInfo;

        public RemotePlayer(PeerInfo peerInfo) {
            super(peerInfo.getName());
            this.peerInfo = peerInfo;
        }

        public Move receiveMove() {
            return doReceiveMove(this);
        }

        public PeerInfo getPeerInfo() {
            return peerInfo;
        }

        @Override
        public CardColor receiveColor() {
            return (CardColor) receiveObject(CardColor.class);
        }

        public String toString() {
            return getName() + " (remote)";
        }

        public boolean equals(Object o) {
            if (o instanceof RemotePlayer) {
                return peerInfo.equals(((RemotePlayer) o).getPeerInfo());
            }

            return false;
        }

        public int hashCode() {
            return this.toString().hashCode();
        }
    }

    public void hostNetwork(int numberOfPlayers) throws IOException, ClassNotFoundException {
        players.add(getLocalPlayer());

        // first wait for join requests from each player
        int counter = numberOfPlayers - 1;
        while (counter > 0) {
            // get the join request
            JoinRequestMessage jrm = (JoinRequestMessage) receiveObject(JoinRequestMessage.class);
            PeerInfo peerInfo = jrm.getPeerInfo();

            // tell them the game is being created (it's new)
            sendObject(peerInfo, new GameBeingCreatedMessage());

            // add them to the list of players
            players.add(new RemotePlayer(peerInfo));
            counter--;
        }

        // then broadcast the list of players so everyone agrees on it (and its order!)
        ArrayList<PeerInfo> peerInfos = new ArrayList<>();
        for (Player p : players) {
            if (p instanceof LocalPlayer)
                peerInfos.add(myInfo);
            else
                peerInfos.add(p.getPeerInfo());
        }
        broadcastObject(peerInfos);
    }

    public GameState joinNetwork(String ip, int port) throws IOException, ClassNotFoundException {
        PeerInfo hostInfo = new PeerInfo(ip, port, "dav");

        // tell the host we want to join
        JoinRequestMessage jrm = new JoinRequestMessage(myInfo);
        sendObject(hostInfo, jrm);

        // receive a message back which tells us if the game is running or not
        Object message = messageReceiver.receiveObject(
                Arrays.asList(GameInProgressMessage.class, GameBeingCreatedMessage.class));

        if (message instanceof GameInProgressMessage)
            return joinGameInProgress();
        else if (message instanceof GameBeingCreatedMessage)
            joinGameBeingCreated();
        else
            System.out.println("Unhandled message type");
        return null;
    }

    private GameState joinGameInProgress() {
        System.out.println("The game is in progress! Joining it.");

        // receive the game state from the player we contacted
        return (GameState) receiveObject(GameState.class);
    }

    private void joinGameBeingCreated() {
        // receive a list of all players from the host
        ArrayList<PeerInfo> peerInfos = (ArrayList<PeerInfo>) receiveObject(ArrayList.class);
        setPlayers(peerInfos);
    }

    private int lastAttemptedMove = 0;

    private Move receiveMoveFromLocalUser(Player playerInTurn) {
        if (!moveValidator.legalMoveExists())
            return new Move(playerInTurn, MoveType.END_TURN, 0);
        MoveType moveType = null;
        while (moveType == null) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("It's your turn!");
            System.out.print("Write d to draw, a number to play a card, or e to end your turn: ");
            System.out.flush();

            String reply;
            if (autoplay) {
                reply = "" + (lastAttemptedMove++) + "uno";
                if (lastAttemptedMove > 50) //TODO: remove magic constant
                    reply = "d";
            } else
                reply = scanner.nextLine();

            boolean uno;
            Matcher matcher = Pattern.compile("(.*)uno.*", Pattern.CASE_INSENSITIVE).matcher(reply);
            uno = matcher.matches();
            if (uno) {
                reply = matcher.group(1);
                System.out.println("UNO!!!");
            }
            switch (reply) {
                case "a":
                    autoplay = true;
                    return receiveMoveFromLocalUser(playerInTurn);
                case "d":
                    moveType = MoveType.DRAW;
                    break;
                case "e":
                    moveType = MoveType.END_TURN;
                    break;
                default:
                    Matcher matcher2 = Pattern.compile("([0-9]+).*?", Pattern.CASE_INSENSITIVE).matcher(reply);
                    if (matcher2.matches())
                        return new Move(playerInTurn, MoveType.PLAY, Integer.parseInt(matcher2.group(1)), uno);
                    System.out.println("Failed to parse");
            }
        }
        int cardIndex = 0;
        return new Move(playerInTurn, moveType, cardIndex);
    }

    private CardColor getColorFromLocalUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Which color should the card be? (red, green, blue, yellow)");
        String reply;
        if (autoplay)
            reply = new String[]{"r", "g", "b", "y"}[(int) Math.random() * 4];
        else
            reply = scanner.next();
        switch (reply) {
            case "red":
            case "r":
                return CardColor.RED;
            case "green":
            case "g":
                return CardColor.GREEN;
            case "blue":
            case "b":
                return CardColor.BLUE;
            case "yellow":
            case "y":
                return CardColor.YELLOW;
            default:
                System.out.println("Failed to parse");
                return getColorFromLocalUser();
        }
    }

    private int getCardFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Which card from your hand would you like to play?: ");
        String reply = scanner.next();
        try {
            return Integer.parseInt(reply);
        } catch (NumberFormatException nfe) {
            System.out.println("Failed to parse");
            return getCardFromUser();
        }
    }

    private void sendMove(PeerInfo peerInfo, Move move) {
        sendObject(peerInfo, new MoveMessage(move.getType(), move.getCardIndex(), move.saidUno()));
    }

    private void sendObject(PeerInfo peerInfo, Serializable object) {
        numSent++;
        try {
            Socket socket = new Socket(peerInfo.getIp(), peerInfo.getPort());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(object);

            // we have to open this to avoid an exception being raised on the other side,
            // even though we don't use it.

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            socket.close();
        } catch (IOException /*| ClassNotFoundException*/ e) {
            e.printStackTrace();
        }
    }

    public Card decryptCardWithKeysFromOtherPlayers(Player player, EncryptedCard card) {
        Card result = card;
        for (Player p : players) {
            if (p.equals(player)) {
                continue;
            } else if (p instanceof LocalPlayer) {
                broadcastObject(card.getMyKey());
            } else if (p instanceof RemotePlayer) {
                logger.debug("Receiving a key from: " + p);
                CryptoKey ck = (CryptoKey) receiveObject(CryptoKey.class);
                logger.debug("Got the key: " + ck);
                result = ((EncryptedCard) result).decrypt(ck);
            }
        }
        return result;
    }

    public Card sendPlayersKeyForCardToOtherPlayers(Player player, Card card) {
        logger.debug("Inside sendPlayersKeyForCardToOtherPlayers");
        if (player instanceof LocalPlayer) {
            for (Player p1 : players) {
                if (p1 != player) {
                    logger.debug("Sending my key to: " + p1);
                    sendObjectToPlayer(p1, card.getMyKey());
                }
            }
            return card;
        } else if (player instanceof RemotePlayer) {
            logger.debug("Receiving from: " + player);
            CryptoKey key = (CryptoKey) receiveObject(CryptoKey.class);
            logger.debug("Got key: " + key);
            return ((EncryptedCard) card).decrypt(key);
        }
        return null;
    }
}

class MoveMessage implements Serializable {
    private MoveType moveType;
    private int index;
    private boolean uno;

    public MoveMessage(MoveType moveType, int index, boolean uno) {
        this.moveType = moveType;
        this.index = index;
        this.uno = uno;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public int getIndex() {
        return index;
    }

    public boolean getUno() {
        return uno;
    }
}

class JoinRequestMessage implements Serializable {
    private final PeerInfo peerInfo;
    private boolean relayed;

    public JoinRequestMessage(PeerInfo p) {
        peerInfo = p;
        relayed = false;
    }

    public PeerInfo getPeerInfo() {
        return peerInfo;
    }

    public boolean isRelayed() {
        return relayed;
    }

    public void setRelayed() {
        relayed = true;
    }
}

class GameInProgressMessage implements Serializable {
}

class GameBeingCreatedMessage implements Serializable {
}
