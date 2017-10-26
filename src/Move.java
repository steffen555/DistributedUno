public class Move {
    private Player player;
    private MoveType type = MoveType.PLAY;
    private Card card;

    public Player getPlayer() {
        return player;
    }

    public MoveType getType() {
        return type;
    }

    public Card getCard() {
        return card;
    }
}
