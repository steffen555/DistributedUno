import java.math.BigInteger;
import java.util.List;

public class DeckShufflingProtocol {

    private CryptoKey k_i;
    private Deck deck;
    private CommunicationStrategy communicator;
    private List<Player> players;
    private int firstPlayerAfterLocal;
    private boolean firstPlayerIsLocal;

    public DeckShufflingProtocol(CommunicationStrategy communicator) {
        this.communicator = communicator;
        players = communicator.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(0).getPeerInfo() == null) {
                firstPlayerAfterLocal = (i + 1) % players.size();
                break;
            }
        }
        firstPlayerIsLocal = players.get(0).getPeerInfo() == null;
    }

    // first, run round 1, in which each player chooses a key K_i,
    // and uses it to encrypt every card in the deck, then passes on
    // the encrypted deck.
    private void doRound1() {
        k_i = CryptoScheme.generateKey();

        if (players.get(0).getPeerInfo() == null) { // If there is no info on where to send, it must be local.
            // the first player generates the deck
            deck = Deck.generatePlainDeck();
        } else {
            // other players receive the deck from a previous player
            List<BigInteger> intList;
            intList = (List<BigInteger>) communicator.receiveObject();
            deck = Deck.fromIntList(intList);
        }

        // the player encrypts the deck with their key.
        deck.encryptWithSingleKey(k_i);

        // then they shuffle it
        deck.shuffle();

        // then they pass it on to the next player.
        communicator.sendObjectToPlayer(players.get(firstPlayerAfterLocal), deck.asIntList());
    }

    private void doRound2() {

        // receive the deck from the previous player
        List<BigInteger> intList = (List<BigInteger>) communicator.receiveObject();
        deck = Deck.fromIntList(intList);

        // decrypt it under our key
        deck.decrypt(k_i);

        // encrypt every card with its own key
        deck.encryptWithMultipleKeys();

        // pass the deck on
        Player nextPlayer = players.get(firstPlayerAfterLocal);
        communicator.sendObjectToPlayer(nextPlayer, deck.asIntList());
    }

    private void doRound3() {
        List<BigInteger> intList = (List<BigInteger>) communicator.receiveObject();
        deck.updateCards(intList);
        if (firstPlayerIsLocal) {
            communicator.broadcastObject(deck.asIntList());
        }
    }

    public Deck makeShuffledDeck() {

        // in round 1, each player encrypts and shuffles the deck
        doRound1();

        // in round 2, each player decrypts the deck, then re-encrypts
        // every card under an individual key
        doRound2();

        // in round 3, the first player receives the final deck,
        // and distributes it to every player
        doRound3();

        deck.decryptAllCardsWithMyKey();

        // every player now has the same deck, which we can return
        return deck;
    }
}
