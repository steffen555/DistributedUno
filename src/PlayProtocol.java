public class PlayProtocol {

//    if (players.myTurn()) {
//        comm.broadcastObject(move.getCard());
//    } else {
//        // receiveKey();
//        // decrypt played card
//        // check if it matches
//        // send OK
//    }
//    updatePile(null); //TODO: get the card corresponding to the ID from the move
//    updateHand();

    private Communicator comm;
    private Deck deck;
    private Pile pile;
    private PlayerGroup players;

    public PlayProtocol(Communicator comm, Deck deck, Pile pile, PlayerGroup players) {
        this.comm = comm;
        this.deck = deck;
        this.pile = pile;
        this.players = players;
    }

    public void processMoveForCurrentPlayer(Move move) {
        Card playedCard = deck.getCard(move.getCard());
        boolean cardCanBePlayed;
        if (players.myTurn()) {
            comm.broadcastObject(playedCard.getMyKey());
            cardCanBePlayed = UnoGame.checkIfCardCanBePlayed(playedCard);
            boolean isAllowedByOthers = (Boolean) comm.receiveObject();
            if(!isAllowedByOthers || !cardCanBePlayed) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
        // Receive the key for the played card from the player in turn;
            CryptoKey key = (CryptoKey) comm.receiveObject();
        // decrypt the played card
            playedCard.decrypt(key);
        // check if it the move is allowed
            cardCanBePlayed = UnoGame.checkIfCardCanBePlayed(playedCard);
        // send OK
            if(cardCanBePlayed) {
                comm.sendObject(players.getPlayerInTurn().getPeerInfo(), true);
            } else {
                comm.sendObject(players.getPlayerInTurn().getPeerInfo(), false);
            }
        }
        //Update the pile
        pile.addCard(playedCard);
        //Update the hand
        players.getPlayerInTurn().getHand().removeCard(playedCard);
    }

}
