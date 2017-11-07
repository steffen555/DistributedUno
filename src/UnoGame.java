import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class UnoGame implements MoveValidator {

    private CommunicationStrategy comm;
    private CardHandlingStrategy cardHandlingStrategy;
    private List<Player> players;
    private int currentPlayerIndex;
    private Player winner;
    private boolean currentPlayerHasDrawnThisTurn;

    public UnoGame(CommunicationStrategy comm, CardHandlingStrategy cardHandlingStrategy) {
        this.comm = comm;
        comm.setMoveValidator(this);
        this.cardHandlingStrategy = cardHandlingStrategy;
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayerHasDrawnThisTurn = false;
    }

    private void distributeHands() {
        cardHandlingStrategy.distributeHands();
    }

    private void turnTopCardFromDeck() {
        cardHandlingStrategy.turnTopCardFromDeck();
    }

    private Move getMoveFromCurrentPlayer() {
        return getMoveFromPlayer(getCurrentPlayer());
    }

    public boolean isLegal(Move move) {
        if (move.getType() == MoveType.PLAY) {
            Card playedCard = cardHandlingStrategy.getCardFromPlayer(move.getPlayer(), move.getCardIndex());
            if (playedCard == null) {
                System.out.println("Invalid move!");
                return false;
            }
            Card topCard = cardHandlingStrategy.getTopCardFromPile();
            return (playedCard.getColor() == topCard.getColor()) ||
                    (playedCard.getNumber() == topCard.getNumber());
        } else if (move.getType() == MoveType.DRAW){
            return !currentPlayerHasDrawnThisTurn;
        } else {
            System.out.println("Invalid move!");
            return false;
        }
    }

    /**
     * Does the concrete action of performing the move. The move given is checked to be valid.
     * The move can be either playing a card to the pile or drawing a new card.
     * If a card is drawn, the user should have the option to play any card.
     */
    private boolean doMove(Move move) {
        if (move.getType() == MoveType.PLAY) {
            return doPlayMove(move);
        } else if (move.getType() == MoveType.DRAW) {
            return doDrawMove(move);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the action of drawing a card from the deck.
     */
    private boolean doDrawMove(Move move) {
        cardHandlingStrategy.drawCardFromDeckForPlayer(move.getPlayer());
        currentPlayerHasDrawnThisTurn = true;
        return true;
    }

    private boolean gameOver() {
        throw new NotImplementedException();
    }

    private boolean isWinner(Player player) {
        return cardHandlingStrategy.getCardsFromPlayer(player).size() == 0;
    }

    /**
     * Performs the action of playing a card to the pile.
     */
    private boolean doPlayMove(Move move) {
        cardHandlingStrategy.revealCardFromMove(move);
        if (!isLegal(move))
            return false;

        cardHandlingStrategy.movePlayersCardToPile(move.getPlayer(), move.getCardIndex());
        return true;
    }

    private Move getMoveFromPlayer(Player player) {
        return comm.getNextMoveFromPlayer(player);
    }

    /**
     * Returns whether a player has won the game
     */
    private boolean checkForWinner() {
        for(Player p : players)
            if(isWinner(p)) {
                winner = p;
                return true;
            }
        return false;
    }

    public void run() {
        players = comm.getPlayers();
        cardHandlingStrategy.initializeNewDeck();
        distributeHands();
        do {
            renderState();
            doTurn();
            nextTurn();
        } while (!checkForWinner());
        announceWinner();
    }

    private void renderState() {
        System.out.println("------------------------");
        System.out.println("Awaiting move from player " + currentPlayerIndex);
        System.out.println("Pile:");
        CardPrinter.printCard(cardHandlingStrategy.getTopCardFromPile());
        System.out.println("Player hand:");
        CardPrinter.printCards(cardHandlingStrategy.getCardsFromPlayer(getCurrentPlayer()));
        System.out.println("------------------------");
    }

    /**
     * Mechanics for the turn of any player.
     * If we are the current player, it prompts the user for an input.
     * Otherwise, we wait for the action of another player.
     */
    private void doTurn() {
        Move move;
        while (true) {
            move = getMoveFromCurrentPlayer();
            if (doMove(move))
                // it was valid, so the turn is over
                break;
        }
    }

    /**
     * Prints the winner of the game to the user.
     */
    private void announceWinner() {
        System.out.println("Player " + winner + " is the winner! But did he say Uno?");
    }
}
