import java.io.IOException;

import java.util.List;

public class UnoGame implements MoveValidator, ActionCardTarget {

    private CommunicationStrategy comm;
    private CardHandlingStrategy cardHandlingStrategy;
    private List<Player> players;
    private int currentPlayerIndex;
    private Player winner;
    private boolean currentPlayerHasMovedThisTurn;
    private boolean currentPlayerHasDrawnThisTurn;
    private int turnDirection = 1;
    private int pendingSkipCards = 0;
    private int pendingDraws = 0;

    public UnoGame(CommunicationStrategy comm, CardHandlingStrategy cardHandlingStrategy) {
        this.comm = comm;
        comm.setMoveValidator(this);
        this.cardHandlingStrategy = cardHandlingStrategy;
        cardHandlingStrategy.setActionCardTarget(this);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    @Override
    public void playedSkipCard() {
        pendingSkipCards++;
    }

    public Player getNextPlayer() {
        int index = currentPlayerIndex + turnDirection;
        if (index < 0)
            index += players.size();
        index %= players.size();
        return players.get(index);
    }

    public void advanceTurn() {
        currentPlayerIndex += turnDirection;
        if (currentPlayerIndex < 0)
            currentPlayerIndex += players.size();
        currentPlayerIndex %= players.size();
        currentPlayerHasDrawnThisTurn = false;
        currentPlayerHasMovedThisTurn = false;
    }

    @Override
    public void drawCardsFromDeckForNextPlayer(int numCards) {
        pendingDraws += numCards;
    }

    @Override
    public CardColor getColorFromCurrentPlayer() {
        return comm.getColorFromPlayer(getCurrentPlayer());
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
            Card topCard = cardHandlingStrategy.getTopCardFromPile();

            if (playedCard == null) {
                return false;
            }

            // if there are draws pending, you may only put a +2 on top of another +2,
            // and likewise for a +4.
            if (pendingDraws != 0) {
                return topCard.getClass().equals(playedCard.getClass());
            }

            if (playedCard.getNumber() == topCard.getNumber())
                return true;

            boolean isColorValid = (
                   playedCard.getColor() == topCard.getColor() ||
                   playedCard.getColor() == CardColor.NO_COLOR
            );

            if (isColorValid && !currentPlayerHasMovedThisTurn)
                return true;

        } else if (move.getType() == MoveType.DRAW) {
            return !currentPlayerHasDrawnThisTurn && !currentPlayerHasMovedThisTurn;
        } else if (move.getType() == MoveType.END_TURN) {
            return currentPlayerHasMovedThisTurn || currentPlayerHasDrawnThisTurn;
        }

        return false;
    }

    /**
     * Caller must ensure that the UnoGame class can access all of the current player's cards,
     * i.e. check that the player is local.
     */
    public boolean legalMoveExists() {
        return isLegal(new Move(players.get(currentPlayerIndex), MoveType.DRAW, 0)) || legalPlayMoveExists();
    }

    private boolean legalPlayMoveExists() {
        int i = 0;
        for (Card c : cardHandlingStrategy.getCardsFromPlayer(players.get(currentPlayerIndex))) {
            if (isLegal(new Move(players.get(currentPlayerIndex), MoveType.PLAY, i++))) {
                return true;
            }
        }
        return false;
    }


    /**
     * Does the concrete action of performing the move. The move given is checked to be valid.
     * The move can be either playing a card to the pile or drawing a new card.
     * If a card is drawn, the user should have the option to play any card.
     */
    private boolean doMove(Move move) {
        if (move.getType() == MoveType.PLAY) {
            return doPlayMove(move);
        } else if (move.getType() == MoveType.DRAW && pendingDraws != 0) {
            doPendingDrawMove(move);
            advanceTurn();
            return true;
        } else if (move.getType() == MoveType.DRAW) {
            return doSimpleDrawMove(move);
        } else if (move.getType() == MoveType.END_TURN) {
            advanceTurn();
            consumeSkips();
            return true;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void doPendingDrawMove(Move move) {
        for (int i = 0; i < pendingDraws; i++)
            cardHandlingStrategy.drawCardFromDeckForPlayer(move.getPlayer());
        pendingDraws = 0;
    }

    private void consumeSkips() {
        for (int i = 0; i < pendingSkipCards; i++)
            advanceTurn();
        pendingSkipCards = 0;
    }

    /**
     * Performs the action of drawing a card from the deck.
     */
    private boolean doSimpleDrawMove(Move move) {
        cardHandlingStrategy.drawCardFromDeckForPlayer(move.getPlayer());
        currentPlayerHasDrawnThisTurn = true;
        return true;
    }

    private boolean isWinner(Player player) {
        return cardHandlingStrategy.getCardsFromPlayer(player).size() == 0;
    }

    /**
     * Performs the action of playing a card to the pile.
     */
    private boolean doPlayMove(Move move) {
//        System.out.println("Revealing card number " + move.getCardIndex());
        cardHandlingStrategy.revealCardFromMove(move);
//        System.out.println("Revealed it.");
        if (!isLegal(move)) {
            System.out.println("That was a BAD MOVE");
            return false;
        }

        Card card = cardHandlingStrategy.getCardFromPlayer(move.getPlayer(), move.getCardIndex());
        currentPlayerHasMovedThisTurn = true;
        cardHandlingStrategy.movePlayersCardToPile(move.getPlayer(), move.getCardIndex());
//        System.out.println("Moved it to the pile.");

        if (card instanceof ActionCard) {
            ((ActionCard) card).performAction();
        }

        return true;
    }

    private Move getMoveFromPlayer(Player player) {
        return comm.getNextMoveFromPlayer(player);
    }

    /**
     * Returns whether a player has won the game
     */
    private boolean checkForWinner() {
        for (Player p : players)
            if (isWinner(p)) {
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
        } while (!checkForWinner());
        announceWinner();
    }

    private void renderState() {
        clearScreen();
        System.out.println("------------------------");
        System.out.println("Awaiting move from player " + currentPlayerIndex);
        System.out.println("Pile:");
        CardPrinter.printCard(cardHandlingStrategy.getTopCardFromPile());
        System.out.println("Player hands:");
        for (Player player : players) {
            if (player.equals(getCurrentPlayer()))
                System.out.println("In turn");
            CardPrinter.printCards(cardHandlingStrategy.getCardsFromPlayer(player));
        }
        System.out.println("------------------------");
    }

    /**
     * Clears the screen
     */
    public static void clearScreen(){
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {e.printStackTrace();}
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

    @Override
    public void changeTurnDirection() {
        if (players.size() == 2)
            pendingSkipCards++;
        else
            turnDirection *= -1;
    }
}
