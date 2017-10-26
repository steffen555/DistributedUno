
/*
The main requirement for the encryption scheme for our shuffling protocol
is that it must be commutative: E_a(E_b(m)) == E_b(E_a(m))

We also require that our scheme is CPA secure, because that prevents leaking
of any info that could let an attacker distinguish between plaintexts.


 */
public class CryptoScheme {
    public static void test() {
        System.out.println("test");
    }


    public static void main(String[] args) {
        test();
    }


    public CryptoKey generateKey() {
        return null;
    }
}
