import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class Card {

    public static enum Color {
        RED, YELLOW, GREEN, BLUE;
    };

    public static int NUM_COLORS = 4;
    public static int NUM_CARDS_PER_COLOR = 10;
    public static int NUM_CARDS = NUM_CARDS_PER_COLOR * NUM_COLORS;

    private BigInteger value; // only retrieve this through its getter!!
    private CryptoKey myKey;

    public Card (BigInteger value) {
        this.value = value;
    }

    public void encrypt(CryptoKey key) {
        value = CryptoScheme.encrypt(key, value);
    }

    public void decrypt(CryptoKey key) {
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

}
