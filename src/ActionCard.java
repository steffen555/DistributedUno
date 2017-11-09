import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

public abstract class ActionCard extends Card {

    public ActionCard(ActionCardTarget actionTarget, CardColor color) {
        super(color, actionTarget);
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

    public CardRepresentation toRepresentation() {
        return new CardRepresentation(0, getValue());
    }

    public abstract void performAction();

}
