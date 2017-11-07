import java.util.List;

public interface CardHandlingStrategy {
    void initializeNewDeck();

    void distributeHands();

    void drawCardFromDeckForPlayer(Player player);

    Card getCardFromPlayer(Player player, int cardIndex);

    List<Card> getCardsFromPlayer(Player player);

    void movePlayersCardToPile(Player player, int cardIndex);

    void printHand(Player player);

    Card getTopCardFromPile();

    void turnTopCardFromDeck();

    void revealCardFromMove(Move move);
}
