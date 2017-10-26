public class Move {
    private Player player;
    private MoveType type = MoveType.PLAY;
    private int cardIndex;

    public Player getPlayer() {
        return player;
    }

    public MoveType getType() {
        return type;
    }

    /**
     * Returns the index of the card in the player's hand to be played
     */
    public int getCard() {
        return cardIndex;
    }
}
