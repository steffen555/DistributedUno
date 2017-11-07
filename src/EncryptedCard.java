import java.math.BigInteger;

public class EncryptedCard extends Card {

    private BigInteger encryptedValue;
    private int encryptionCounter;

    public EncryptedCard (BigInteger value, CardColor color) {
        super(color);
        this.encryptedValue = value;
    }

    public void decrypt(CryptoKey key) {
        // System.out.println("Decrypting " + getValue() + " with key: " + key);
        encryptedValue = CryptoScheme.decrypt(key, encryptedValue);
    }

    public void decryptWithMyKey() {
        decrypt(super.getMyKey());
    }

    @Override
    public String toString() {
        return "<encrypted card>"; // not fully decrypted yet..;
    }
}
