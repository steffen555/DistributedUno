public class EncryptedCard implements Card {
    private int key = 0;

    public EncryptedCard() {
        key = 1234;
    }

    public int getKey() {
        return key;
    }
}
