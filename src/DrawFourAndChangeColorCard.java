public class DrawFourAndChangeColorCard extends ActionCard {
    public DrawFourAndChangeColorCard(ActionCardTarget actionTarget, CardColor color) {
        super(actionTarget, color);
    }

    @Override
    public void performAction() {
        getActionTarget().drawCardsFromDeckForNextPlayer(4);

        // TODO: remember to switch color!!

    }

}
