public class ChangeTurnDirectionCard extends ActionCard {

    public ChangeTurnDirectionCard(ActionCardTarget actionTarget, CardHandlingStrategy chs, CommunicationStrategy cs) {
        super(actionTarget, chs, cs);
    }

    public void performAction() {
        actionTarget.changeTurnDirection();
    }

}
