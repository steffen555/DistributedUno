import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

public abstract class ActionCard extends Card {

    ActionCardTarget actionTarget;
    CardHandlingStrategy cardHandlingStrategy;
    CommunicationStrategy communicator;

    public ActionCard(ActionCardTarget actionTarget, CardHandlingStrategy chs, CommunicationStrategy cs, CardColor color) {
        super(color);
        this.actionTarget = actionTarget;
        this.cardHandlingStrategy = chs;
        this.communicator = cs;
    }

    public EncryptedCard encrypt(CryptoKey key) {
        throw new NotImplementedException();
    }

    public EncryptedCard encryptWithNewKey() {
        throw new NotImplementedException();
    }

    public CardRepresentation toRepresentation() {
        throw new NotImplementedException();
    }

    public abstract void performAction();

}
