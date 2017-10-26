
/*
The main requirement for the encryption scheme for our shuffling protocol
is that it must be commutative: E_a(E_b(m)) == E_b(E_a(m))

We also require that our scheme is CPA secure, because that prevents leaking
of any info that could let an attacker distinguish between plaintexts.

NOTE: we will use SRA for now, but it's not actually CPA secure.

 */
public class CryptoScheme {
    public CryptoKey generateKey() {
        // TODO: don't hardcode these.
        int e = 5;
        int d = 29;
        int N = 91;
        return new CryptoKey(e, d, N);
    }

    public int encrypt(CryptoKey key, int m) {
        return 0;
    }

    public int decrypt(CryptoKey key, int c) {
        return 0;


    }
}
