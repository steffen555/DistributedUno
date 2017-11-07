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
        // TODO: add action cards, too
        ArrayList<Card> tmp = new ArrayList<>();
        for (CardColor color : CardColor.values()) {
            if (color == CardColor.NO_COLOR)
                continue;
            for (int i = 0; i < Card.NUM_CARDS_PER_COLOR; i++)
                tmp.add(new RegularCard(color, i));
        }
        return new Deck(tmp);
    }

    public Deck(List<Card> cards) {
        this.cards = cards;
    }

    public void encryptWithSingleKey(CryptoKey k_i) {
        for (int i = 0; i < cards.size(); i++)
            cards.set(i, cards.get(i).encrypt(k_i));
    }

    public void shuffle() {
        // NOTE: this must be computed cryptographically securely!!!
        Collections.shuffle(cards, new SecureRandom());
    }

    // encrypts every card in the deck with its own key
    public void encryptWithMultipleKeys() {
        for (int i = 0; i < cards.size(); i++)
            cards.set(i, cards.get(i).encryptWithNewKey());
    }

    public void decrypt(CryptoKey k_i) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card instanceof EncryptedCard) {
                EncryptedCard ec = (EncryptedCard) card;
                cards.set(i, ec.decrypt(k_i));
            }
        }
    }

    public static Deck fromRepresentationList(List<CardRepresentation> reprs) {
        ArrayList<Card> tmp = new ArrayList<>();
        for (CardRepresentation repr : reprs)
            tmp.add(repr.toCard());
        return new Deck(tmp);
    }

    public ArrayList<CardRepresentation> asRepresentationList() {
        ArrayList<CardRepresentation> result = new ArrayList<>();
        for(Card card : cards)
            result.add(card.toRepresentation());
        return result;
    }

    public void updateCards(List<CardRepresentation> reprs) {
        // our cards should be encrypted at this point.
        for (int i = 0; i < cards.size(); i++) {
            EncryptedCard oldCard = (EncryptedCard) cards.get(i);
            EncryptedCard newCard = (EncryptedCard) reprs.get(i).toCard();
            newCard.setMyKey(oldCard.getMyKey());
            cards.set(i, newCard);
        }
    }

    // takes a card out of the deck, removing it.
    public Card drawCard() {
        return cards.remove(0);
    }

    public void decryptAllCardsWithMyKey() {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card instanceof EncryptedCard) {
                EncryptedCard ec = (EncryptedCard) card;
                cards.set(i, ec.decryptWithMyKey());
            }
        }
    }

}
