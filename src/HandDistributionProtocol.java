import java.util.ArrayList;
import java.util.List;

public class HandDistributionProtocol {
    private CommunicationStrategy communicator;
    private PlayerGroup players;
    private Deck deck;
    private Pile pile;

    public HandDistributionProtocol(CommunicationStrategy comm, Deck d, Pile pile, PlayerGroup players) {
        this.deck = d;
        this.communicator = comm;
        this.players = players;
        this.pile = pile;
    }

    public void distributeInitialCards() {
        // take the cards off the deck
        for (Player player : players.getPlayers()) {
            for (int i = 0; i < Hand.CARDS_PER_HAND; i++) {
                player.drawCard(deck);
            }
        }

        // take off one more card and put it on the (currently empty) pile
        pile.addCard(deck.drawCard());

        // then distribute the keys so everyone can decrypt their own hand
        for (Player player : players.getPlayers()) {
            if (player.equals(players.getMe()))
                receiveInitialKeys();
            else
                sendInitialKeys(player);
        }

        // decrypt the pile card, and also decrypt our own hand
        pile.getCard(0).decryptWithMyKey();

        // apply my own key to every card that's been drawn
        for (Player p : players.getPlayers())
            for (Card card : p.getHand().getCards())
                card.decryptWithMyKey();
    }

    // sends num_players*hand_size + 1 keys to 'player'
    // so that we send the keys needed to decrypt every hand but our own,
    // and also the initial card for the Pile
    private void sendInitialKeys(Player recipient) {
        ArrayList<CryptoKey> keys = new ArrayList<>();

        // send keys for others' hands
        for (Player player : players.getPlayers()) {
            for (Card card : player.getHand().getCards()) {
                if (player.equals(players.getMe()))
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
        for (int i = 0; i < players.getPlayers().size() - 1; i++) {
            List<CryptoKey> keys = (List<CryptoKey>) communicator.receiveObject();

            int keyIndex = 0;
            for (Player player : players.getPlayers()) {
                for (Card card : player.getHand().getCards()) {
                    CryptoKey key = keys.get(keyIndex++);
                    if (key != null)
                        card.decrypt(key);
                }
            }

            pile.getCard(0).decrypt(keys.get(keyIndex));
        }
    }
}
