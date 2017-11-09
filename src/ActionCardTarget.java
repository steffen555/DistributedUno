public interface ActionCardTarget {
    void changeTurnDirection();
    Player getCurrentPlayer();
    void advanceTurn();

    void drawCardFromDeckForNextPlayer();

    CardColor getColorFromCurrentPlayer();
}
