public class DrawTwoCard extends ActionCard {

    public DrawTwoCard(ActionCardTarget actionTarget, CardColor color) {
        super(actionTarget, color);
    }

    @Override
    public void performAction() {
        getActionTarget().drawCardFromDeckForNextPlayer();
    }
}
