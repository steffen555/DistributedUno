import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HandDistributionProtocol {
    private Communicator communicator;
    private PlayerGroup players;
    private Deck deck;
    private Pile pile;

    public HandDistributionProtocol(Communicator comm, Deck d, Pile pile, PlayerGroup players) {
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
                receiveInitialKeys(player);
            else
                sendInitialKeys(player);
        }

        // decrypt the pile card, and also decrypt our own hand
        pile.getCard(0).decryptWithMyKey();
        for (Card card : players.getMe().getHand().getCards())
            card.decryptWithMyKey();
    }

    // sends num_players*hand_size + 1 keys to 'player'
    // so that we send the keys needed to decrypt every hand but our own,
    // and also the initial card for the Pile
    private void sendInitialKeys(Player recipient) {
        List<CryptoKey> keys = new ArrayList<CryptoKey>();

        // send keys for others' hands
        for (Player player : players.getPlayers()) {
            if (player.equals(players.getMe()))
                continue; // do not send the keys for our hand.

            for (Card card : player.getHand().getCards())
                keys.add(card.getMyKey());
        }

        // also send one more key for the initial pile card
        keys.add(pile.getCard(0).getMyKey());

        communicator.sendObject(recipient.getPeerInfo(), keys);
    }

    private void receiveInitialKeys(Player sender) {
        List<CryptoKey> keys = null;
        try {
            keys = (List<CryptoKey>) communicator.receiveObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int keyIndex = 0;

        for (Player player : players.getPlayers()) {
            if (player.equals(sender))
                continue; // sender didn't send their own keys

            for (Card card : player.getHand().getCards())
                card.decrypt(keys.get(keyIndex++));
        }

        pile.getCard(0).decrypt(keys.get(keyIndex));
    }
}
