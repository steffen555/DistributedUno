import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoCardHandlingStrategy implements CardHandlingStrategy {

    private ActionCardTarget actionTarget;
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
        DeckShufflingProtocol deckCreator = new DeckShufflingProtocol(actionTarget, this, comm);
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
        EncryptedCard card = (EncryptedCard) deck.drawCard();
        Card decrypted = comm.decryptCardWithKeysFromOtherPlayers(player, card);

        // if the player is new, his hand may not be initialized yet.
        if (!playerHandMap.containsKey(player))
            playerHandMap.put(player, new ArrayList<>());

        playerHandMap.get(player).add(decrypted);
    }

    @Override
    public Card getCardFromPlayer(Player player, int cardIndex) {
        List<Card> cards = playerHandMap.get(player);
        if (cardIndex >= cards.size() || cardIndex < 0)
            return null;
        Card card = cards.get(cardIndex);
        return card;
    }

    @Override
    public List<Card> getCardsFromPlayer(Player player) {
        return playerHandMap.get(player);
    }

    @Override
    public void movePlayersCardToPile(Player player, int cardIndex) {
        Card card = playerHandMap.get(player).remove(cardIndex);
        pile.addCard(card);
    }

    @Override
    public Card getTopCardFromPile() {
        return pile.getTopCard();
    }

    @Override
    public void turnTopCardFromDeck() {
        pile.addCard(deck.drawCard());
    }

    @Override
    public void revealCardFromMove(Move move) {
        int cardIndex = move.getCardIndex();
        Player player = move.getPlayer();
        Card card = playerHandMap.get(player).get(cardIndex);
        Card revealedCard = comm.sendPlayersKeyForCardToOtherPlayers(move.getPlayer(), card);
        playerHandMap.get(player).set(cardIndex, revealedCard);
    }

    public void setActionCardTarget(ActionCardTarget target) {
        actionTarget = target;
    }

    @Override
    public Pile getPile() {
        return pile;
    }

    @Override
    public void setPile(List<CardRepresentation> pileCards, ActionCardTarget act) {
        pile = new Pile();
        for (CardRepresentation c : pileCards)
            pile.addCard(c.toCard(act));
    }

    @Override
    public List<List<Card>> getHands() {
        List<List<Card>> result = new ArrayList<>();
        for (Player p : comm.getPlayers()) {
            result.add(getCardsFromPlayer(p));
        }
        return result;
    }

    @Override
    public void setHands(List<List<CardRepresentation>> handCards) {
        int i = 0;
        for(Player p : comm.getPlayers()) {
            List<CardRepresentation> reprs = handCards.get(i++);
            List<Card> cards = new ArrayList<>();
            for (CardRepresentation repr : reprs)
                cards.add(repr.toCard(actionTarget));

            playerHandMap.put(p, cards);
        }
    }

    public Deck getDeck() {
        return deck;
    }
}
