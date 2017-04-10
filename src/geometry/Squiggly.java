package geometry; /**
 * Created by homosapien97 on 3/11/17.
 */

import Utilities.Dependable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.*;
import world.Continent;

import java.util.*;

public class Squiggly extends LinkedList<Point2D> {
    private Random rand;
//    public final Polygon container;
//    public final HashSet<Polygon> containers;
    public Polygon container;
    public final double segmentLength;
    public final double curivness;

    public Squiggly(SquigglySettings squigglySettings, Random rand) {
        this(squigglySettings.container, squigglySettings.segmentLength, squigglySettings.curviness, rand);
    }

    public Squiggly(HashSet<Polygon> containers, double segmentLength, double curviness, Random rand) {
        this(PolygonUtils.firstNonNull(containers), segmentLength, curviness, rand);
    }

    public Squiggly(Polygon container, double segmentLength, double curviness, Random rand) {
        super();
        System.out.println("Generating squiggly with sidelength " + segmentLength);
        if(container == null) {
            throw new IllegalArgumentException("Cannot generate Squiggly within null/empty polygon");
        }
        if(segmentLength == 0.0) {
            throw new IllegalArgumentException("Cannot have 0 length segments");
        }
        if(curviness < 0.0) {
            throw new IllegalArgumentException("Cannot have curviness < 0");
        }
        this.rand = rand;
//        this.container = container;
//        this.containers = new HashSet<>();
//        containers.add(container);
        this.container = container;
        this.segmentLength = segmentLength;
        this.curivness = curviness;
        //add points until you cross the container. do not add points that cross self.
        this.add(getStart());
        this.add(getSecond());
        if(!contained(this.getLast().getX(), this.getLast().getY())) {
            System.out.println("Only 2 points");
        } else {
            Point2D next;
            Iterator<Point2D> tailIterator;
            boolean crosses;
            int consecutiveFails = 0;
            do {
                tailIterator = this.descendingIterator();
                Point2D last = tailIterator.next();
                Point2D a = tailIterator.next();
                next = getNext(a, last, segmentLength, curviness);
                crosses = false;
                while(tailIterator.hasNext()) {
                    Point2D b = a;
                    a = tailIterator.next();
                    if(PointUtils.crosses(last, next, a, b)) {
                        crosses = true;
                        break;
                    }
                }
                if(crosses) {
                    consecutiveFails++;
                    if(consecutiveFails > 3) {
                        this.removeLast();
                        consecutiveFails = 0;
                    }
                } else {
                    this.addLast(next);
                }
            } while(contained(next.getX(), next.getY()));
        }
    }

    public boolean clipTail() {
        Polyline tail = new Polyline();
        Iterator<Point2D> reverserator = this.descendingIterator();
        Point2D tail2 = reverserator.next();
        Point2D tail1 = reverserator.next();
        tail.getPoints().addAll(tail2.getX(), tail2.getY());
        tail.getPoints().addAll(tail1.getX(), tail1.getY());
        try {
            Polyline intersect = PolylineUtils.intersection(tail, container);
            System.out.println("Intersection done");
//            this.removeLast();
            System.out.println("Removal done");
            System.out.println("Intersect size = " + intersect.getPoints().size());
            this.add(new Point2D(intersect.getPoints().get(intersect.getPoints().size() - 2), intersect.getPoints().get(intersect.getPoints().size() - 1)));
            System.out.println("Addition done");
            return true;
        } catch (Exception e) {
            System.out.println("Intersection error while clipping squiggly");
            return false;
        }
    }

    public boolean removeTail() {
        if(this.size() > 2) {
            this.removeLast();
            return true;
        }
        return false;
    }
    public boolean removeTail(double portion) {
        if(portion > 1.0 || portion < 0.0) {
            throw new IllegalArgumentException("Portion must be between 0 and 1");
        }
        if(this.size() * (1 - portion) > 2) {
            for(int i = 0; i < (int)(this.size() * portion); i++) {
                this.removeLast();
            }
            return true;
        } else {
            return false;
        }
    }
    public double length() {
        return this.size() * segmentLength;
    }

    private Point2D getStart() {
        System.out.println("Generating first point");
//        Bounds containerLocalBounds = container.getBoundsInLocal();
//        LinkedList<Polygon> containers = PolygonUtils.polygons(container);
        Bounds containerParentBounds = container.getBoundsInParent();
//        System.out.println("Container local bounds: " + containerLocalBounds);
//        System.out.println("Container parent bounds: " + containerParentBounds);
        Point2D ret;
        if(containerParentBounds.getWidth() < 1.0 && containerParentBounds.getHeight() < 1.0) {
            return new Point2D (containerParentBounds.getMinX() + containerParentBounds.getWidth() / 2,
                    containerParentBounds.getMinY() + containerParentBounds.getHeight() / 2);
        }
        double x;
        double y;
        do {
            x = containerParentBounds.getMinX() + rand.nextDouble() * containerParentBounds.getWidth();
            y = containerParentBounds.getMinY() + rand.nextDouble() * containerParentBounds.getHeight();
//            System.out.println("Tried " + x + ", " + y);
//        } while (!container.contains(x, y));
        } while(!PolygonUtils.contains(container, x, y));
        return new Point2D(x, y);
    }

    private Point2D getSecond() {
        System.out.print("Generating second point");
        double fx = this.getFirst().getX();
        double fy = this.getFirst().getY();
        double r = 1.5;
        double dr = Math.abs(Math.log(container.getBoundsInParent().getWidth() * container.getBoundsInParent().getHeight()));
        double s;
        Polygon circle;
        Polygon subtract;
        do {
//            r = Math.sqrt(segmentLength * segmentLength * segmentLength);
//            r *= r;
            r+= dr;
            s = Math.sqrt(2 * r * r - 2 * r * r * Math.cos(Math.PI * 2 / 20));
            circle = (new RegularPolygon(new Point2D(fx - s / 2, fy - Math.sqrt(r * r - s * s / 4)), 20, s)).getPolygon();
            subtract = PolygonUtils.subtract(circle, container);
        } while(subtract == null);
        System.out.println(" with radius: " + r);
        double x;
        double y;
        Bounds subtractBounds = subtract.getBoundsInParent();
        do {
            x = subtractBounds.getMinX() + rand.nextDouble() * subtractBounds.getWidth();
            y = subtractBounds.getMinY() + rand.nextDouble() * subtractBounds.getHeight();
//            System.out.println("Tried " + x + ", " + y);
        } while(!PolygonUtils.contains(subtract, x, y));
        double l = segmentLength / Math.sqrt((x - fx) * (x - fx) + (y - fy) * (y - fy));
        x = fx + l * (x - fx);
        y = fy + l * (y - fy);
        return new Point2D(x, y);
    }


    private boolean contained(double x, double y) {
//        //this is so stupid
//        Polygon rect = new Polygon();
//        rect.getPoints().addAll(
//                x - 1.0 / 512.0, y - 1.0 / 512.0,
//                x + 1.0 / 512.0, y - 1.0 / 512.0,
//                x + 1.0 / 512.0, y + 1.0 / 512.0,
//                x - 1.0 / 512.0, y + 1.0 / 512.0
//        );
//        try {
//            Polygon subt = PolygonUtils.subtract(rect, container);
//            if(subt == null) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//        }
        return PolygonUtils.contains(container, x, y);
    }

    private Point2D getNext(Point2D a, Point2D b, double segmentLength, double curviness) {
        double theta = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()) + (rand.nextDouble() - 0.5) * curviness;
        return new Point2D(b.getX() + segmentLength * Math.cos(theta), b.getY() + segmentLength * Math.sin(theta));
    }

    private double centerRand() {
        double ret = rand.nextDouble();
        return ret * (4 * ret * ret - 6 * ret + 3);
    }

    public LinkedList<Double> getDoubleList() {
        LinkedList<Double> ret = new LinkedList<>();
        Point2D p;
        for(ListIterator<Point2D> iter = this.listIterator(); iter.hasNext();) {
            p = iter.next();
            ret.addLast(p.getX());
            ret.addLast(p.getY());
        }
        return ret;
    }
}
