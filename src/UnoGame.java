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
        System.out.println("Here is the game state: ");
        System.out.println("lol jk");
        // TODO fix this
    }

    /**
     * Mechanics for the turn of any player.
     * If we are the current player, it prompts the user for an input.
     * Otherwise, we wait for the action of another player.
     */
    private void doTurn() {
        Move move = comm.receiveMove(players.getPlayerInTurn());
        if (!isLegalMove(move)) {
            throw new NotImplementedException(); //TODO: Better, please
        }
        doMove(move);
    }

    private boolean isLegalMove(Move move) {
        // TODO implement this
        return true;
    }

    /**
     * Does the concrete action of performing the move. The move given is checked to be valid.
     * The move can be either playing a card to the pile or drawing a new card.
     * If a card is drawn, the user should have the option to play any card.
     */
    private void doMove(Move move) {
        if (move == null) return; // TODO: remove this line, it's only for the mockup
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
            comm.broadcastObject(move.getCard());
        } else {
            // receiveKey();
            // decrypt played card
            // check if it matches
            // send OK
        }
        updatePile(null); //TODO: get the card corresponding to the ID from the move
        updateHand();
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
        // TODO: this is a mock-up, implement is with crypto and stuff
        Player player = players.getPlayerInTurn();
        player.drawCard(deck);
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
