public class DrawFourAndChangeColorCard extends ActionCard {
    public DrawFourAndChangeColorCard(ActionCardTarget actionTarget, CardColor color) {
        super(actionTarget, color);
    }

    @Override
    public void performAction() {
        for (int i = 0; i < 4; i++)
            getActionTarget().drawCardFromDeckForNextPlayer();

        // TODO: remember to switch color!!

    }

}
