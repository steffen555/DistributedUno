import java.util.ArrayList;
import java.util.List;

public class CardPrinter {

    final static String RED = "\033[0;31m";
    final static String GREEN = "\033[0;32m";
    final static String YELLOW = "\033[0;33m";
    final static String BLUE = "\033[0;34m";
    final static String NO_COLOR = "\033[0m";

    private static String stringify(CardColor c) {
        if (c == CardColor.RED)
            return RED;
        if (c == CardColor.GREEN)
            return GREEN;
        if (c == CardColor.YELLOW)
            return YELLOW;
        if (c == CardColor.BLUE)
            return BLUE;
        return "";

    }

    private static void printCardAsASCII(Card card, List<String> out, int number) {
        String color;
        if (card instanceof EncryptedCard) {
            color = "";
        } else {
            color = stringify(card.getColor());
        }

        // TODO: find better symbols...
        String symbol;
        if (card instanceof EncryptedCard) {
            symbol = "?";
        } else if (card instanceof RegularCard) {
            symbol = Integer.toString(((RegularCard) card).getNumber());
        } else if (card instanceof ChangeTurnDirectionCard) {
            symbol = "⇄";
        } else if (card instanceof SkipCard) {
            symbol = "⚔";
        } else if (card instanceof ChangeColorCard) {
            symbol = "⚛";

        } else {
            System.out.println("weird card: " + card);
            symbol = "TODO";
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
