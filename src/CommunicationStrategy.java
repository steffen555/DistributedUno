import java.io.Serializable;
import java.util.List;

public interface CommunicationStrategy {
    /**
     * @return A list of all the players in the game
     */
    List<Player> getPlayers();

    Move getNextMoveFromPlayer(Player player);

    void sendObjectToPlayer(Player player, Serializable object);

    Object receiveObject(Class c);

    void broadcastObject(Serializable object);

    Card decryptCardWithKeysFromOtherPlayers(Player player, EncryptedCard card);

    Card sendPlayersKeyForCardToOtherPlayers(Player player, Card card);

    void setMoveValidator(MoveValidator v);

    CardColor getColorFromPlayer(Player player);

    void handleJoiningPlayers(Player currentPlayer, GameStateSupplier game);

    void setPlayers(List<PeerInfo> peerInfos);

    void addSelfToPlayersList();

    PeerInfo getLocalPeerInfo();

    Player getLocalPlayer();

    void indicateFinished();
}
