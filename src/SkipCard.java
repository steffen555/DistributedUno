public class SkipCard extends ActionCard {

    public SkipCard(ActionCardTarget actionTarget, CardHandlingStrategy chs,
                    CommunicationStrategy cs, CardColor color) {
        super(actionTarget, chs, cs, color);
    }

    @Override
    public void performAction() {
        actionTarget.advanceTurn();
    }
}
