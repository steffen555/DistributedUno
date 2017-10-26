public class DeckShufflingProtocol {

    private CryptoKey k_i;
    private Deck deck;
    private Communicator communicator;
    private CryptoScheme cryptoScheme;
    private boolean isFirstPlayer;

    public DeckShufflingProtocol(Communicator communicator, CryptoScheme cs, boolean isFirstPlayer) {
        this.communicator = communicator;
        this.cryptoScheme = cs;
        this.isFirstPlayer = isFirstPlayer;
    }

    // first, run round 1, in which each player chooses a key K_i,
    // and uses it to encrypt every card in the deck, then passes on
    // the encrypted deck.
    private void doRound1() {
        k_i = cryptoScheme.generateKey();

        if (isFirstPlayer) {
            // the first player generates the deck
            deck = Deck.generatePlainDeck();
        }
        else {
            // other players receive the deck from a previous player
            // TODO: we should not send the whole deck, just integers..
            deck = (Deck) communicator.receiveObject();
        }

        // the player encrypts the deck with their key.
        deck.encryptWithSingleKey(k_i);

        // then they shuffle it
        deck.shuffle();

        // then they pass it on to the next player.
        communicator.sendObject(deck);

    }

    private void doRound2() {

        // receive the deck from the previous player
        deck = (Deck) communicator.receiveObject();

        // decrypt it under our key
        deck.decrypt(k_i);

        // encrypt every card with its own key
        deck.encryptWithMultipleKeys();

        // pass the deck on
        communicator.sendObject(deck);
    }

    private void doRound3() {
        deck = (Deck) communicator.receiveObject();
        if (isFirstPlayer) {
            communicator.broadcastObject(deck);
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

        // every player now has the same deck, which we can return
        return deck;
    }
}
