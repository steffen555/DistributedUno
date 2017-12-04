public abstract class Player {
    abstract Move receiveMove();

    private Hand hand;
    private String name;

    public Player(String name) {
        hand = new Hand();
        this.name = name;
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
        if(p == null)
            return false;
        if (getPeerInfo() == null && p.getPeerInfo() == null) {
            return true;
        }
        return !(getPeerInfo() == null || p.getPeerInfo() == null) && getPeerInfo().equals(p.getPeerInfo());

    }

    public abstract CardColor receiveColor();

    public String getName(){
        return name;
    }
}
