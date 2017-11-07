public class ChangeColorCard extends ActionCard {

    CardColor color;

    public ChangeColorCard(ActionCardTarget actionTarget, CardHandlingStrategy chs,
                           CommunicationStrategy cs, CardColor color) {
        super(actionTarget, chs, cs, color);
    }

    @Override
    public void performAction() {
        Player currentPlayer = actionTarget.getCurrentPlayer();
        color = communicator.getColorFromPlayer(currentPlayer);
    }

    public CardColor getColor() {
        return color;
    }
}
