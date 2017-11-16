import java.util.List;

public interface CardHandlingStrategy {
    void initializeNewDeck();

    void distributeHands();

    void drawCardFromDeckForPlayer(Player player);

    Card getCardFromPlayer(Player player, int cardIndex);

    List<Card> getCardsFromPlayer(Player player);

    void movePlayersCardToPile(Player player, int cardIndex);

    Card getTopCardFromPile();

    void turnTopCardFromDeck();

    void revealCardFromMove(Move move);

    void setActionCardTarget(ActionCardTarget target);

    Pile getPile();

    void setPile(List<CardRepresentation> pileCards, ActionCardTarget act);

    List<List<Card>> getHands();

    void setHands(List<List<CardRepresentation>> handCards);

    Deck getDeck();
}
