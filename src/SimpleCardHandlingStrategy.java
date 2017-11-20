import java.util.*;

public class SimpleCardHandlingStrategy implements CardHandlingStrategy {

    private CommunicationStrategy comm;
    private List<Card> deck;
    private List<Card> pile;
    private Map<Player, List<Card>> playerHandMap;

    public SimpleCardHandlingStrategy(CommunicationStrategy comm) {
        this.comm = comm;
        deck = new ArrayList<>();
        pile = new ArrayList<>();
        playerHandMap = new HashMap<>();

    }

    @Override
    public void initializeNewDeck() {
        ArrayList<Card> tmp = new ArrayList<>();
        for (CardColor color : CardColor.values())
            for (int i = 0; i < Card.NUM_CARDS_PER_COLOR; i++)
                deck.add(new RegularCard(null, color, i));
    }

    public void shuffleCards() {
//        Collections.shuffle(deck);
    }

    public void distributeHands() {
        for (Player p : comm.getPlayers()) {
            System.out.println("Distributing cards to " + p);
            ArrayList<Card> hand = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                hand.add(takeTopCardFromDeck());
            }
            playerHandMap.put(p, hand);
        }
    }

    @Override
    public void drawCardFromDeckForPlayer(Player player) {
        playerHandMap.get(player).add(takeTopCardFromDeck());
    }

    @Override
    public Card getCardFromPlayer(Player player, int cardIndex) {
        return playerHandMap.get(player).get(cardIndex);
    }

    @Override
    public List<Card> getCardsFromPlayer(Player player) {
        return playerHandMap.get(player);
    }

    @Override
    public void movePlayersCardToPile(Player player, int cardIndex) {
        pile.add(0, playerHandMap.get(player).remove(cardIndex));
    }

    @Override
    public Card getTopCardFromPile() {
        return pile.get(0);
    }

    @Override
    public void turnTopCardFromDeck() {
        pile.add(0, takeTopCardFromDeck());
    }

    @Override
    public void revealCardFromMove(Move move) {
    }

    private Card takeTopCardFromDeck() {
        return deck.remove(0);
    }

    public void setActionCardTarget(ActionCardTarget t) {
        // not implemented
    }

    @Override
    public Pile getPile() {
        return null;
    }

    @Override
    public void setPile(List<CardRepresentation> pileCards, ActionCardTarget act) {

    }

    @Override
    public List<List<Card>> getHands() {
        return null;
    }

    @Override
    public void setHands(List<List<CardRepresentation>> handCards) {

    }

    @Override
    public Deck getDeck() {
        return null;
    }

}
