import java.util.ArrayList;
import java.util.List;

public class HandDistributionProtocol {
    private CommunicationStrategy communicator;
    private List<Player> players;
    private Deck deck;
    private Pile pile;

    public HandDistributionProtocol(CommunicationStrategy comm, Deck d, Pile pile) {
        this.deck = d;
        this.communicator = comm;
        this.pile = pile;
        players = communicator.getPlayers();
    }

    public void distributeInitialCards() {
        // take the cards off the deck
        for (Player player : players) {
            for (int i = 0; i < Hand.CARDS_PER_HAND; i++) {
                player.drawCard(deck);
            }
        }

        // take off one more card and put it on the (currently empty) pile
        pile.addCard(deck.drawCard());

        // then distribute the keys so everyone can decrypt their own hand
        for (Player player : players) {
            if (player.getPeerInfo() == null)
                receiveInitialKeys();
            else
                sendInitialKeys(player);
        }
    }

    // sends num_players*hand_size + 1 keys to 'player'
    // so that we send the keys needed to decrypt every hand but our own,
    // and also the initial card for the Pile
    private void sendInitialKeys(Player recipient) {
        ArrayList<CryptoKey> keys = new ArrayList<>();

        // send keys for others' hands
        for (Player player : players) {
            for (Card card : player.getHand().getCards()) {
                if (player.getPeerInfo() == null)
                    keys.add(null); // don't send our own keys
                else
                    keys.add(card.getMyKey());
            }
        }

        // also send one more key for the initial pile card
        keys.add(pile.getCard(0).getMyKey());

        communicator.sendObjectToPlayer(recipient, keys);
    }

    private void receiveInitialKeys() {
        // receive from each other player in turn.
        for (int i = 0; i < players.size() - 1; i++) {
            List<CryptoKey> keys = (List<CryptoKey>) communicator.receiveObject();

            int keyIndex = 0;
            for (Player player : players) {
                for (Card card : player.getHand().getCards()) {
                    CryptoKey key = keys.get(keyIndex++);
                    if (key != null)
                        card.decrypt(key);
                }
            }
            System.out.println(pile.getTopCard());
            pile.getTopCard().decrypt(keys.get(keyIndex));
            System.out.println(pile.getTopCard());
        }
    }
}
