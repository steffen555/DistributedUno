public interface GameStateSupplier {
    GameState getState();
    void initializeNewDeck();
    CardHandlingStrategy getCardHandlingStrategy();
}
