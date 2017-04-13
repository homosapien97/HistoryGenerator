package Utilities;

/**
 * Created by homosapien97 on 4/11/17.
 */
public class Transformations {
    public static double sqrtlog(double d) {
        if(d <= 0.0) {
            return 0.0;
        }
        d = Math.log(d);
        if(d < 0.0) {
            return 0.0;
        }
        return Math.sqrt(d);
    }
}
