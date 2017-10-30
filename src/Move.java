import java.io.Serializable;

public class Move implements Serializable {
    private Player player;
    private MoveType type;
    private int cardIndex;

    public Move(MoveType type, int index) {
        this.type = type;
        this.cardIndex = index;
    }

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
