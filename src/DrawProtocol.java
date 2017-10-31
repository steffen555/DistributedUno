public class DrawProtocol {

    CommunicationStrategy comm;
    PlayerGroup players;
    Deck deck;
    Card drawnCard;

    public DrawProtocol(CommunicationStrategy comm, PlayerGroup players, Deck deck) {
        this.comm = comm;
        this.players = players;
        this.deck = deck;
    }

    public void drawCardForCurrentPlayer() {
        // we are dealing with the top card in the deck.
        Player playerInTurn = players.getPlayerInTurn();
        drawnCard = playerInTurn.drawCard(deck);

        // every player should send their key for this card to every other player,
        // with the exception that the player who is drawing this card should not
        // share his keys.
        for (Player p : players.getPlayers()) {
            if (p.equals(players.getMe())) {
                // my turn to receive keys from everyone else
                receiveKeys();
            }
            else {
                // send the key to the guy who's receiving keys from everyone else
                // (unless we're the one drawing the card)
                if (!players.myTurn())
                    sendKey(p);
            }
        }

        drawnCard.decryptWithMyKey();
    }

    // send my key for the drawn card to player p.
    private void sendKey(Player p) {
        comm.sendObjectToPlayer(p, drawnCard.getMyKey());
    }

    // receive keys from every player except for the player in turn and ourselves.
    private void receiveKeys() {
        // TODO: remember we don't get from 1 guy
        int numKeys = players.getPlayers().size() - 1;
        if (!players.myTurn())
            numKeys--; // we don't get a key from the player in turn either.

        for (int i = 0; i < numKeys; i++)
            drawnCard.decrypt((CryptoKey) comm.receiveObject());
    }
}
