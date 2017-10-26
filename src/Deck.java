public class Deck {
    public Card getCard(int index){
        return null;
    }

    public static Deck generatePlainDeck() {
        return null; // TODO

    }

    public void encryptWithSingleKey(CryptoKey k_i) {
        // TODO
    }

    public void shuffle() {
        // TODO
        // NOTE: this must be cryptographically secure!!!
    }

    public void encryptWithMultipleKeys() {
        // TODO: encrypt every card with its own key.
    }

    public void decrypt(CryptoKey k_i) {
        // TODO: decrypt every card with k_i
    }
}
