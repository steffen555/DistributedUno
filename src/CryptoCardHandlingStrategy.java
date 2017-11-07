import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoCardHandlingStrategy implements CardHandlingStrategy {

    private CommunicationStrategy comm;
    private Pile pile;
    private Deck deck;
    private Map<Player, List<Card>> playerHandMap;

    public CryptoCardHandlingStrategy(CommunicationStrategy comm) {
        this.comm = comm;
        pile = new Pile();
        playerHandMap = new HashMap<>();
    }

    @Override
    public void initializeNewDeck() {
        // Run deck shuffling/distributing protocol here.
        DeckShufflingProtocol deckCreator = new DeckShufflingProtocol(comm);
        deck = deckCreator.makeShuffledDeck();
    }

    public void distributeHands() {
        // now distribute keys and so on to initialize each player's hand,
        // as well as the pile.
        HandDistributionProtocol distributor = new HandDistributionProtocol(comm, deck, pile);
        distributor.distributeInitialCards();
        for (Player p : comm.getPlayers()){
            playerHandMap.put(p, p.getHand().getCards());
        }
    }

    @Override
    public void drawCardFromDeckForPlayer(Player player) {
        Card card = deck.drawCard();
        comm.decryptCardWithKeysFromOtherPlayers(player, card);
        playerHandMap.get(player).add(card);
    }

    @Override
    public Card getCardFromPlayer(Player player, int cardIndex) {
        Card card = playerHandMap.get(player).get(cardIndex);
        comm.sendPlayersKeyForCardToOtherPlayers(player, card);
        return card;
    }

    @Override
    public List<Card> getCardsFromPlayer(Player player) {
        return playerHandMap.get(player);
    }



    @Override
    public void movePlayersCardToPile(Player player, int cardIndex) {
        Card card = playerHandMap.get(player).remove(cardIndex);
        comm.sendPlayersKeyForCardToOtherPlayers(player, card);
        pile.addCard(card);
    }

    @Override
    public void printHand(Player player) {
        for (Card card : playerHandMap.get(player))
            System.out.println(card);
    }

    @Override
    public Card getTopCardFromPile() {
        return pile.getTopCard();
    }

    @Override
    public void turnTopCardFromDeck() {
        pile.addCard(deck.drawCard());
    }
}
