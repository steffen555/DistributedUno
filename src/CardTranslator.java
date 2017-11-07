import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

public class CardTranslator {

    private static int cardToInt(Card card) {
        if (card instanceof RegularCard) {
            int result = (((RegularCard) card).getNumber() + card.getColor().ordinal()*10);
            System.out.println("cardToInt: " + result);
            System.out.println("The color is: " + card.getColor().ordinal());
            System.out.println("The number is: " + ((RegularCard) card).getNumber());
            return result;
        }
        else
            // TODO: pick numbers for action cards.
            throw new NotImplementedException();

    }

    public static BigInteger cardToValue(Card card) {
        return BigInteger.valueOf(cardToInt(card));
    }

    public static Card valueToCard(BigInteger value) {
        int v = value.intValue();
        int number = v % Card.NUM_CARDS_PER_COLOR;
        int index = v / Card.NUM_CARDS_PER_COLOR;

        System.out.println("valueToCard: " + value);
        CardColor color = CardColor.values()[index];
        return new RegularCard(color, number);
    }
}
