package geometry; /**
 * Created by homosapien97 on 3/11/17.
 */

import javafx.geometry.Point2D;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Blob extends LinkedList<Point2D> {
    private static final int DEFAULT_DEFORMATIONS = 5;
    private static final double DEFAULT_SCALE = 1024.0;
    private static final RegularPolygon TRIANGLE = new RegularPolygon(new Point2D(0,0), 3, DEFAULT_SCALE);
    private Random rand;

    public Blob(double jaggedness, Random rand) {
        super(TRIANGLE);
        System.out.println("Generating blob");
        this.rand = rand;
        Point2D a;
        Point2D b;
        for(int i = 0; i < DEFAULT_DEFORMATIONS; i++) {
            System.out.println("Deformation " + i);
            int j = 0;
            boolean done = false;
            for(ListIterator<Point2D> iter = this.listIterator(); !done && iter.hasNext();) {
                a = iter.next();
                if(iter.hasNext()) {
                    b = iter.next();
                } else {
                    b = this.getFirst();
                    done = true;
                }
                Point2D d = getMutation(a, b, jaggedness / (i + 1));
//                System.out.println("\ta : " + a);
//                System.out.println("\tb : " + b);
//                System.out.println("\td : " + d);
                iter.previous();
                iter.add(d);
//                try {TimeUnit.MILLISECONDS.sleep(10);} catch (Exception e){};
            }
        }
    }

    private Point2D getMutation(Point2D a, Point2D b, double jaggedness) {
        double rx;
        double ry;
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        if(dx == 0) {
            rx = 0;
            ry = dy * centerRand();
        } else {
            rx = dx * centerRand();
            ry = rx * dy / dx;
        }
        if(dy == 0) {
            dy = dx * (rand.nextDouble() - 0.5) * jaggedness;
            dx = 0;
        } else {
            double minv = -dx/dy;
            dx = Math.sqrt((dx * dx + dy * dy) / (minv * minv + 1)) * (rand.nextDouble() - 0.5) * jaggedness;
            dy = minv * dx;
        }
        rx += dx + a.getX();
        ry += dy + a.getY();
        return new Point2D(rx, ry);
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

    private void removeSelfIntersections() {

    }
}
