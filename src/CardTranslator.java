import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class CardTranslator {

    public CardColor getColor(BigInteger value) {
        int index = (value.intValue() - 2) / Card.NUM_CARDS_PER_COLOR;
        return CardColor.values()[index];
    }

    public int getNumber(BigInteger value) {
        return (value.intValue() - 2) % Card.NUM_CARDS_PER_COLOR;
    }

    public boolean isEncrypted(BigInteger value) {
        return value.intValue() < 0 || value.intValue() >= Card.NUM_CARDS_PER_COLOR * Card.NUM_COLORS + 2;
    }

    public Card translateValueToCard(BigInteger value) {
        Card card;
        if (isEncrypted(value)) {
            card = new EncryptedCard(value, CardColor.NO_COLOR);
        } else {
            card = new RegularCard(getColor(value), getNumber(value));
        }
        return card;
    }

    public BigInteger translateCardToValue(Card card) {
        return new BigInteger("0");
    }
}
