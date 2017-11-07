public interface MoveValidator {
    boolean isLegal(Move move);

    boolean legalMoveExists();
}
