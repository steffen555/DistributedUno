import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.math.BigInteger;

// we pack cards into this type of class before sending them on the network
// because then we are sure that we do not accidentally transmit any keys.
public class CardRepresentation implements Serializable {

    private final BigInteger value;
    private final int encryptionCounter;

    public CardRepresentation(int encryptionCounter, BigInteger value) {
        this.value = value;
        this.encryptionCounter = encryptionCounter;
    }

    public Card toCard(ActionCardTarget act) {
        if (encryptionCounter == 0) {
            return CardTranslator.valueToCard(value, act);
        }
        else {
            return new EncryptedCard(act, value, encryptionCounter);
        }
    }

    public BigInteger getValue() {
        return value;
    }

}
