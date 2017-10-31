import java.io.Serializable;
import java.util.List;

public interface CommunicationStrategy {
    /**
     * @return A list of all the players in the game
     */
    List<Player> getPlayers();

    Move getNextMoveFromPlayer(Player player);

    void sendObjectToPlayer(Player player, Serializable object);

    Object receiveObject();

    void broadcastObject(Serializable object);
}
