import java.math.BigInteger;

public class RegularCard extends Card {

    private int number;

    public RegularCard(ActionCardTarget act, CardColor color, int number) {
        super(color, act);
        this.number = number;
    }

    public BigInteger getValue() {
        return CardTranslator.cardToValue(this);
    }

    public EncryptedCard encrypt(CryptoKey key) {
        BigInteger encryptedValue = CryptoScheme.encrypt(key, getValue());
        return new EncryptedCard(getActionTarget(), encryptedValue, 1);
    }

    public EncryptedCard encryptWithNewKey() {
        return new EncryptedCard(getActionTarget(), getValue());
    }

    @Override
    public CardRepresentation toRepresentation() {
        return new CardRepresentation(0, getValue());
    }

    public int getNumber() {
        return number;
    }

    public String toString() {
        String color = colorsAsString.get(getColor().ordinal());
        return color + " " + getNumber();
    }
}
