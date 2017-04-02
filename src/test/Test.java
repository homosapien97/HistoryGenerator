package test;

import geometry.LineSegment;
import geometry.PointUtils;

/**
 * Created by homosapien97 on 4/2/17.
 */
public class Test {
    public static void main(String[] args) {
        LineSegment a = new LineSegment(0.0, 0.0, 1.0, 0.0);
        LineSegment b = new LineSegment(0.5, 0.5, 0.5, -0.5);
        LineSegment c = new LineSegment(0.0, 1.0, 1.0, 1.0);
        System.out.println("a intersect b? " + PointUtils.crosses(a, b));
        System.out.println("a intersect c? " + PointUtils.crosses(a, c));
    }
}
