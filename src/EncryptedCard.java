import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

public class EncryptedCard extends Card {

    private BigInteger encryptedValue;
    private int encryptionCounter;

    public EncryptedCard (BigInteger plainValue) {
        super(CardColor.NO_COLOR);
        myKey = CryptoScheme.generateKey();
        this.encryptedValue = CryptoScheme.encrypt(myKey, plainValue);
        this.encryptionCounter = 1;
    }

    public EncryptedCard (BigInteger encryptedValue, int encryptionCounter) {
        super(CardColor.NO_COLOR);
        this.encryptedValue = encryptedValue;
        this.encryptionCounter = encryptionCounter;
    }

    public EncryptedCard encrypt(CryptoKey key) {
        encryptedValue = CryptoScheme.encrypt(key, encryptedValue);
        encryptionCounter++;
        return this;
    }

    public Card decrypt(CryptoKey key) {
        // System.out.println("Decrypting " + getValue() + " with key: " + key);
        if (encryptionCounter == 1) {
            BigInteger plainValue = CryptoScheme.decrypt(key, encryptedValue);
            Card result = new CardRepresentation(0, plainValue).toCard();
            result.setMyKey(getMyKey());
            return result;
        }
        else {
            encryptedValue = CryptoScheme.decrypt(key, encryptedValue);
            encryptionCounter--;
            return this;
        }
    }

    public EncryptedCard encryptWithNewKey() {
        myKey = CryptoScheme.generateKey();
        return encrypt(myKey);
    }

    @Override
    public CardRepresentation toRepresentation() {
        return new CardRepresentation(encryptionCounter, encryptedValue);
    }

    public Card decryptWithMyKey() {
        return decrypt(myKey);
    }

}
