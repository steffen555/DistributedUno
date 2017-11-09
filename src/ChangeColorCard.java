public class ChangeColorCard extends ActionCard {

    public ChangeColorCard(ActionCardTarget actionTarget, CardColor color) {
        super(actionTarget, color);
    }

    @Override
    public void performAction() {
        setColor(getActionTarget().getColorFromCurrentPlayer());
    }
}
