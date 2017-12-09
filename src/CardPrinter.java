import java.util.ArrayList;
import java.util.List;


public class CardPrinter {

    private static int LINE_MAX_WIDTH = 60;

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


        String symbol;
        if (card instanceof EncryptedCard) {
            symbol = "?";
        } else if (card instanceof RegularCard) {
            symbol = Integer.toString(((RegularCard) card).getNumber());
        } else if (card instanceof ChangeTurnDirectionCard) {
            if (System.getProperty("os.name").startsWith("Windows"))
                symbol = "d";
            else
                symbol = "⇄";
        } else if (card instanceof SkipCard) {
            if (System.getProperty("os.name").startsWith("Windows"))
                symbol = "s";
            else
                symbol = "⊘";
        } else if (card instanceof ChangeColorCard) {
            if (System.getProperty("os.name").startsWith("Windows"))
                symbol = "c";
            else
                symbol = "\u269B";
        } else if (card instanceof DrawTwoCard) {
            if (System.getProperty("os.name").startsWith("Windows"))
                symbol = "t";
            else
                symbol = "②";
        } else if (card instanceof DrawFourAndChangeColorCard) {
            if (System.getProperty("os.name").startsWith("Windows"))
                symbol = "f";
            else
                symbol = "④";
        } else {
            System.out.println("weird card: " + card);
            symbol = "?";
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
        MultiLinePrinter result = new MultiLinePrinter(LINE_MAX_WIDTH);
        doPrintCards(cards, result, true);
        return result;
    }

    public static MultiLinePrinter printCard(Card card) {
        MultiLinePrinter result = new MultiLinePrinter(LINE_MAX_WIDTH);
        ArrayList<Card> l = new ArrayList<Card>();
        l.add(card);
        doPrintCards(l, result, false);
        return result;
    }
}

class MultiLinePrinter {

    ArrayList<String> out;
    final String NEWLINE = System.getProperty("line.separator");

    // this is only approximate
    final int maxWidth;

    int lineOffset = 0;
    int lineAddend = 0;

    public MultiLinePrinter(int maxWidth) {
        out = new ArrayList<>();
        this.maxWidth = maxWidth;
    }

    public void print(int lineNumber, String s) {
        lineNumber += lineOffset;
        while (out.size() <= lineNumber) {
            out.add("");
        }

        String oldLine = out.get(lineNumber);
        String newLine = oldLine + s;

        if (getLength(newLine) <= maxWidth) {
            out.set(lineNumber, newLine);
            return;
        }

        if (lineAddend == 0)
            lineAddend = out.size();

        lineOffset += lineAddend;

        print(lineNumber, s);

    }

    // needed to filter out the characters that color/embolden the line
    private int getLength(String s) {
        String filtered = s.replaceAll("\033\\[\\d.*?m", "");
        return filtered.length();
    }

    public void printWithoutWrapping(int lineNumber, String s) {
        while (out.size() <= lineNumber)
            out.add("");

        out.set(lineNumber, out.get(lineNumber) + s);
    }


    public String getOutput() {
        String result = "";
        for (String s : out)
            result += s + NEWLINE;
        return result;
    }

}
