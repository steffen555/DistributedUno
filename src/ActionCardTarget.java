public interface ActionCardTarget {
    void changeTurnDirection();
    Player getCurrentPlayer();
    Player getNextPlayer();
    void advanceTurn();
}
