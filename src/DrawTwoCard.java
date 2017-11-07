public class DrawTwoCard extends ActionCard {

    public DrawTwoCard(ActionCardTarget actionTarget, CardHandlingStrategy chs, CommunicationStrategy cs) {
        super(actionTarget, chs, cs);
    }

    @Override
    public void performAction() {
        Player target = actionTarget.getNextPlayer();
        cardHandlingStrategy.drawCardFromDeckForPlayer(target);
        cardHandlingStrategy.drawCardFromDeckForPlayer(target);
    }
}
