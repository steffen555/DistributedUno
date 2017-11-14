public class DrawFourAndChangeColorCard extends ActionCard {
    public DrawFourAndChangeColorCard(ActionCardTarget actionTarget, CardColor color) {
        super(actionTarget, color);
    }

    @Override
    public void performAction() {
        setColor(getActionTarget().getColorFromCurrentPlayer());
        getActionTarget().drawCardsFromDeckForNextPlayer(4);
    }

}
