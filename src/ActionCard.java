import java.math.BigInteger;

public abstract class ActionCard extends Card {

    ActionCardTarget actionTarget;
    CardHandlingStrategy cardHandlingStrategy;
    CommunicationStrategy communicator;

    public ActionCard(ActionCardTarget actionTarget, CardHandlingStrategy chs, CommunicationStrategy cs) {
        super();
        this.actionTarget = actionTarget;
        this.cardHandlingStrategy = chs;
        this.communicator = cs;
    }

    public abstract void performAction();

    public ActionCard(BigInteger value) {
        // TODO fix this; value doesn't really make sense for action cards.
        super(value);
    }
}
