public abstract class Player {
    abstract Move receiveMove(Communicator communicator);

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
        if (this instanceof LocalPlayer && p instanceof LocalPlayer)
            return true;
        if (!(this instanceof RemotePlayer && p instanceof RemotePlayer))
            return false;

        return getPeerInfo().equals(p.getPeerInfo());
    }

}
