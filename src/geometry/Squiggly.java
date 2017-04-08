package geometry; /**
 * Created by homosapien97 on 3/11/17.
 */

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Squiggly extends LinkedList<Point2D> {
    private Random rand;

    public Squiggly(SquigglySettings squigglySettings, Random rand) {
        this(squigglySettings.container, squigglySettings.segmentLength, squigglySettings.curviness, rand);
    }

    public Squiggly(Polygon container, double segmentLength, double curviness, Random rand) {
        super();
        System.out.println("Generating squiggly");
        if(container == null || container.getPoints().size() == 0) {
            throw new IllegalArgumentException("Cannot generate Squiggly within null/empty polygon");
        }
        if(segmentLength == 0.0) {
            throw new IllegalArgumentException("Cannot have 0 length segments");
        }
        if(curviness < 0.0) {
            throw new IllegalArgumentException("Cannot have curviness < 0");
        }
        this.rand = rand;
        //add points until you cross the container. do not add points that cross self.
        int undoSize = 1;
    }

    private Point2D getStart(Polygon container) {
        Bounds containerLocalBounds = container.getBoundsInLocal();
        Bounds containerParentBounds = container.getBoundsInParent();
        System.out.println("Container local bounds: " + containerLocalBounds);
        System.out.println("Container parent bounds: " + containerParentBounds);
        Point2D ret;
        double x;
        double y;
        do {
            x = containerParentBounds.getMinX() + rand.nextDouble() * containerParentBounds.getWidth();
            y = containerParentBounds.getMinY() + rand.nextDouble() * containerParentBounds.getHeight();
//            ret = new Point2D(containerParentBounds.getMinX() + rand.nextDouble() * containerParentBounds.getWidth(),
//                    containerParentBounds.getMinY() + rand.nextDouble() * containerParentBounds.getHeight());
        } while (!container.contains(x, y));
        return new Point2D(x, y);
    }

    private Point2D getNext(Point2D a, Point2D b, double segmentLength, double curviness) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double thetaOld = Math.atan2(dx, dy);

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
