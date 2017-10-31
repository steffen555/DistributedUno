import java.util.List;

public interface CardStrategy {
    void initializeNewDeck();

    void distributeHands();

    void drawCardFromDeckForPlayer(Player player);

    List<Card> getCardsFromPlayer(Player player);

    void movePlayersCardToPile(Player player, int cardIndex);

    void printHand(Player player);

    Card getTopCardFromPile();

    void turnTopCardFromDeck();
}
