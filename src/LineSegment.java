import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;

/**
 * Created by homosapien97 on 3/11/17.
 */
public class LineSegment {
    public final Point2D a;
    public final Point2D b;
    public final double length;

    public LineSegment(Point2D a, Point2D b) {
        this.a = a;
        this.b = b;
        this.length = a.distance(b);
    }

    public Point2D getA() {
        return a;
    }

    public Point2D getB() {
        return b;
    }

    /**
     * Gets the length of this segment
     * @return length
     */
    public double length() {
        return length;
    }

    public double squareLength() {
        return PointUtils.squareDistance(a, b);
    }

    /**
     * Gets the slope of this line segment.
     * In the case where the segment is vertical, returns Double.NaN.
     * @return slope
     */
    public double slope() {
        if(length == 0 || a.getX() == b.getX()) return Double.NaN;
        return (a.getY() - b.getY()) / (a.getX() - b.getX());
    }

    /**
     * Gets the midpoint of the line segment
     * @return the midpoint of the segment
     */
    public Point2D midpoint() {
        return a.midpoint(b);
    }

    /**
     * Gets the point of intersection between the line represented by this segment and the line represented by l.
     * In the case where the lines do not intersect, the result is PointUtils.NanPoint.
     * In the case where the lines have multiple points of intersection, a suitable endpoint of one of the sections is chosen.
     * @param l line to be intersected
     * @return intersection of this with l
     */
    public Point2D intersection(LineSegment l) {
        if(length == 0) {
            //this is a point
            if((l.length == 0 && a.equals(l.a)) || (PointUtils.orientation(a,l.a,l.b) == 0.0 && l.onSegment(a))){
                //l is the same point, or this is on l
                return a;
            } else {
                //there is no intersection
                return PointUtils.NaNPoint;
            }
        } else {
            //this is a segment
            if(l.length == 0 && PointUtils.orientation(a,b,l.a) == 0 && onSegment(l.a)) {
                //l is a point on this
                return l.a;
            } else if(slope() == l.slope() || (Double.isNaN(slope()) && Double.isNaN(l.slope()))) {
                //this and l are parallel
                if(PointUtils.orientation(a, b, l.a) == 0.0) {
                    //the lines are the same
                    if(onSegment(l.a)) {
                        return l.a;
                    } else if(onSegment(l.b)) {
                        return l.b;
                    } else if(l.onSegment(a)){
                        return a;
                    } else if(l.onSegment(b)) {
                        return b;
                    } else {
                        //there is no crossover
                        return PointUtils.NaNPoint;
                    }
                }
                //there is no intersection
                return PointUtils.NaNPoint;
            } else {
                if(a.equals(l.a) || a.equals(l.b)) {
                    //to avoid rounding errors
                    return a;
                } else if(b.equals(l.a) || b.equals(l.b)) {
                    //to avoid rounding errors
                    return b;
                } else {
                    //actual intersection math
                    double q = a.getX() * b.getY() - b.getX() * a.getY();
                    double w = l.a.getX() - l.b.getX();
                    double e = l.a.getX() * l.b.getY() - l.b.getX() * l.a.getY();
                    double r = a.getX() - b.getY();
                    double t = l.a.getY() - l.b.getY();
                    double y = a.getY() - b.getY();

                    return new Point2D((q * w - e * r) / (r * t - w * y), (q * t - e * y) / (r * t - w * y));
                }
            }
        }
    }

    /**
     * Gives whether r is on this segment.
     * Requires that r be collinear with a and b.
     * @param q point to test
     * @return whether r is on this segment
     */
    private boolean onSegment(Point2D q)  {
        return q.getX() <= Math.max(a.getX(), b.getX()) && q.getX() >= Math.min(a.getX(), b.getX())
                && q.getY() <= Math.max(a.getY(), b.getY()) && q.getY() >= Math.min(a.getY(), b.getY());
    }

    /**
     * Projection of q onto the line represented by this segment
     *
     * math from http://www.euclideanspace.com/maths/geometry/elements/line/projections/
     *
     * @param q a point
     * @return the closest point on the line represented by this segment to q
     */
    public Point2D projection(Point2D q) {
        if(length == 0) return a;
        double x1 = q.getX() - a.getX();
        double y1 = q.getY() - a.getY();
        double x2 = b.getX() - a.getX();
        double y2 = b.getY() - a.getY();
        double scale = (x1 * x2 + y1 * y2) / (x2 * x2 + y2 * y2);
        return new Point2D(a.getX() + x2 * scale, a.getY() + y2 * scale);
    }

    /**
     * Gets the distance between q and the projection of q onto the line represented by this segment
     * @param q a point
     * @return distance to the projection of q onto this
     */
    public double orthoDistance(Point2D q) {
        return q.distance(projection(q));
    }

    /**
     * Nearest point to q on this segment
     * @param q a point
     * @return the closest point on this segment to q
     */
    public Point2D nearest(Point2D q) {
        Point2D ret = projection(q);
        if(onSegment(ret)) {
            return ret;
        }
        if(a.distance(q) < b.distance(q)) {
            return a;
        }
        return b;
    }

    /**
     * Gets the distance between q and this segment
     * @param q a point
     * @return distance between this segment and q
     */
    public double distance(Point2D q) {
        Point2D proj = projection(q);
        if(onSegment(proj)) {
            return q.distance(proj);
        }
        double da = a.distance(q);
        double db = b.distance(q);
        return Math.min(da,db);
    }

    /**
     * Determines whether a point x,y is above or below the line represented by the segment.
     * Positive is above
     * Negative is below
     * 0 is on.
     * http://stackoverflow.com/questions/99353/how-to-test-if-a-line-segment-intersects-an-axis-aligned-rectange-in-2d
     * @param x
     * @param y
     * @return
     */
    private double above(double x, double y) {
        return (b.getY() - a.getY()) * x + (a.getX() - b.getX()) * y + b.getX() * a.getY() - a.getX() * b.getY();
    }

//    public BoxList thickRasterization(double scale) {
//        BoxList ret = new BoxList(scale);
//        double s = slope();
//        if(Double.isNaN(s)) {
//            //TODO
//        } else {
//            if(a.getX() > b.getX()) {
//                int i = (int)Math.ceil((a.getX() - b.getX()) / scale);
//                if(s == 0.0) {
//                    ret.push(b);
//                    if(ret.isVerticalSide(b)) {
//                        ret.push(new IntPair(ret.peek().a - 1, ret.peek().b));
//                        if(ret.isHorizontalSide(b)) {
//                            ret.push(new IntPair(ret.peek().a + 1, ret.peek().b - 1));
//                            ret.push(new IntPair(ret.peek().a - 1, ret.peek().b));
//                            for(int j = 0; j < i; j++) {
//                                ret.add(new IntPair(ret.getLast().a + 1, ret.getLast().b - 1));
//                                ret.add(new IntPair(ret.getLast().a, ret.getLast().b + 1));
//                            }
//                        } else {
//                            for(int j = 0; j < i; j++) {
//                                ret.add(new IntPair(ret.getLast().a + 1, ret.getLast().b));
//                            }
//                        }
//                    } else if(ret.isHorizontalSide(b)) {
//                        for(int j = 0; j < i; j++) {
//                            ret.add(new IntPair(ret.getLast().a + 1, ret.getLast().b - 1));
//                            ret.add(new IntPair(ret.getLast().a, ret.getLast().b + 1));
//                        }
//                    }
//                } else if (s < 0.0) {
//                    //TODO
//                } else if (s > 0.0) {
//                    //TODO
//                }
//            } else {
//                //TODO
//            }
//        }
//    }
}
