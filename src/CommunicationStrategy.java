import java.util.List;

public interface CommunicationStrategy {
    /**
     * @return A list of all the players in the game
     */
    List<Player> getPlayers();

    Move getNextMoveFromPlayer(Player player);

    void sendObjectToPlayer(Player player, Object object);

    Object receiveObject();

    void broadcastObject(Object object);
}
