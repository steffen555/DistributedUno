import java.math.BigInteger;
import java.security.SecureRandom;

/*
The main requirement for the encryption scheme for our shuffling protocol
is that it must be commutative: E_a(E_b(m)) == E_b(E_a(m))

We also require that our scheme is CPA secure, because that prevents leaking
of any info that could let an attacker distinguish between plaintexts.

NOTE: we will use SRA for now, but it's not actually CPA secure.

 */
public class CryptoScheme {

    private static BigInteger getRandomBigInteger(BigInteger max) {
        BigInteger result;
        SecureRandom r = new SecureRandom();
        do {
            result = new BigInteger(max.bitLength(), r);
        } while (result.compareTo(max) >= 0);
        return result;
    }

    public static CryptoKey generateKey() {
        // p and q should be two large primes which the players all agree on.
        // for now we hardcode them. This isn't a security problem in itself.
        // these are 512-bit primes.
        BigInteger p = new BigInteger("2176187763245297881380991883213950017887607507358316510279182399064824689314449105430222344841565460924488358979540420251500364254919944890937082881695711");
        BigInteger q = new BigInteger("3527621340917007454188182589377582662756547437513641295853523524274065928842127377635838153725151854226668472382746463639102264307213532475601718621969707");

        // for quick testing.
        // BigInteger p = BigInteger.valueOf(283);
        // BigInteger q = BigInteger.valueOf(293);

        BigInteger totient = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger N = p.multiply(q);

        BigInteger e, d;

        while (true) {
            e = getRandomBigInteger(N);
            try {
                d = e.modInverse(totient);
                break;
            }
            catch (ArithmeticException ex) {
                // just try again.
            }
        }

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
