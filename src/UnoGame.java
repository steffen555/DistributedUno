public class UnoGame {

    private Deck deck;
    private Player playerInTurn;
    private Player winner;
    private Communicator comm;
    private int numberOfPlayers;
    private int myPlayerNumber;
    private Player[] players;

    public UnoGame(Communicator comm) {
        this.comm = comm;
        deck = makeShuffledDeck();
        playerInTurn = computeFirstPlayer();
        distributeInitialCards();
    }

    private Deck makeShuffledDeck() {
        return null;
    }

    public Player computeFirstPlayer() {
        return null;
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
        while (!checkForWinner()) {
            renderState();
            try {
                doTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            playerInTurn = computeNextPlayerInTurn();
        }
        announceWinner();
    }

    /**
     * Returns whether a player has won the game
     */
    private boolean checkForWinner() {
        return false;
    }

    private void renderState() {
    }

    /**
     * Mechanics for the turn of any player.
     * If we are the current player, it prompts the user for an input.
     * Otherwise, we wait for the action of another player.
     */
    private void doTurn() throws Exception {
        Move move = comm.receiveMove(playerInTurn);
        if (!isLegalMove(move)) {
            throw new Exception(); //TODO: Better, please
        }
        doMove(move);
    }

    private boolean isLegalMove(Move move) {
        return true;
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
        updatePile(move.getCard());
        updateHand();
    }

    private boolean myTurn() {
        return false;
    }

    private void updatePile(Card card) {
    }

    private void updateHand() {
    }

    /**
     * Performs the action of drawing a card from the deck.
     */
    private void doDrawMove(Move move) {

    }

    private Player computeNextPlayerInTurn() {
        return null;
    }

    /**
     * Prints the winner of the game to the user
     */
    private void announceWinner() {
    }

    private Move promptPlayerForMove() {
        return null;
    }

    private void sendInitialKeys(int i) {
        // see pictures
    }

    private void receiveInitialKeys() {
        // see pictures
    }
}
