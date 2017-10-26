import java.math.BigInteger;

/*
The main requirement for the encryption scheme for our shuffling protocol
is that it must be commutative: E_a(E_b(m)) == E_b(E_a(m))

We also require that our scheme is CPA secure, because that prevents leaking
of any info that could let an attacker distinguish between plaintexts.

NOTE: we will use SRA for now, but it's not actually CPA secure.

 */
public class CryptoScheme {
    public static CryptoKey generateKey() {
        // TODO: don't hardcode these.
        BigInteger e = BigInteger.valueOf(5);
        BigInteger d = BigInteger.valueOf(29);
        BigInteger N = BigInteger.valueOf(91);
        return new CryptoKey(e, d, N);
    }

    public static BigInteger encrypt(CryptoKey key, BigInteger m) {
        return m.modPow(key.exponent(), key.modulus());
    }

    public static BigInteger decrypt(CryptoKey key, BigInteger c) {
        return c.modPow(key.secret(), key.modulus());
    }

    public static void main(String[] args) {

        for (int i = 0; i < 50; i++) {
            CryptoKey key = CryptoScheme.generateKey();

            BigInteger m = BigInteger.valueOf(i);
            BigInteger c = CryptoScheme.encrypt(key, m);
            BigInteger decrypted = CryptoScheme.decrypt(key, c);

            if (!decrypted.equals(m)) {
                System.out.println("Bad decryption: " + i);
            }

        }

        System.out.println("Done with tests.");
    }
}
