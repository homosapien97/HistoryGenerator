/**
 * Created by homosapien97 on 3/11/17.
 */
public class IntPair {
    public final int a;
    public final int b;

    public IntPair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int hashCode() {
        return ((a << 16) + (a >>> 16) + b);
    }

    public boolean equals(Object o) {
        if(o instanceof IntPair) {
            if(this == o ) {
                return true;
            } else if(this.hashCode() == o.hashCode()) {
                IntPair rhs = (IntPair) o;
                return (a == rhs.a) && (b == rhs.b);
            }
        }
        return false;
    }
}
