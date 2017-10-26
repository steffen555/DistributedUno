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

    private List<CryptoKey> keys;
    private BigInteger value;

    public Card (BigInteger value) {
        this.value = value;
        this.keys = new ArrayList<CryptoKey>();
    }

    public void encrypt(CryptoKey k_i) {
        keys.add(k_i);
        value = CryptoScheme.encrypt(k_i, value);
    }

    public void decrypt(CryptoKey k_i) {
        value = CryptoScheme.decrypt(k_i, value);
    }

    public void encryptWithNewKey() {
        CryptoKey key = CryptoScheme.generateKey();
        encrypt(key);
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }

    public Color getColor() {
        int index = value.intValue() / NUM_CARDS_PER_COLOR;
        return Color.values()[index];
    }

}
