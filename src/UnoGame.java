import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class UnoGame {

    private Deck deck;
    private Communicator comm;
    private PlayerGroup players;
    private Pile pile;
    private Player currentPlayer;

    public UnoGame(Communicator comm) {
        this.comm = comm;
        pile = new Pile();
        players = new PlayerGroup(comm.getPlayers());
        currentPlayer = getCurrentPlayer();
        shuffleDeck();
        distributeHands();
    }

    private Player getCurrentPlayer() {
        return players.getPlayerInTurn();
    }

    private Player getNextPlayer() {
        throw new NotImplementedException();
    }

    private void shuffleDeck(){
        DeckShufflingProtocol deckCreator = new DeckShufflingProtocol(comm, players);
        deck = deckCreator.makeShuffledDeck();
    }

    private void distributeHands(){
        // now distribute keys and so on to initialize each player's hand,
        // as well as the pile.
        HandDistributionProtocol distributor = new HandDistributionProtocol(comm, deck, pile, players);
        distributor.distributeInitialCards();
    }

    private Move getMoveFromCurrentPlayer() {
        return getMoveFromPlayer(currentPlayer);
    }

    public static boolean isLegal(Card playedCard, Pile pile) {
        Card topCard = pile.getTopCard();
        return (playedCard.getColor() == topCard.getColor()) ||
                (playedCard.getNumber() == topCard.getNumber());
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
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the action of drawing a card from the deck.
     */
    private void doDrawMove(Move move) {
        // since the top card in the deck is going to playerInTurn's hand,
        // that means that every player except him must share the key
        // with every other player.
        // TODO
        throw new NotImplementedException();
    }

    private boolean currentPlayerHasDrawnThisTurn() {
        throw new NotImplementedException();
    }

    private boolean gameOver() {
        throw new NotImplementedException();
    }

    private boolean isWinner(Player player) {
        throw new NotImplementedException();
    }

    /**
     * Performs the action of playing a card to the pile.
     */
    private void doPlayMove(Move move) {
        if (players.myTurn()) {
            comm.broadcastObject(move.getCard());
        } else {
            // receiveKey();
            // decrypt played card
            // check if it matches
            // send OK
        }
        throw new NotImplementedException();
    }

    private Move getMoveFromPlayer(Player player) {
        throw new NotImplementedException();
    }

    /**
     * Returns whether a player has won the game
     */
    private boolean checkForWinner() {
        throw new NotImplementedException();
    }

    public void run() {
        do {
            renderState();
            doTurn();
            players.signalNextTurn();
        } while (!checkForWinner());
        announceWinner();
    }

    private void renderState() {
        System.out.println("------------------------");
        System.out.println("The pile has this on top: " + pile.getTopCard());
        System.out.println("Your hand looks like this:");
        for (Card card : players.getMe().getHand().getCards())
            System.out.println(card);
        System.out.println("------------------------");
    }

    /**
     * Mechanics for the turn of any player.
     * If we are the current player, it prompts the user for an input.
     * Otherwise, we wait for the action of another player.
     */
    private void doTurn() {
        Move move = comm.receiveMove(players.getPlayerInTurn());
        if (players.myTurn())
            comm.broadcastObject(move);

        if (!isLegal(move)) {
            throw new NotImplementedException();
        }

        doMove(move);
    }

    /**
     * Prints the winner of the game to the user
     */
    private void announceWinner() {
        throw new NotImplementedException();
    }

    public static void validateMove(Card playedCard, Pile pile) {
        // TODO: handle this better.
        if (!isLegalMove(playedCard, pile))
            System.out.println("SOMEONE IS CHEATING");
        else
            System.out.println("Playing " + playedCard + " is a valid move.");
    }
}
