import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards;

    public Card getCard(int index) {
        return cards.get(index);
    }

    private ActionCardTarget act;

    // should generate a Deck of unencrypted cards -- one for each number and color.
    public static Deck generatePlainDeck(ActionCardTarget actionTarget) {
        ArrayList<Card> cards = new ArrayList<>();
        for (CardColor color : CardColor.values()) {
            if (color == CardColor.NO_COLOR)
                continue;

            // add the regular cards
            for (int i = 0; i < Card.NUM_CARDS_PER_COLOR; i++)
                cards.add(new RegularCard(actionTarget, color, i));

            // add special action cards
            cards.add(new ChangeTurnDirectionCard(actionTarget, color));
            cards.add(new SkipCard(actionTarget, color));
            cards.add(new ChangeColorCard(actionTarget, CardColor.NO_COLOR));
            cards.add(new DrawTwoCard(actionTarget, color));
            cards.add(new DrawFourAndChangeColorCard(actionTarget, CardColor.NO_COLOR));
        }
        return new Deck(cards, actionTarget);
    }

    public Deck(List<Card> cards, ActionCardTarget act) {
        this.cards = cards;
        this.act = act;
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

    public static Deck fromRepresentationList(List<CardRepresentation> reprs,
                                              ActionCardTarget act) {
        ArrayList<Card> tmp = new ArrayList<>();
        for (CardRepresentation repr : reprs)
            tmp.add(repr.toCard(act));
        return new Deck(tmp, act);
    }

    public ArrayList<CardRepresentation> asRepresentationList() {
        ArrayList<CardRepresentation> result = new ArrayList<>();
        for (Card card : cards)
            result.add(card.toRepresentation());
        return result;
    }

    public void updateCards(List<CardRepresentation> reprs) {
        // our cards should be encrypted at this point.
        for (int i = 0; i < cards.size(); i++) {
            EncryptedCard oldCard = (EncryptedCard) cards.get(i);
            EncryptedCard newCard = (EncryptedCard) reprs.get(i).toCard(act);
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

    public String toString() {
        String result = "";
        for (Card c : cards) {
            result += c.encrypt(c.getMyKey()) + "\n";
            ((EncryptedCard) c).decrypt(c.getMyKey());
        }
        return result;
    }

    public void addAllCardsFromDeck(Deck deck) {
        Card c = deck.drawCard();
        while (true) {
            cards.add(c);
            try {
                c = deck.drawCard();
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public int numberOfCardsInDeck() {
        return cards.size();
    }

}
