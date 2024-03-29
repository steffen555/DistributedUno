import java.math.BigInteger;
import java.util.List;

public class DeckShufflingProtocol {

    private final ActionCardTarget actionTarget;
    private final CardHandlingStrategy chs;
    private CryptoKey k_i;
    private Deck deck;
    private CommunicationStrategy communicator;
    private List<Player> players;
    private int firstPlayerAfterLocal;
    private boolean firstPlayerIsLocal;
    private Logger logger;

    public DeckShufflingProtocol(ActionCardTarget actionTarget, CardHandlingStrategy chs,
                                 CommunicationStrategy communicator) {
        this.communicator = communicator;
        this.actionTarget = actionTarget;
        this.chs = chs;
        players = communicator.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPeerInfo() == null) {
                firstPlayerAfterLocal = (i + 1) % players.size();
                break;
            }
        }
        firstPlayerIsLocal = players.get(0).getPeerInfo() == null;

        logger = new Logger("DeckShufflingProtocol", "log.txt", Logger.DEBUG);
    }

    // first, run round 1, in which each player chooses a key K_i,
    // and uses it to encrypt every card in the deck, then passes on
    // the encrypted deck.
    private void doRound1() {
        k_i = CryptoScheme.generateKey();

        if (players.get(0).getPeerInfo() == null) { // If there is no info on where to send, it must be local.
            // the first player generates the deck
            deck = Deck.generateLargePlainDeck(actionTarget, (players.size() * Hand.CARDS_PER_HAND) / 60 + 1);
        } else {
            // other players receive the deck from a previous player
            List<CardRepresentation> reprs;
            reprs = (List<CardRepresentation>) communicator.receiveObject(List.class);
            deck = Deck.fromRepresentationList(reprs, actionTarget);
        }

        // the player encrypts the deck with their key.
        deck.encryptWithSingleKey(k_i);

        // then they shuffle it
        deck.shuffle();

        // then they pass it on to the next player.
        Player target = players.get(firstPlayerAfterLocal);
        logger.debug("Sending my deck to: " + target);
        communicator.sendObjectToPlayer(target, deck.asRepresentationList());
    }

    private void doRound2() {

        // receive the deck from the previous player
        List<CardRepresentation> reprs = (List<CardRepresentation>) communicator.receiveObject(List.class);
        deck = Deck.fromRepresentationList(reprs, actionTarget);

        // decrypt it under our key
        deck.decrypt(k_i);

        // encrypt every card with its own key
        deck.encryptWithMultipleKeys();

        // pass the deck on
        Player nextPlayer = players.get(firstPlayerAfterLocal);
        logger.debug("Sending newly encrypted deck to: " + nextPlayer);
        communicator.sendObjectToPlayer(nextPlayer, deck.asRepresentationList());
    }

    private void doRound3() {
        List<CardRepresentation> reprs = (List<CardRepresentation>) communicator.receiveObject(List.class);
        deck.updateCards(reprs);
        if (firstPlayerIsLocal) {
            communicator.broadcastObject(deck.asRepresentationList());
        }
    }

    public Deck makeShuffledDeck() {

        logger.debug("running makeShuffledDeck with these players: ");
        for (Player p : players)
            logger.debug(p.toString());

        // in round 1, each player encrypts and shuffles the deck
        logger.debug("Doing round 1");
        doRound1();

        // in round 2, each player decrypts the deck, then re-encrypts
        // every card under an individual key
        logger.debug("Doing round 2");
        doRound2();

        // in round 3, the first player receives the final deck,
        // and distributes it to every player
        logger.debug("Doing round 3");
        doRound3();

        logger.debug("Done with round 3");

        deck.decryptAllCardsWithMyKey();

        // every player now has the same deck, which we can return
        return deck;
    }
}
