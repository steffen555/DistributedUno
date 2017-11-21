import java.io.Serializable;

public class Move implements Serializable {
    private Player player;
    private MoveType type;
    private int cardIndex;
    private boolean uno;

    public Move(Player player, MoveType type, int index) {
        this(player, type, index, false);
    }

    public Move(Player player, MoveType type, int index, boolean uno) {
        this.player = player;
        this.type = type;
        this.cardIndex = index;
        this.uno = uno;
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
        if (type == MoveType.DRAW)
            return "Draw card from deck";
        else if (type == MoveType.PLAY)
            return "Play card no. " + cardIndex;
        else
            return "(unknown move: " + type.toString() + ")";
    }

    public boolean saidUno(){
        return uno;
    }
}
