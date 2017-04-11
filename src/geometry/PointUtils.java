package geometry;

import javafx.geometry.Point2D;

import java.util.List;

/**
 * Created by homosapien97 on 3/11/17.
 */
public class PointUtils {
    public static final double PRECISION = Double.MIN_VALUE;
    public static final Point2D NaNPoint = new Point2D(Double.NaN, Double.NaN);

    /**
     * Gets the orientation of three points. Positive is clockwise, negative is counterclockwise, and 0 is collinear.
     * @param p First point
     * @param q Second point
     * @param r Third point
     * @return Orientation
     */
    public static double orientation(Point2D p, Point2D q, Point2D r) {
//        return (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
        return orientation(p.getX(), p.getY(), q.getX(), q.getY(), r.getX(), r.getY());
    }

    public static double orientation(double x1, double y1, double x2, double y2, double x3, double y3) {
        return (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
    }

    public static double squareDistance(Point2D a, Point2D b) {
        return (a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY());
    }

    /**
     * Determines whether two points are closer than c to eachother
     * @param a a point
     * @param b a point
     * @param c a distance
     * @return
     */
    public static boolean close(Point2D a, Point2D b, double c) {
        return a.distance(b) <= c;
    }

    public static boolean squareClose(Point2D a, Point2D b, double csquared) {
        return squareDistance(a, b) <= csquared;
    }

    public static boolean onSegmentStrict(double x1, double y1, double x2, double y2, double x3, double y3)  {
        return x3 < Math.max(x1, x2) && x3 > Math.min(x1, x2) && y3 < Math.max(y1, y2) && y3 > Math.min(y1, y2);
    }

    public static boolean crosses(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return  (orientation(x1, y1, x2, y2, x3, y3) * orientation(x1, y1, x2, y2, x4, y4) < 0
                &&
                orientation(x3, y3, x4, y4, x1, y1) * orientation(x3, y3, x4, y4, x2, y2) < 0)
                ||
                orientation(x1, y1, x2, y2, x3, y3) == 0 && onSegmentStrict(x1, y1, x2, y2, x3, y3)
                ||
                orientation(x1, y1, x2, y2, x4, y4) == 0 && onSegmentStrict(x1, y1, x2, y2, x4, y4)
                ||
                orientation(x3, y3, x4, y4, x1, y1) == 0 && onSegmentStrict(x3, y3, x4, y4, x1, y1)
                ||
                orientation(x3, y3, x4, y4, x2, y2) == 0 && onSegmentStrict(x3, y3, x4, y4, x2, y2);
    }

    public static boolean crosses(Point2D a, Point2D b, Point2D c, Point2D d) {
        return crosses(a.getX(), a.getY(), b.getX(), b.getY(), c.getX(), c.getY(), d.getX(), d.getY());
    }

    public static boolean crosses(LineSegment a, LineSegment b) {
        return crosses(a.getA(), a.getB(), b.getA(), b.getB());
    }

    public static Point2D closest(Point2D p, List<Point2D> points) {
        Point2D ret = points.get(0);
        double shortestDistance = 0.0;
        double currentDistance;
        for(Point2D point : points) {
            currentDistance = p.distance(point);
            if(currentDistance < shortestDistance) {
                ret = point;
                shortestDistance = currentDistance;
            }
        }
        return ret;
    }
}
