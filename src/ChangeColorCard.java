public class ChangeColorCard extends ActionCard {

    CardColor color;

    public ChangeColorCard(ActionCardTarget actionTarget, CardColor color) {
        super(actionTarget, color);
    }

    @Override
    public void performAction() {
        color = getActionTarget().getColorFromCurrentPlayer();
    }

    public CardColor getColor() {
        return color;
    }
}
