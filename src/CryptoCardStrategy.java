import java.util.List;

public class CryptoCardStrategy implements CardStrategy {

    private CommunicationStrategy comm;

    public CryptoCardStrategy(CommunicationStrategy comm){
        this.comm = comm;
//        pile = new Pile();
    }

    @Override
    public void initializeNewDeck() {
        // Run deck shuffling/distributing protocol here.
        //DeckShufflingProtocol deckCreator = new DeckShufflingProtocol(comm, players);
        //deck = deckCreator.makeShuffledDeck();
    }

    public void distributeHands() {
        // now distribute keys and so on to initialize each player's hand,
        // as well as the pile.
//        HandDistributionProtocol distributor = new HandDistributionProtocol(comm, deck, pile, players);
//        distributor.distributeInitialCards();
    }

    @Override
    public void drawCardFromDeckForPlayer(Player player) {
//        DrawProtocol drawProtocol = new DrawProtocol(comm, players, deck);
//        drawProtocol.drawCardForCurrentPlayer();
    }

    @Override
    public List<Card> getCardsFromPlayer(Player player) {
        return null;
    }

    @Override
    public void movePlayersCardToPile(Player player, int cardIndex) {
//        PlayProtocol play = new PlayProtocol(comm, deck, pile, players);
//        play.processMoveForCurrentPlayer(move);
    }

    @Override
    public void printHand(Player player) {
//        for (Card card : players.getMe().getHand().getCards())
//            System.out.println(card);
    }

    @Override
    public Card getTopCardFromPile() {
        return null;
    }

    @Override
    public void turnTopCardFromDeck() {

    }
}
