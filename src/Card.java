import java.util.List;

public abstract class Card {

    public static int NUM_COLORS = 4;
    public static int NUM_CARDS_PER_COLOR = 10;
    public static int NUM_CARDS = NUM_CARDS_PER_COLOR * NUM_COLORS;

    private List<CryptoKey> keys;

    public void encrypt(CryptoKey k_i) {
        keys.add(k_i);
        // TODO: actually encrypt the card.
    }

    public void encryptWithNewKey() {
        // TODO: make a new crypto key, put it in keys, encrypt with it.
    }

    public static enum Color {
        RED, YELLOW, GREEN, BLUE;
    };

    private int value;

    public Card (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int asInt() {
        return value;
    }

    public Color getColor() {
        return Color.values()[value/NUM_CARDS_PER_COLOR];
    }


}
