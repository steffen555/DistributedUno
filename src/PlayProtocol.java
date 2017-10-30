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

        if (players.myTurn())
            comm.broadcastObject(playedCard.getMyKey());
        else {
            playedCard.decrypt((CryptoKey) comm.receiveObject());
            System.out.println("Playing this card: " + playedCard);
        }


        UnoGame.validateMove(playedCard, pile);

        //Update the pile
        pile.addCard(playedCard);
        //Update the hand
        players.getPlayerInTurn().getHand().removeCard(move.getCardIndex());
    }

}
