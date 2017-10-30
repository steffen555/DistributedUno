import java.util.List;

// encompasses a group of players playing a Uno game
public class PlayerGroup {
    private Player playerInTurn;

    // all the players in this group
    // NOTE: these must be sorted based on who goes first in turn
    List<Player> players;
    int currentPlayerIndex;

    public PlayerGroup(List<Player> players) {
        this.players = players;
        determinePlayerOrder();
    }

    private void determinePlayerOrder() {
        // TODO: do this better via a custom random protocol
        // Collections.sort(players);
        // For now, just use whatever order we happen to have..
    }

    public Player getPlayerInTurn() {
        return players.get(currentPlayerIndex);
    }

    public void signalNextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public int getMeIndex() {
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player instanceof LocalPlayer)
                return i;
        }
        return -1; // should not happen.
    }

    public Player getMe() {
        return players.get(getMeIndex());
    }

    public Player firstPlayer() {
        return players.get(0);
    }

    public boolean isFirstPlayer() {
        return getMe().equals(firstPlayer());
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean myTurn() {
        return getPlayerInTurn().equals(getMe());
    }

    public Player playerAfterMe() {
        int index = (getMeIndex() + 1) % players.size();
        return players.get(index);
    }

    public Player getPlayer(int i) {
        return players.get(i);
    }
}
