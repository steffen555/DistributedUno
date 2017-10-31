import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public abstract class Card implements Comparable<Card> {

    public enum Color {
        RED, YELLOW, GREEN, BLUE
    }

    public static List<String> colorsAsString = Arrays.asList(
            "red", "yellow", "green", "blue"
    );

    public static int NUM_COLORS = 4;
    public static int NUM_CARDS_PER_COLOR = 10;
    public static int NUM_CARDS = NUM_CARDS_PER_COLOR * NUM_COLORS;

    private BigInteger value; // only retrieve this through its getter!!
    private CryptoKey myKey;

    public Card(BigInteger value) {
        this.value = value;
    }

    public void encrypt(CryptoKey key) {
        // System.out.println("Encrypting " + getValue() + " with key: " + key);
        value = CryptoScheme.encrypt(key, value);
    }

    public void decrypt(CryptoKey key) {
        // System.out.println("Decrypting " + getValue() + " with key: " + key);
        value = CryptoScheme.decrypt(key, value);
    }

    public void encryptWithNewKey() {
        myKey = CryptoScheme.generateKey();
        encrypt(myKey);
    }

    public void decryptWithMyKey() {
        decrypt(myKey);
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

    public Color getColor() {
        int index = getValue().intValue() / NUM_CARDS_PER_COLOR;
        return Color.values()[index];
    }

    public int getNumber() {
        return getValue().intValue() % NUM_CARDS_PER_COLOR;
    }

    public String toString() {
        if (getValue().intValue() < 0 || getValue().intValue() >= NUM_CARDS_PER_COLOR * NUM_COLORS)
            return "<encrypted card>"; // not fully decrypted yet..
        String color = colorsAsString.get(getColor().ordinal());
        return color + " " + getNumber();
    }

    @Override
    public int compareTo(Card card) {
        return getValue().compareTo(card.getValue());
    }
}
