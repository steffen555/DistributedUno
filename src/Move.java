import java.io.Serializable;

public class Move implements Serializable {
    private Player player;
    private MoveType type;
    private int cardIndex;

    public Move(Player player, MoveType type, int index) {
        this.player = player;
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
    public int getCardIndex() {
        return cardIndex;
    }

    public String toString() {
        return "Move(type=" + type + ", cardIndex=" + cardIndex + ")";
    }
}
