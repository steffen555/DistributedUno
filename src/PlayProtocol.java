public class PlayProtocol {

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
        Card playedCard = players.getPlayerInTurn().getHand().getCards().get(move.getCardIndex());
        boolean cardCanBePlayed;
        if (players.myTurn()) {
            comm.broadcastObject(playedCard.getMyKey());
            cardCanBePlayed = UnoGame.isLegalMove(playedCard, pile);
            if(!cardCanBePlayed) {
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
            cardCanBePlayed = UnoGame.isLegalMove(playedCard, pile);
        // send OK
            if(!cardCanBePlayed) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Update the pile
        pile.addCard(playedCard);
        //Update the hand
        players.getPlayerInTurn().getHand().removeCard(move.getCardIndex());
    }

}
