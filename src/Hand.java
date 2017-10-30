import java.util.ArrayList;
import java.util.Collections;
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

    public void removeCard(int cardIndex) { cards.remove(cardIndex); }

    public List<Card> getCards() {
        return cards;
    }

    public String toString() {
        String result = "";

        List<Card> sorted = new ArrayList<Card>(cards);
        Collections.sort(sorted);

        for (Card card : sorted)
            result += card + "\n";
        return result;
    }
}
