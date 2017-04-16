package geometry; /**
 * Created by homosapien97 on 3/11/17.
 */

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.*;

import java.util.*;

public class Squiggly extends LinkedList<Point2D> {
    private Random rand;
//    public final Polygon container;
//    public final HashSet<Polygon> containers;
    public Polygon container;
    public Polygon superContainer;
    public final double segmentLength;
    public final double curivness;

    public Squiggly(SquigglySettings squigglySettings, Random rand) {
        this(squigglySettings.superContainer, squigglySettings.container, squigglySettings.segmentLength, squigglySettings.curviness, squigglySettings.shorter, rand);
    }

    public Squiggly(Polygon superContainer, HashSet<Polygon> containers, double segmentLength, double curviness, boolean shorter, Random rand) {
        this(superContainer, PolygonUtils.firstNonNull(containers), segmentLength, curviness, shorter, rand);
    }

    public Squiggly(Polygon superContainer, Polygon container, double segmentLength, double curviness, boolean shorter, Random rand) {
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
        this.superContainer = superContainer;
        this.container = container;
        this.segmentLength = segmentLength;
        this.curivness = curviness;
        //add points until you cross the container. do not add points that cross self.
        System.out.println("\tGenerating start");
        this.add(getStart());
//        System.out.println("First squiggly point: " + this.getFirst().getX() + ", " + this.getFirst().getY());
        System.out.println("\tGenerating second point");
        if(shorter) {
            this.add(getShorterSecond());
        } else {
            this.add(getRandomSecond());
        }
        System.out.println("\tGenerating more points");
//        System.out.println("Second squiggly point: " + this.get(1).getX() + ", " + this.get(1).getY());
        if(!contained(this.getLast().getX(), this.getLast().getY())) {
//            System.out.println("Only 2 points");
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
//            System.out.println("Intersection done");
//            this.removeLast();
//            System.out.println("Removal done");
//            System.out.println("Intersect size = " + intersect.getPoints().size());
            this.add(new Point2D(intersect.getPoints().get(intersect.getPoints().size() - 2), intersect.getPoints().get(intersect.getPoints().size() - 1)));
//            System.out.println("Addition done");
            return true;
        } catch (Exception e) {
//            System.out.println("Intersection error while clipping squiggly");
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
        return PolygonUtils.approxRandomInteriorPoint(container, 1.0, rand);
    }

    private Point2D getRandomSecond() {
        double fx = this.getFirst().getX();
        double fy = this.getFirst().getY();
        double theta = rand.nextDouble() * Math.PI * 2;
        return new Point2D(fx + segmentLength * Math.cos(theta), fy + segmentLength * Math.sin(theta));
    }

    private Point2D getShorterSecond() {
        double fx = this.getFirst().getX();
        double fy = this.getFirst().getY();
        double r = 1.5;
        double dr = Math.abs(Math.log(superContainer.getBoundsInParent().getWidth() * superContainer.getBoundsInParent().getHeight()));
        double s;
        Polygon circle;
        Polygon subtract;
        do {
            r+= dr;
            s = Math.sqrt(2 * r * r - 2 * r * r * Math.cos(Math.PI * 2 / 20));
            circle = (new RegularPolygon(new Point2D(fx - s / 2, fy - Math.sqrt(r * r - s * s / 4)), 20, s)).getPolygon();
            subtract = PolygonUtils.subtract(circle, superContainer);
        } while(subtract == null);
        double x;
        double y;
        Bounds subtractBounds = subtract.getBoundsInParent();
//        do {
//            x = subtractBounds.getMinX() + rand.nextDouble() * subtractBounds.getWidth();
//            y = subtractBounds.getMinY() + rand.nextDouble() * subtractBounds.getHeight();
////            System.out.println("Tried " + x + ", " + y);
//        } while(!PolygonUtils.contains(subtract, x, y));
        Point2D temp = PolygonUtils.approxRandomInteriorPoint(subtract, 1.0, rand);
        x = temp.getX();
        y = temp.getY();
        double l = segmentLength / Math.sqrt((x - fx) * (x - fx) + (y - fy) * (y - fy));
        x = fx + l * (x - fx);
        y = fy + l * (y - fy);
        return new Point2D(x, y);
    }


    private boolean contained(double x, double y) {
        return PolygonUtils.contains(container, x, y);
    }

    private Point2D getNext(Point2D a, Point2D b, double segmentLength, double curviness) {
        double theta = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()) + (rand.nextDouble() - 0.5) * curviness;
        return new Point2D(b.getX() + segmentLength * Math.cos(theta), b.getY() + segmentLength * Math.sin(theta));
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
