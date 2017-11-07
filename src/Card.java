import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public abstract class Card implements Comparable<Card> {

    public static List<String> colorsAsString = Arrays.asList(
            "red", "yellow", "green", "blue"
    );

    public static int NUM_COLORS = 4;
    public static int NUM_CARDS_PER_COLOR = 10;
    public static int NUM_CARDS = NUM_CARDS_PER_COLOR * NUM_COLORS;

    private BigInteger value; // only retrieve this through its getter!!
    private CryptoKey myKey;
    private CardColor color;

    public Card(CardColor color) {
        this.color = color;
    }

    public void encrypt(CryptoKey key) {
        // System.out.println("Encrypting " + getValue() + " with key: " + key);
        value = CryptoScheme.encrypt(key, value);
    }

    public void encryptWithNewKey() {
        myKey = CryptoScheme.generateKey();
        encrypt(myKey);
    }

    public CryptoKey getMyKey() {
        return myKey;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }

    public CardColor getColor() {
        return color;
    }

    public String toString() {
        return getValue().toString();
    }

    @Override
    public int compareTo(Card card) {
        return getValue().compareTo(card.getValue());
    }
}
