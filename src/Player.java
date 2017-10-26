public abstract class Player {
    abstract Move receiveMove(Communicator communicator);

    private Hand hand;

    public Player () {
        hand = new Hand();
    }

    public void addCardToHand(Card card) {
        hand.addCard(card);
    }


    public void drawCard(Deck deck) {
        Card card = deck.drawCard();
        addCardToHand(card);
    }

    public Hand getHand() {
        return hand;
    }

    public PeerInfo getPeerInfo() {
        return null;
    }
}
