import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class UnoGame {

    private CommunicationStrategy comm;
    private CardStrategy cardStrategy;
    private List<Player> players;
    private int currentPlayerIndex;
    private Player winner;

    public UnoGame(CommunicationStrategy comm, CardStrategy cardStrategy) {
        this.comm = comm;
        this.cardStrategy = cardStrategy;
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    private void distributeHands() {
        cardStrategy.distributeHands();
    }

    private void turnTopCardFromDeck() {
        cardStrategy.turnTopCardFromDeck();
    }

    private Move getMoveFromCurrentPlayer() {
        return getMoveFromPlayer(getCurrentPlayer());
    }

    private boolean isLegal(Move move) {
        System.out.println("Move " + move + " is legal");
        return true; //TODO
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
        cardStrategy.drawCardFromDeckForPlayer(move.getPlayer());
    }

    private boolean currentPlayerHasDrawnThisTurn() {
        throw new NotImplementedException();
    }

    private boolean gameOver() {
        throw new NotImplementedException();
    }

    private boolean isWinner(Player player) {
        return cardStrategy.getCardsFromPlayer(player).size() == 0;
    }

    /**
     * Performs the action of playing a card to the pile.
     */
    private void doPlayMove(Move move) {
        cardStrategy.movePlayersCardToPile(move.getPlayer(), move.getCardIndex());
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
        cardStrategy.initializeNewDeck();
        distributeHands();
        turnTopCardFromDeck();
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
        System.out.println("The pile has this on top: " + cardStrategy.getTopCardFromPile());
        System.out.println("Your hand looks like this:");
        cardStrategy.printHand(getCurrentPlayer());
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
            if (isLegal(move))
                break;
            else
                System.out.println(getCurrentPlayer() + " tried an illegal move. Asking again.");
        }
        doMove(move);
    }

    /**
     * Prints the winner of the game to the user.
     */
    private void announceWinner() {
        System.out.println("Player " + winner + " is the winner! But did he say Uno?");
    }

    public static void validateMove(Card playedCard, Pile pile) {
        // TODO: handle this better.
        if (!isLegal(playedCard, pile))
            System.out.println("SOMEONE IS CHEATING");
        else
            System.out.println("Playing " + playedCard + " is a valid move.");
    }
}
