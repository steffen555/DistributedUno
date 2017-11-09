import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

public class EncryptedCard extends Card {

    private BigInteger encryptedValue;
    private int encryptionCounter;


    public EncryptedCard (ActionCardTarget act, BigInteger plainValue) {
        super(CardColor.NO_COLOR, act);
        myKey = CryptoScheme.generateKey();
        this.encryptedValue = CryptoScheme.encrypt(myKey, plainValue);
        this.encryptionCounter = 1;
    }

    public EncryptedCard (ActionCardTarget act, BigInteger encryptedValue, int encryptionCounter) {
        super(CardColor.NO_COLOR, act);
        this.encryptedValue = encryptedValue;
        this.encryptionCounter = encryptionCounter;
    }

    @Override
    public int getNumber() {
        throw new NotImplementedException();
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
            Card result = CardTranslator.valueToCard(plainValue, getActionTarget());
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
