import java.util.ArrayList;
import java.util.List;


public class CardPrinter {

    final public static String RED = "\033[0;31m";
    final public static String GREEN = "\033[0;32m";
    final public static String YELLOW = "\033[0;33m";
    final public static String BLUE = "\033[0;34m";
    final public static String NO_COLOR = "\033[0m";
    final public static String BOLD = "\033[1m";

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

    private static void printCardAsASCII(Card card, MultiLinePrinter printer, int number) {
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
            symbol = "d"; //⇄
        } else if (card instanceof SkipCard) {
            symbol = "s"; //⊘  ☹
        } else if (card instanceof ChangeColorCard) {
            symbol = "c"; //⚛
        } else if (card instanceof DrawTwoCard) {
            symbol = "t"; //②
        } else if (card instanceof DrawFourAndChangeColorCard) {
            symbol = "f"; //④
        } else {
            System.out.println("weird card: " + card);
            symbol = "TODO";
        }

        printer.print(0, color + " ___ " + NO_COLOR);
        printer.print(1, color + "|   |" + NO_COLOR);
        printer.print(2, color + "| " + symbol + " |" + NO_COLOR);
        printer.print(3, color + "|___|" + NO_COLOR);

        if (number != -1) {
            int numberOfDigits = ((Integer) number).toString().length();
            String spaceBefore = "  ";
            String spaceAfter = "  ";
            if (numberOfDigits == 2)
                spaceAfter = " ";
            if (numberOfDigits == 3) {
                spaceBefore = " ";
                spaceAfter = " ";
            }
            printer.print(4, spaceBefore + Integer.toString(number) + spaceAfter);
        }
    }

    public static void doPrintCards(List<Card> cards, MultiLinePrinter printer, boolean printNumbers) {
        for (int i = 0; i < cards.size(); i++) {
            if (printNumbers)
                printCardAsASCII(cards.get(i), printer, i);
            else
                printCardAsASCII(cards.get(i), printer, -1);
        }
    }

    public static MultiLinePrinter printCards(List<Card> cards) {
        MultiLinePrinter result = new MultiLinePrinter();
        doPrintCards(cards, result, true);
        return result;
    }

    public static void printCard(Card card) {
        MultiLinePrinter result = new MultiLinePrinter();
        ArrayList<Card> l = new ArrayList<Card>();
        l.add(card);
        doPrintCards(l, result, false);
        System.out.println(result.getOutput());
    }
}

class MultiLinePrinter {

    ArrayList<String> out;
    final String NEWLINE = System.getProperty("line.separator");

    public MultiLinePrinter() {
        out = new ArrayList<>();
    }

    public void print(int lineNumber, String s) {
        while (out.size() <= lineNumber) {
            out.add("");
        }

        out.set(lineNumber, out.get(lineNumber) + s);
    }

    public String getOutput() {
        String result = "";
        for (String s : out)
            result += s + NEWLINE;
        return result;
    }

}
