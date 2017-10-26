import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class UnoGame {

    private Deck deck;
    private Player winner;
    private Communicator comm;
    private int numberOfPlayers;
    private int myPlayerNumber;
    private CryptoScheme cryptoScheme;
    private boolean isFirstPlayer;
    private PlayerGroup players;

    public UnoGame(Communicator comm) {
        this.comm = comm;
        this.cryptoScheme = new CryptoScheme();

        // TODO: set up the players PlayerGroup;
        // specifically, we must determine who is the first player.

        deck = new DeckShufflingProtocol(comm, cryptoScheme, isFirstPlayer).makeShuffledDeck();

        playerInTurn = computeFirstPlayer();
        distributeInitialCards();
    }

    private void makeShuffledDeck() {
        for (CardColor cc : CardColor.values()){
            for (int i = 0; i < 10; i++){
                deck.addCard(new RegularCard(cc, i));
            }
        }
        deck.shuffle();
    }

    public Player computeFirstPlayer() {
        throw new NotImplementedException();
    }

    private void distributeInitialCards() {
        for (int i = 0; i < numberOfPlayers; i++) {
            if (i == myPlayerNumber) {
                receiveInitialKeys();
            } else {
                sendInitialKeys(i);
            }
        }
        // TODO: Decrypt the cards of my hand
        // TODO: Update the deck
    }

    public void run() {
        do {
            renderState();
            doTurn();
            playerInTurn = computeNextPlayerInTurn();
        } while (!checkForWinner());
        announceWinner();
    }

    /**
     * Returns whether a player has won the game
     */
    private boolean checkForWinner() {
        throw new NotImplementedException();
    }

    private void renderState() {
        throw new NotImplementedException();
    }

    /**
     * Mechanics for the turn of any player.
     * If we are the current player, it prompts the user for an input.
     * Otherwise, we wait for the action of another player.
     */
    private void doTurn() {
        Move move = comm.receiveMove(playerInTurn);
        if (!isLegalMove(move)) {
            throw new NotImplementedException(); //TODO: Better, please
        }
        doMove(move);
    }

    private boolean isLegalMove(Move move) {
        throw new NotImplementedException();
    }

    /**
     * Does the concrete action of performing the move. The move given is checked to be valid.
     * The move can be either playing a card to the pile or drawing a new card.
     * If a card is drawn, the user should have the option to play any card.
     */
    private void doMove(Move move) {
        if (move.getType() == MoveType.PLAY) {
            doPlayMove(move);
        } else if (move.getType() == MoveType.DRAW) {
            doDrawMove(move);
        } else {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Something bad happened");
                e.printStackTrace();
            }
        }
    }

    /**
     * Performs the action of playing a card to the pile.
     */
    private void doPlayMove(Move move) {
        if (myTurn()) {
            comm.broadcastKey(move.getCard());
        } else {
            // receiveKey();
            // decrypt played card
            // check if it matches
            // send OK
        }
        updatePile(null); //TODO: get the card corresponding to the ID from the move
        updateHand();
    }

    private boolean myTurn() {
        throw new NotImplementedException();
    }

    private void updatePile(Card card) {
        throw new NotImplementedException();
    }

    private void updateHand() {
        throw new NotImplementedException();
    }

    /**
     * Performs the action of drawing a card from the deck.
     */
    private void doDrawMove(Move move) {
        throw new NotImplementedException();
    }

    private Player computeNextPlayerInTurn() {
        throw new NotImplementedException();
    }

    /**
     * Prints the winner of the game to the user
     */
    private void announceWinner() {
        throw new NotImplementedException();
    }

    private Move promptPlayerForMove() {
        throw new NotImplementedException();
    }

    private void sendInitialKeys(int i) {
        throw new NotImplementedException();
        // see pictures
    }

    private void receiveInitialKeys() {
        throw new NotImplementedException();
        // see pictures
    }
}
