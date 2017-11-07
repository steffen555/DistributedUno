import java.util.ArrayList;
import java.util.List;

public class Pile {
    private List<Card> cards;
    public Pile () {
        cards = new ArrayList<Card>();
    }
    public void addCard(Card card) {
        cards.add(card);
    }

    public Card getCard(int i) {
        return cards.get(i);
    }

    public Card getTopCard() {
        return cards.get(cards.size() - 1);
    }

    public void setTopCard(Card c) {
        cards.set(cards.size() - 1, c);
    }
}
