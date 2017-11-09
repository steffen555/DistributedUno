public class SkipCard extends ActionCard {

    public SkipCard(ActionCardTarget actionTarget, CardColor color) {
        super(actionTarget, color);
    }

    @Override
    public void performAction() {
        getActionTarget().advanceTurn();
    }
}
