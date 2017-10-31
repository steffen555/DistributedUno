public abstract class Player {
    abstract Move receiveMove(DistributedCommunicationStrategy communicator);

    private Hand hand;

    public Player () {
        hand = new Hand();
    }

    public void addCardToHand(Card card) {
        hand.addCard(card);
    }


    public Card drawCard(Deck deck) {
        Card card = deck.drawCard();
        addCardToHand(card);
        return card;
    }

    public Hand getHand() {
        return hand;
    }

    public PeerInfo getPeerInfo() {
        return null;
    }

    public boolean equals(Player p) {
        if (getPeerInfo() == null && p.getPeerInfo() == null) {
            return true;
        }
        return !(getPeerInfo() == null || p.getPeerInfo() == null) && getPeerInfo().equals(p.getPeerInfo());

    }

}
