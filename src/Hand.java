import java.util.ArrayList;
import java.util.List;

public class Hand {

    public static final int CARDS_PER_HAND = 7;

    private List<Card> cards;

    public Hand() {
        cards = new ArrayList<Card>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) { cards.remove(card); }

    public List<Card> getCards() {
        return cards;
    }
}
