package world;

import java.util.Random;

/**
 * Created by homosapien97 on 4/15/17.
 */
public class Culture {
    public static final double SCALE = 100.0;
    public double language;
    public double religion;
    public double individualism;
    private Random rand;

    public Culture(double l, double r, double i, Random random) {
        language = l;
        religion = r;
        individualism = i;
        rand = random;
    }

    public Culture(Random random) {
        rand = random;
        language = SCALE * rand.nextGaussian();
        religion = SCALE * rand.nextGaussian();
        individualism = SCALE * rand.nextGaussian();
    }

    public void driftTo(Culture anchor, Culture driftTo, double factor) {
        System.out.println("DriftTo Factor: " + factor);
        double dl = driftTo.language - anchor.language;
        double dr = driftTo.religion - anchor.religion;
        double di = driftTo.individualism - anchor.individualism;
        language += factor * dl;
        religion += factor * dr;
        individualism += factor * di;
    }

    public void drift(double factor) {
        language += factor * (centerRand() - 0.5);
        religion += factor * (centerRand() - 0.5);
        individualism += factor * (centerRand() - 0.5);
    }

    private double centerRand() {
        double ret = rand.nextDouble();
        return ret * (4 * ret * ret - 6 * ret + 3);
    }

    @Override
    public String toString() {
        return "Culture:(" + language + ", " + religion + ", " + individualism + ")";
    }
}
