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
        // However for production use, they should be much larger.
        // BigInteger p = BigInteger.valueOf(102079);
        // BigInteger q = BigInteger.valueOf(104383);
        BigInteger p = BigInteger.valueOf(283);
        BigInteger q = BigInteger.valueOf(293);
        // BigInteger p = new BigInteger("94283884166278099039827565009889052966771805725755793943931273274867605828531324499355605151594203717517529143625166692162928981031097594293798477958713526123600003229334506611919972576326970948702875984395735248890793764073521754359225373958014640130512754556561099358878844129016284525100432797747865162629");
        // BigInteger q = new BigInteger("97694032211643084226462844455093135919321673564611664962538343223166256734050545440107761641740464115163401570348379557675657335812817993937660148642952530641130483081689345688998058585777352004589013069482836169418413886480756878538668264403911410054887777497375494789297625394189389926386113344723070207203");

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
