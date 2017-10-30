import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class UnoGame {

    private Deck deck;
    private Player winner;
    private Communicator comm;
    private PlayerGroup players;
    private Pile pile;

    public UnoGame(Communicator comm) {
        this.comm = comm;

        pile = new Pile();
        players = new PlayerGroup(comm.getPlayers());

        DeckShufflingProtocol deckCreator = new DeckShufflingProtocol(comm, players);
        deck = deckCreator.makeShuffledDeck();

        // now distribute keys and so on to initialize each player's hand,
        // as well as the pile.
        HandDistributionProtocol distributer = new HandDistributionProtocol(comm, deck, pile, players);
        distributer.distributeInitialCards();
    }

    public void run() {
        do {
            renderState();
            doTurn();
            players.signalNextTurn();
        } while (!checkForWinner());
        announceWinner();
    }

    /**
     * Returns whether a player has won the game
     */
    private boolean checkForWinner() {
        return false; // TODO implement me
    }

    private void renderState() {
        System.out.println("------------------------");
        System.out.println("The pile has this on top: " + pile.getTopCard());
        System.out.println("Your hand looks like this:");
        System.out.println(players.getMe().getHand());
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

        doMove(move);
    }

    public static boolean isLegalMove(Card playedCard, Pile pile) {
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
        PlayProtocol play = new PlayProtocol(comm, deck, pile, players);
        play.processMoveForCurrentPlayer(move);
    }

    private void updatePile(Card card) {
        return; // TODO: implement
    }

    private void updateHand() {
        return; // TODO: implement
    }

    /**
     * Performs the action of drawing a card from the deck.
     */
    private void doDrawMove(Move move) {
        DrawProtocol drawProtocol = new DrawProtocol(comm, players, deck);
        drawProtocol.drawCardForCurrentPlayer();
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


}
