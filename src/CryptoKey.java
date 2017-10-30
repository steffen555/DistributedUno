import java.io.Serializable;
import java.math.BigInteger;

public class CryptoKey implements Serializable {
    private BigInteger e, d, n;
    public CryptoKey(BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
    }

    public BigInteger exponent() {
        return e;
    }

    public BigInteger modulus() {
        return n;
    }

    public BigInteger secret() {
        return d;
    }

    public String toString() {
        return "(e, d) = (" + e + ", " + d + ")";
    }
}
