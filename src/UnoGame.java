public class UnoGame {

    private Deck deck;
    private Player winner;
    private Communicator comm;
    private int numberOfPlayers;
    private int myPlayerNumber;
    private CryptoScheme cryptoScheme;
    private PlayerGroup players;

    public UnoGame(Communicator comm) {
        this.comm = comm;

        // TODO: set up the players PlayerGroup;
        // specifically, we must determine who is the first player.

        deck = new DeckShufflingProtocol(comm, players.isFirstPlayer()).makeShuffledDeck();

        distributeInitialCards();
    }

    /**
     * Prints the winner of the game to the user
     */
    private void announceWinner() {
    }

    /**
     * Returns whether a player has won the game
     */
    private boolean checkForWinner() {
        return false;
    }

    private Player computeNextPlayerInTurn() {
        return null;
    }

    /**
     * Mechanics for the turn of any player.
     * If we are the current player, it prompts the user for an input.
     * Otherwise, we wait for the action of another player.
     */
    private void doTurn() throws Exception {
        Move move;
        if (myTurn()) {
            move = promptPlayerForMove();
            if (!isLegalMove(move)) {
                throw new Exception(); //TODO: Better, please
            }
            comm.broadcastMove(move);
            doMove(move);
        } else {
            move = comm.receiveMove(players.getPlayerInTurn());
            if (!isLegalMove(move)) {
                throw new Exception(); //TODO: Better, please
            }
            doMove(move);
        }
    }

    /**
     * Does the concrete action of performing the move. The move given is checked to be valid.
     * The move can be either playing a card to the pile or drawing a new card.
     * If a card is drawn, the user should have the option to play any card.
     */
    private void doMove(Move move) {
        if(move.getType() == MoveType.PLAY){
            doPlayMove(move);
        } else if(move.getType() == MoveType.DRAW) {
            doDrawMove(move);
        } else{
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Something bad happened");
                e.printStackTrace();
            }
        }
    }

    /**
     * Performs the action of drawing a card from the deck.
     */
    private void doDrawMove(Move move) {

    }

    /**
     * Performs the action of playing a card to the pile.
     */
    private void doPlayMove(Move move) {
        if(myTurn()){
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

    private void updateHand() {
    }

    private void updatePile(Card card) {
    }

    private boolean isLegalMove(Move move) {
        return false;
    }

    private Move promptPlayerForMove() {
        return null;
    }

    private boolean myTurn() {
        return false;
    }

    private void renderState() {
    }

    private void distributeInitialCards() {
        for (int i = 0; i < numberOfPlayers; i++){
            if(i == myPlayerNumber){
                receiveInitialKeys();
            } else {
                sendInitialKeys(i);
            }
        }
        // TODO: Decrypt the cards of my hand
        // TODO: Update the deck
    }

    private void sendInitialKeys(int i) {
        // see pictures
    }

    private void receiveInitialKeys() {
        // see pictures
    }

    public void run() {
        while (!checkForWinner()) {
            renderState();
            try {
                doTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // signal that it's a new player's turn
            players.signalNextTurn();
        }
        announceWinner();
    }

    public Player computeFirstPlayer() {
        return null;
    }
}
