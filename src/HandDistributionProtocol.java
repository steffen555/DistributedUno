import com.sun.org.apache.xml.internal.security.encryption.EncryptedType;

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

        // we must calculate the keys before we start receiving keys from others,
        // otherwise our card might turn into an unencrypted card, at which
        // point we'd lose the key we want to broadcast.
        ArrayList<CryptoKey> keys = generateKeysList();

        // then distribute the keys so everyone can decrypt their own hand
        for (Player player : players) {
            if (player.getPeerInfo() == null) // If the player is a local player
                receiveInitialKeys();
            else
                sendInitialKeys(player, keys);
        }
    }

    private ArrayList<CryptoKey> generateKeysList() {
        ArrayList<CryptoKey> keys = new ArrayList<>();

        // send keys for others' hands
        for (Player player : players) {
            for (Card card : player.getHand().getCards()) {
                if (player.getPeerInfo() == null) {
                    keys.add(null); // don't send our own keys
                }
                else {
                    // since these are cards from another player's hand,
                    // they are necessarily EncryptedCards
                    keys.add(((EncryptedCard) card).getMyKey());
                }
            }
        }

        // also send one more key for the initial pile card
        EncryptedCard pileCard = (EncryptedCard) pile.getCard(0);
        keys.add(pileCard.getMyKey());

        return keys;
    }

    // sends num_players*hand_size + 1 keys to 'player'
    // so that we send the keys needed to decrypt every hand but our own,
    // and also the initial card for the Pile
    private void sendInitialKeys(Player recipient, ArrayList<CryptoKey> keys) {
        communicator.sendObjectToPlayer(recipient, keys);
    }

    private void receiveInitialKeys() {
        // receive from each other player in turn.
        for (int i = 0; i < players.size() - 1; i++) {
            List<CryptoKey> keys = (List<CryptoKey>) communicator.receiveObject(List.class);

            int keyIndex = 0;
            for (Player player : players) {
                for (int j = 0; j < player.getHand().getCards().size(); j++) {
                    Card card = player.getHand().getCard(j);
                    CryptoKey key = keys.get(keyIndex++);
                    if (key == null)
                        continue;
                    Card decrypted = ((EncryptedCard) card).decrypt(key);
                    player.getHand().setCard(j, decrypted);
                }
            }

            // decrypt the pile card
            EncryptedCard pileCard = (EncryptedCard) pile.getTopCard();
            Card decryptedPileCard = pileCard.decrypt(keys.get(keyIndex));
            pile.setTopCard(decryptedPileCard);
        }
    }
}
