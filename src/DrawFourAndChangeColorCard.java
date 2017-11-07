public class DrawFourAndChangeColorCard extends ActionCard {
    public DrawFourAndChangeColorCard(ActionCardTarget actionTarget, CardHandlingStrategy chs, CommunicationStrategy cs) {
        super(actionTarget, chs, cs);
    }

    @Override
    public void performAction() {
        Player target = actionTarget.getNextPlayer();
        for (int i = 0; i < 4; i++)
            cardHandlingStrategy.drawCardFromDeckForPlayer(target);
    }
}
