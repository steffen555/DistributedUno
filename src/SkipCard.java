public class SkipCard extends ActionCard {

    public SkipCard(ActionCardTarget actionTarget, CardHandlingStrategy chs, CommunicationStrategy cs) {
        super(actionTarget, chs, cs);
    }

    @Override
    public void performAction() {
        actionTarget.advanceTurn();
    }
}
