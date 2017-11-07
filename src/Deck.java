import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards;

    public Card getCard(int index){
        return cards.get(index);
    }

    // should generate a Deck of unencrypted cards -- one for each number and color.
    public static Deck generatePlainDeck() {
        ArrayList<Card> tmp = new ArrayList<>();
        for (int card_value = 0; card_value < Card.NUM_CARDS; card_value++) {
            Card card = new RegularCard(BigInteger.valueOf(card_value));
            tmp.add(card);
        }
        return new Deck(tmp);
    }

    public Deck(List<Card> cards) {
        this.cards = cards;
    }

    public void encryptWithSingleKey(CryptoKey k_i) {
        for (Card card : cards) {
            card.encrypt(k_i);
        }
    }

    public void shuffle() {
        // NOTE: this must be computed cryptographically securely!!!
        Collections.shuffle(cards, new SecureRandom());
    }

    // encrypts every card in the deck with its own key
    public void encryptWithMultipleKeys() {
        for(Card card : cards) {
            card.encryptWithNewKey();
        }
    }

    public void decrypt(CryptoKey k_i) {
        for (Card card : cards) {
            card.decrypt(k_i);
        }
    }

    public static Deck fromIntList(List<BigInteger> intList) {
        ArrayList<Card> tmp = new ArrayList<>();
        for (int i = 0; i < Card.NUM_CARDS; i++) {
            BigInteger value = intList.get(i);
            Card card = new RegularCard(value);
            tmp.add(card);
        }
        return new Deck(tmp);
    }

    public ArrayList<BigInteger> asIntList() {
        ArrayList<BigInteger> result = new ArrayList<>();
        for(Card card : cards) {
            result.add(card.getValue());
        }
        return result;
    }

    public void updateCards(List<BigInteger> intList) {
        for (int i = 0; i < Card.NUM_CARDS; i++) {
            BigInteger value = intList.get(i);
            Card card = cards.get(i);
            card.setValue(value);
        }
    }

    public String toString() {
        String result = "";
        for (Card c : cards) {
            result += "Card: " + c.getValue() + ",";
        }
        return result;
    }

    // takes a card out of the deck, removing it.
    public Card drawCard() {
        return cards.remove(0);
    }

    public void decryptAllCardsWithMyKey() {
        for (Card c : cards)
            c.decryptWithMyKey();
    }
}
