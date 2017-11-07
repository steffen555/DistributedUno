public class DrawTwoCard extends ActionCard {

    public DrawTwoCard(ActionCardTarget actionTarget, CardHandlingStrategy chs,
                       CommunicationStrategy cs, CardColor color) {
        super(actionTarget, chs, cs, color);
    }

    @Override
    public void performAction() {
        Player target = actionTarget.getNextPlayer();
        cardHandlingStrategy.drawCardFromDeckForPlayer(target);
        cardHandlingStrategy.drawCardFromDeckForPlayer(target);
    }
}
