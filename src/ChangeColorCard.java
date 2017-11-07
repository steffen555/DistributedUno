public class ChangeColorCard extends ActionCard {

    Card.Color color;

    public ChangeColorCard(ActionCardTarget actionTarget, CardHandlingStrategy chs, CommunicationStrategy cs) {
        super(actionTarget, chs, cs);
    }

    @Override
    public void performAction() {
        Player currentPlayer = actionTarget.getCurrentPlayer();
        color = communicator.getColorFromPlayer(currentPlayer);
    }

    public Card.Color getColor() {
        return color;
    }
}
