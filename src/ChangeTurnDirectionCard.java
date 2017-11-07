public class ChangeTurnDirectionCard extends ActionCard {

    public ChangeTurnDirectionCard(ActionCardTarget actionTarget, CardHandlingStrategy chs,
                                   CommunicationStrategy cs, CardColor color) {
        super(actionTarget, chs, cs, color);
    }

    public void performAction() {
        actionTarget.changeTurnDirection();
    }

}
