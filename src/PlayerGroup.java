// encompasses a group of players playing a Uno game
public class PlayerGroup {
    private Player playerInTurn;

    public Player getPlayerInTurn() {
        return playerInTurn;
    }

    public void signalNextTurn() {
        // TODO: this should change playerInTurn to the next player.
    }

    public boolean isFirstPlayer() {
        return false; // TODO: fixme.
    }
}
