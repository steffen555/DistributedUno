import java.io.IOException;
import java.util.List;

public class UnoGame implements MoveValidator, ActionCardTarget, GameStateSupplier {

    private CommunicationStrategy comm;
    private CardHandlingStrategy cardHandlingStrategy;
    private int currentPlayerIndex;
    private Player winner;
    private boolean currentPlayerHasMovedThisTurn;
    private boolean currentPlayerHasDrawnThisTurn;
    private int turnDirection = 1;
    private int pendingSkipCards = 0;
    private int pendingDraws = 0;
    private Logger logger;

    public UnoGame(CommunicationStrategy comm, CardHandlingStrategy cardHandlingStrategy) {
        this.comm = comm;
        comm.setMoveValidator(this);
        this.cardHandlingStrategy = cardHandlingStrategy;
        cardHandlingStrategy.setActionCardTarget(this);

        logger = new Logger("UnoGame", "log.txt", Logger.DEBUG);
    }

    public Player getCurrentPlayer() {
        return getPlayers().get(currentPlayerIndex);
    }

    @Override
    public void playedSkipCard() {
        pendingSkipCards++;
    }

    public Player getNextPlayer() {
        int index = currentPlayerIndex + turnDirection;
        if (index < 0)
            index += getPlayers().size();
        index %= getPlayers().size();
        return getPlayers().get(index);
    }

    public void advanceTurn() {
        currentPlayerIndex += turnDirection;
        if (currentPlayerIndex < 0)
            currentPlayerIndex += getPlayers().size();
        currentPlayerIndex %= getPlayers().size();
        currentPlayerHasDrawnThisTurn = false;
        currentPlayerHasMovedThisTurn = false;
    }

    public GameState getState() {
        return new GameState(currentPlayerIndex, turnDirection, pendingDraws, pendingSkipCards,
                currentPlayerHasMovedThisTurn, currentPlayerHasDrawnThisTurn,
                comm.getPlayers(), comm.getLocalPeerInfo(),
                cardHandlingStrategy.getPile().getCards(),
                cardHandlingStrategy.getHands());
    }

    public void setState(GameState state) {
        currentPlayerIndex = state.getCurrentPlayerIndex();
        turnDirection = state.getTurnDirection();
        pendingDraws = state.getPendingDraws();
        pendingSkipCards = state.getPendingSkipCards();
        currentPlayerHasMovedThisTurn = state.getCurrentPlayerHasMovedThisTurn();
        currentPlayerHasDrawnThisTurn = state.getCurrentPlayerHasDrawnThisTurn();
        comm.setPlayers(state.getPeerInfos());
        cardHandlingStrategy.setPile(state.getPileCards(), this);
        cardHandlingStrategy.setHands(state.getHandCards());
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
                            playedCard.getColor() == CardColor.NO_COLOR ||
                            topCard.getColor() == CardColor.NO_COLOR
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
        return isLegal(new Move(getPlayers().get(currentPlayerIndex), MoveType.DRAW, 0)) || legalPlayMoveExists();
    }

    private boolean legalPlayMoveExists() {
        int i = 0;
        for (Card c : cardHandlingStrategy.getCardsFromPlayer(getPlayers().get(currentPlayerIndex))) {
            if (isLegal(new Move(getPlayers().get(currentPlayerIndex), MoveType.PLAY, i++))) {
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
            doPlayMove(move);
            return false;
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
        return false;
    }

    private boolean isWinner(Player player) {
        return cardHandlingStrategy.getCardsFromPlayer(player).size() == 0;
    }

    /**
     * Performs the action of playing a card to the pile.
     */
    private void doPlayMove(Move move) {
//        System.out.println("Revealing card number " + move.getCardIndex());
        cardHandlingStrategy.revealCardFromMove(move);
        if (!isLegal(move)) {
            System.out.println("Illegal move!");
            return;
        }

        Card card = cardHandlingStrategy.getCardFromPlayer(move.getPlayer(), move.getCardIndex());
        currentPlayerHasMovedThisTurn = true;
        cardHandlingStrategy.movePlayersCardToPile(move.getPlayer(), move.getCardIndex());

        if (card instanceof ActionCard) {
            ((ActionCard) card).performAction();
        }
        if (cardHandlingStrategy.getCardsFromPlayer(move.getPlayer()).size() == 1 && !move.saidUno()) {
            System.out.println("Forgot to say UNO!");
            cardHandlingStrategy.drawCardFromDeckForPlayer(move.getPlayer());
            cardHandlingStrategy.drawCardFromDeckForPlayer(move.getPlayer());
        }
    }

    private Move getMoveFromPlayer(Player player) {
        return comm.getNextMoveFromPlayer(player);
    }

    /**
     * Returns whether a player has won the game
     */
    private boolean checkForWinner() {
        for (Player p : getPlayers())
            if (isWinner(p)) {
                winner = p;
                return true;
            }
        return false;
    }

    private List<Player> getPlayers() {
        return comm.getPlayers();
    }

    public void run() {
        cardHandlingStrategy.initializeNewDeck();
        distributeHands();
        //Handling when the initial card on the pile is an ActionCard
        Card topPileCard = cardHandlingStrategy.getTopCardFromPile();
        if (topPileCard instanceof ActionCard) {
            if (topPileCard instanceof DrawFourAndChangeColorCard)
                ((DrawFourAndChangeColorCard) topPileCard).performInitialAction();
            else if (topPileCard instanceof SkipCard || (comm.getPlayers().size() == 2 && topPileCard instanceof ChangeTurnDirectionCard))
                advanceTurn();
            else if (!(topPileCard instanceof ChangeColorCard))
                ((ActionCard) topPileCard).performAction();
        }
        do {
            doTurn();
        } while (!checkForWinner());
        announceWinner();
    }

    private void renderState() {
        clearScreen();
        System.out.println("------------------------");
        System.out.println("Pile:");
        MultiLinePrinter pilePrinter = CardPrinter.printCard(cardHandlingStrategy.getTopCardFromPile());
        if (pendingDraws != 0)
            pilePrinter.print(2, " Pending draws: " + pendingDraws + CardPrinter.NO_COLOR);
        System.out.println(pilePrinter.getOutput());
        if (turnDirection == 1)
            System.out.print("Game direction: ↓ \n"); // arrow options: ↓,⇓,⇩,∨,⌄
        else
            System.out.print("Game direction: ↑ \n"); // arrow options: ↑,⇑,⇧,∧,⌃
        System.out.flush();
        System.out.println("Player hands:");
        for (Player player : comm.getPlayers()) {
            List<Card> cards = cardHandlingStrategy.getCardsFromPlayer(player);
            MultiLinePrinter handPrinter = CardPrinter.printCards(cards);

            String nameRepresentation = " " + CardPrinter.BOLD + player.getName() + CardPrinter.NO_COLOR;
            handPrinter.printWithoutWrapping(2, nameRepresentation);

            if (player.equals(getCurrentPlayer())) {
                String inTurnMarker = CardPrinter.BOLD + " <---[in turn]" + CardPrinter.NO_COLOR;
                handPrinter.printWithoutWrapping(2, inTurnMarker);
            }

            System.out.print(handPrinter.getOutput());
        }
        System.out.println("------------------------");
    }

    /**
     * Clears the screen
     */
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Mechanics for the turn of any player.
     * If we are the current player, it prompts the user for an input.
     * Otherwise, we wait for the action of another player.
     */
    private void doTurn() {
        logger.debug("doTurn was called");

        renderState();

        // first, if any players want to join, handle it
        comm.handleJoiningPlayers(getCurrentPlayer(), this);

        while (true) {
            Move move = getMoveFromCurrentPlayer();
            logger.info("Got a move: " + move);
            if (doMove(move))
                // the turn is over
                break;

            renderState();
        }
    }

    /**
     * Prints the winner of the game to the user.
     */
    private void announceWinner() {
        System.out.println("Player " + winner + " is the winner!!!");
    }

    @Override
    public void changeTurnDirection() {
        if (comm.getPlayers().size() == 2)
            pendingSkipCards++;
        else
            turnDirection *= -1;
    }

    public void initializeNewDeck() {
        cardHandlingStrategy.initializeNewDeck();
    }

    @Override
    public CardHandlingStrategy getCardHandlingStrategy() {
        return cardHandlingStrategy;
    }

    public void join(GameState state) {
        setState(state);
        comm.addSelfToPlayersList();

        logger.debug("New player initializes deck");
        initializeNewDeck();
        logger.debug("New player done initializing deck");

        logger.debug("New player drawing hand cards");
        for (int i = 0; i < Hand.CARDS_PER_HAND; i++)
            cardHandlingStrategy.drawCardFromDeckForPlayer(comm.getLocalPlayer());
        logger.debug("New player drew the cards");

        do {
            doTurn();
        } while (!checkForWinner());
        announceWinner();
    }
}
