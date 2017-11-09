public class ChangeTurnDirectionCard extends ActionCard {

    public ChangeTurnDirectionCard(ActionCardTarget actionTarget, CardColor color) {
        super(actionTarget, color);
    }

    public void performAction() {
        getActionTarget().changeTurnDirection();
    }

}
