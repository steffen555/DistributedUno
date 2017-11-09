public interface ActionCardTarget {
    void changeTurnDirection();
    Player getCurrentPlayer();
    void playedSkipCard();

    void drawCardsFromDeckForNextPlayer(int numCards);

    CardColor getColorFromCurrentPlayer();
}
