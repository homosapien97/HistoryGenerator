package world;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Random;

/**
 * Created by homosapien97 on 4/15/17.
 */
public class Culture {
    public static final double SCALE = 100.0;
    public static double minimumValue = 0.0;
    public static double maximumValue = 0.0;
    public static double maxChange = 1.0;
    public double language;
    public double religion;
    public double individualism;
    private Random rand;

    public Culture(double l, double r, double i, Random random) {
        language = l;
        religion = r;
        individualism = i;
        rand = random;
        setExtremes();
    }
    public Culture(Culture copy) {
        language = copy.language;
        religion = copy.religion;
        individualism = copy.individualism;
        rand = copy.rand;
        setExtremes();
    }

    public Culture(Random random) {
        rand = random;
//        language = SCALE * rand.nextGaussian();
//        religion = SCALE * rand.nextGaussian();
//        individualism = SCALE * rand.nextGaussian();
        language = SCALE * random.nextDouble();
        religion = SCALE * random.nextDouble();
        individualism = SCALE * random.nextDouble();
        setExtremes();
    }

    public void driftTo(Culture anchor, Culture driftTo, double factor) {
//        System.out.println("DriftTo Factor: " + factor);
        double dl = driftTo.language - anchor.language;
        double dr = driftTo.religion - anchor.religion;
        double di = driftTo.individualism - anchor.individualism;
//        System.out.println("Drift factor: " + factor);
        language += factor * dl;
        religion += factor * dr;
        individualism += factor * di;
        setExtremes();
    }

    public void drift(double factor) {
        language += factor * (centerRand() - 0.5);
        religion += factor * (centerRand() - 0.5);
        individualism += factor * (centerRand() - 0.5);
        setExtremes();
    }

    private double centerRand() {
        double ret = rand.nextDouble();
        return ret * (4 * ret * ret - 6 * ret + 3);
    }

    @Override
    public String toString() {
        return "Culture:(" + relativize(language) + ", " + relativize(religion) + ", " + relativize(individualism) + ")";
    }

    public Paint getFill() {
//        return new Color(clamp(language / SCALE + 1), clamp(religion / SCALE + 1), clamp(individualism / SCALE + 1), 1.0);
        return new Color(relativize(language), relativize(religion), relativize(individualism), 1.0);
    }

    private double clamp(double c) {
        if(c < 0.0) return 0.0;
        if(c > 1.0) return 1.0;
        return c;
    }

    private void setExtremes() {
        if(language < minimumValue) {
            minimumValue = language;
        } else if(language > maximumValue) {
            maximumValue = language;
        }
        if(religion < minimumValue) {
            minimumValue = religion;
        } else if(religion > maximumValue) {
            maximumValue = religion;
        }
        if(individualism < minimumValue) {
            minimumValue = individualism;
        } else if(individualism > maximumValue) {
            maximumValue = individualism;
        }
        maxChange = maximumValue - minimumValue;
    }

    private double relativize(double d) {
        return (d - minimumValue) / maxChange;
    }
}
