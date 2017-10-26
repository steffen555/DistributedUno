import java.util.List;

public class Pile {
    private List<Card> cards;
    public void addCard(Card card) {
        cards.add(card);
    }

    public Card getCard(int i) {
        return cards.get(i);
    }

    public Card getTopCard() {
        return cards.get(cards.size() - 1);
    }
}
