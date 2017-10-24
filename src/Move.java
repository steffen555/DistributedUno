public class Move {
    private MoveType type = MoveType.PLAY;
    private Card card;

    public MoveType getType() {
        return type;
    }

    public Card getCard() {
        return card;
    }
}
