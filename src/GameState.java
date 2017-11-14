import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {
    private final List<PeerInfo> peerInfos;
    private int currentPlayerIndex;
    private int turnDirection;
    private int pendingDraws;
    private int pendingSkipCards;
    private boolean currentPlayerHasMovedThisTurn;
    private boolean currentPlayerHasDrawnThisTurn;
    private List<CardRepresentation> pileCards;
    private List<List<CardRepresentation>> handCards;

    public GameState(int currentPlayerIndex, int turnDirection,
                     int pendingDraws, int pendingSkipCards,
                     boolean currentPlayerHasMovedThisTurn, boolean currentPlayerHasDrawnThisTurn,
                     List<Player> players, PeerInfo localPeerInfo, List<Card> pileCards,
                     List<List<Card>> handCards) {
        this.currentPlayerIndex = currentPlayerIndex;
        this.turnDirection = turnDirection;
        this.pendingDraws = pendingDraws;
        this.pendingSkipCards = pendingSkipCards;
        this.currentPlayerHasMovedThisTurn = currentPlayerHasMovedThisTurn;
        this.currentPlayerHasDrawnThisTurn = currentPlayerHasDrawnThisTurn;
        peerInfos = new ArrayList<>();
        for (Player p : players) {
            PeerInfo peerInfo = p.getPeerInfo();
            if (peerInfo == null)
                peerInfo = localPeerInfo;
            System.out.println("Building state. peerinfo: " + p.getPeerInfo() + " player : " + p);
            peerInfos.add(peerInfo);
        }

        this.pileCards = new ArrayList<>();
        for (Card c : pileCards)
            this.pileCards.add(c.toRepresentation());

        this.handCards = new ArrayList<>();
        for (List<Card> cardList : handCards) {
            List<CardRepresentation> reprs = new ArrayList<>();
            for (Card c : cardList) {
                if (!(c instanceof EncryptedCard)) {
                    // remember to encrypt our own cards before sending them..
                    c = c.encrypt(c.getMyKey());
                }
                reprs.add(c.toRepresentation());
            }
            this.handCards.add(reprs);
        }
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public int getTurnDirection() {
        return turnDirection;
    }

    public int getPendingDraws() {
        return pendingDraws;
    }

    public int getPendingSkipCards() {
        return pendingSkipCards;
    }

    public boolean getCurrentPlayerHasMovedThisTurn() {
        return currentPlayerHasMovedThisTurn;
    }

    public boolean getCurrentPlayerHasDrawnThisTurn() {
        return currentPlayerHasDrawnThisTurn;
    }

    public List<PeerInfo> getPeerInfos() {
        return peerInfos;
    }

    public List<CardRepresentation> getPileCards() {
        return pileCards;
    }

    public List<List<CardRepresentation>> getHandCards() {
        return handCards;
    }
}
