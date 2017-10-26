import java.util.ArrayList;

public class Deck {

    private ArrayList<Card> cards;

    public Card getCard(int index){
        return cards.get(index);
    }

    // should generate a Deck of unencrypted cards -- one for each number and color.
    public static Deck generatePlainDeck() {
        ArrayList<Card> tmp = new ArrayList<Card>();
        for (int card_value = 0; card_value < Card.NUM_CARDS; card_value++) {
            Card card = new RegularCard(card_value);
            tmp.add(card);
        }
        return new Deck(tmp);
    }

    public Deck(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void encryptWithSingleKey(CryptoKey k_i) {
        for (Card card : cards) {
            card.encrypt(k_i);
        }
    }

    public void shuffle() {
        // TODO
        // NOTE: this must be cryptographically secure!!!
    }

    // encrypts every card in the deck with its own key
    public void encryptWithMultipleKeys() {
        for(Card card : cards) {
            card.encryptWithNewKey();
        }
    }

    public void decrypt(CryptoKey k_i) {
        // TODO: decrypt every card with k_i
    }
}
