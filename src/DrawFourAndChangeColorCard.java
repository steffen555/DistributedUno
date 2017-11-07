public class DrawFourAndChangeColorCard extends ActionCard {
    public DrawFourAndChangeColorCard(ActionCardTarget actionTarget, CardHandlingStrategy chs,
                                      CommunicationStrategy cs, CardColor color) {
        super(actionTarget, chs, cs, color);
    }

    @Override
    public void performAction() {
        Player target = actionTarget.getNextPlayer();
        for (int i = 0; i < 4; i++)
            cardHandlingStrategy.drawCardFromDeckForPlayer(target);
    }
}
