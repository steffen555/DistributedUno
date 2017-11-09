import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public abstract class Card {

    private final ActionCardTarget act;
    CryptoKey myKey;

    public ActionCardTarget getActionTarget() {
        return act;
    }

    public static List<String> colorsAsString = Arrays.asList(
            "red", "yellow", "green", "blue"
    );

    public static int NUM_COLORS = 4;
    public static int NUM_CARDS_PER_COLOR = 10;
    public static int NUM_CARDS = NUM_CARDS_PER_COLOR * NUM_COLORS;

    private CardColor color;

    public Card(CardColor color, ActionCardTarget act) {
        this.color = color;
        this.act = act;
    }

    public int getNumber() {
        return CardTranslator.cardToNumber(this);
    }

    public abstract EncryptedCard encrypt(CryptoKey key);
    public abstract EncryptedCard encryptWithNewKey();

    public abstract CardRepresentation toRepresentation();

    public CardColor getColor() {
        return color;
    }

    // we need this method in Card rather than EncryptedCard,
    // because even though a card has been fully decrypted,
    // other players may still need its key.
    public CryptoKey getMyKey() {
        return myKey;
    }

    public void setMyKey(CryptoKey key) {
        myKey = key;
    }

}
