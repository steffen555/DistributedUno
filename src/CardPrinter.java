import java.util.ArrayList;
import java.util.List;

public class CardPrinter {

    final static String RED = "\033[0;31m";
    final static String GREEN = "\033[0;32m";
    final static String YELLOW = "\033[0;33m";
    final static String BLUE = "\033[0;34m";
    final static String NO_COLOR = "\033[0m";

    private static String stringify(Card.Color c) {
        if (c == Card.Color.RED)
            return RED;
        if (c == Card.Color.GREEN)
            return GREEN;
        if (c == Card.Color.YELLOW)
            return YELLOW;
        if (c == Card.Color.BLUE)
            return BLUE;
        return "";

    }

    private static void printCardAsASCII(Card card, List<String> out, int number) {
        String symbol;
        String color;
        if (card.isEncrypted()) {
            symbol = "?";
            color = "";
        } else {
            symbol = Integer.toString(card.getNumber());
            color = stringify(card.getColor());
        }


        out.set(0, out.get(0) + color + " ___ " + NO_COLOR);
        out.set(1, out.get(1) + color + "|   |" + NO_COLOR);
        out.set(2, out.get(2) + color + "| " + symbol + " |" + NO_COLOR);
        out.set(3, out.get(3) + color + "|___|" + NO_COLOR);

        if (number != -1)
            out.set(4, out.get(4) + "  " + Integer.toString(number) + "  ");
    }

    public static void doPrintCards(List<Card> cards, boolean printNumbers) {
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < 5; i++)
            out.add("");

        for (int i = 0; i < cards.size(); i++) {
            if (printNumbers)
                printCardAsASCII(cards.get(i), out, i);
            else
                printCardAsASCII(cards.get(i), out, -1);
        }

        for (String s : out)
            System.out.println(s);
    }

    public static void printCards(List<Card> cards) {
        doPrintCards(cards, true);
    }

    public static void printCard(Card card) {
        ArrayList<Card> l = new ArrayList<Card>();
        l.add(card);
        doPrintCards(l, false);
    }
}
