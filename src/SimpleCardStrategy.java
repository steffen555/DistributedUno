import java.math.BigInteger;
import java.util.*;

public class SimpleCardStrategy implements CardStrategy {

    private CommunicationStrategy comm;
    private List<Card> deck;
    private List<Card> pile;
    private Map<Player, List<Card>> playerHandMap;

    public SimpleCardStrategy(CommunicationStrategy comm) {
        this.comm = comm;
        deck = new ArrayList<>();
        pile = new ArrayList<>();
        playerHandMap = new HashMap<>();

    }

    @Override
    public void initializeNewDeck() {
        for (int card_value = 0; card_value < Card.NUM_CARDS; card_value++) {
            Card card = new RegularCard(BigInteger.valueOf(card_value));
            deck.add(card);
        }
    }

    public void shuffleCards() {
        Collections.shuffle(deck);
    }

    public void distributeHands() {
        for (Player p : comm.getPlayers()) {
            ArrayList<Card> hand = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
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
    public List<Card> getCardsFromPlayer(Player player) {
        return playerHandMap.get(player);
    }

    @Override
    public void movePlayersCardToPile(Player player, int cardIndex) {
        pile.add(0, playerHandMap.get(player).remove(cardIndex));
    }

    @Override
    public void printHand(Player player) {
        playerHandMap.get(player).forEach(System.out::println);
    }

    @Override
    public Card getTopCardFromPile() {
        return pile.get(0);
    }

    @Override
    public void turnTopCardFromDeck() {
        pile.add(0, takeTopCardFromDeck());
    }

    private Card takeTopCardFromDeck() {
        return deck.remove(0);
    }
}
