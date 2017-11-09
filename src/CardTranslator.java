import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;
import java.rmi.UnexpectedException;

/*

Card colors:

    000 -> RED
    100 -> YELLOW
    200 -> GREEN
    300 -> BLUE
    400 -> NO_COLOR

Cards:
    0   -> 0
    1   -> 1
    ...
    9   -> 9
    10  -> SkipCard
    11  -> ChangeTurnDirectionCard
    12  -> DrawTwoCard
    13  -> ChangeColorCard
    14  -> DrawFourAndChangeColorCard

 */


public class CardTranslator {

    private static int cardToInt(Card card) {
        System.out.println("Card: " + card.getColor());
        int number = cardToNumber(card);
        int color = card.getColor().ordinal() * 100;
        return color + number;
    }

    public static BigInteger cardToValue(Card card) {
        return BigInteger.valueOf(cardToInt(card));
    }

    public static Card valueToCard(BigInteger v, ActionCardTarget act) {
        int value = v.intValue();
        if (value < 0 || value > 414)
            throw new RuntimeException("Cannot convert an encrypted value to a card.");

        int colorIndex = value / 100;
        CardColor color = CardColor.values()[colorIndex];
        int number = value - 100*colorIndex;

        if (number < 10)
            return new RegularCard(act, color, number);
        else if (number == 10)
            return new SkipCard(act, color);
        else if (number == 11)
            return new ChangeTurnDirectionCard(act, color);
        else if (number == 12)
            return new DrawTwoCard(act, color);
        else if (number == 13)
            return new ChangeColorCard(act, color);
        else if (number == 14)
            return new DrawFourAndChangeColorCard(act, color);
        else
            throw new NotImplementedException();
    }

    public static int cardToNumber(Card card) {
        int number = 0;
        if (card instanceof RegularCard)
            number = card.getNumber();
        else if (card instanceof SkipCard)
            number = 10;
        else if (card instanceof ChangeTurnDirectionCard)
            number = 11;
        else if (card instanceof DrawTwoCard)
            number = 12;
        else if (card instanceof ChangeColorCard)
            number = 13;
        else if (card instanceof DrawFourAndChangeColorCard)
            number = 14;
        else
            throw new NotImplementedException();
        return number;
    }
}
