package geometry; /**
 * Created by homosapien97 on 3/11/17.
 */

import javafx.geometry.Point2D;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Blob extends LinkedList<Point2D> {
    private static final int DEFAULT_DEFORMATIONS = 5;
    private static final double DEFAULT_SCALE = 1024.0;
    private static final RegularPolygon TRIANGLE = new RegularPolygon(new Point2D(0,0), 3, DEFAULT_SCALE);
    private Random rand;

    public Blob(double jaggedness, Random rand) {
        this(jaggedness, DEFAULT_DEFORMATIONS, rand);
    }

    public Blob(double jaggedness, int deformations, Random rand) {
        super(TRIANGLE);
        System.out.println("Generating blob");
        this.rand = rand;
        Point2D a;
        Point2D b;
        for(int ndefs = 0; ndefs < deformations; ndefs++) {
            System.out.println("Beginning deformation " + ndefs);
            int i = 0;
            boolean done = false;
            for(ListIterator<Point2D> iter = this.listIterator(); !done && iter.hasNext(); i++) {
                a = iter.next();
                if(iter.hasNext()) {
                    b = iter.next();
                } else {
                    b = this.getFirst();
                    done = true;
                }
                Point2D d = getMutation(a, b, jaggedness / (ndefs + 1));

                //test whether (ad) crosses anything from 0 to index(a)-1 or index(a+1) to end
                //i = index(a)

                //test whether (bd) crosses anything from 0 to index(b)-1 or index(b+1) to end
                //i+1 = index(b)
                boolean fail = false;
                int j = 0;
                ListIterator<Point2D> jter = this.listIterator();
                Point2D x;
                Point2D y;
                if(done) {
                    //test whether ad crosses anything from 0 to the last index - 2
                    //test whether db crosses anything from 1 to the last index.
                    x = jter.next();
                    y = jter.next();
                    jter.previous();
                    if(PointUtils.crosses(x, y, a, d)) {
                        fail = true;
                    } else {
                        for(; j < this.size() - 2; j++) {
                            x = jter.next();
                            y = jter.next();
                            if(PointUtils.crosses(x, y, a, d) || PointUtils.crosses(x, y, d, b)) {
                                fail = true;
                                jter.previous();
                                break;
                            }
                            jter.previous();
                        }
                        if(!fail) {
                            jter.previous();
                            x = jter.next();
                            y = jter.next();
                            if(PointUtils.crosses(x, y, d, b)) {
                                fail = true;
                            } else {
                                iter.add(d);
                                i++;
                            }
                        }
                    }
                    if(fail) {
                    }
                } else {
                    for (; j < i - 1; j++) {
                        x = jter.next();
                        y = jter.next();
                        if (PointUtils.crosses(x, y, a, d) || PointUtils.crosses(x, y, d, b)) {
                            fail = true;
                            break;
                        }
                        jter.previous();
                    }
                    if (!fail) {
                        x = jter.next();
                        y = jter.next();
                        if (PointUtils.crosses(x, y, d, b)) {
                            fail = true;
                        } else {
                            while (jter.hasNext()) {
                                x = jter.next();
                                if (jter.hasNext()) {
                                    y = jter.next();
                                } else {
                                    break;
                                }
                                if (PointUtils.crosses(x, y, a, d) || PointUtils.crosses(x, y, d, b)) {
                                    fail = true;
                                    break;
                                }
                                jter.previous();
                            }
                            if (!fail) {
                                y = this.getFirst();
                                if (PointUtils.crosses(x, y, a, d) || PointUtils.crosses(x, y, d, b)) {
                                    fail = true;
                                } else {
                                    iter.previous();
                                    iter.add(d);
                                    iter.next();
                                    i++;
                                }
                            }
                        }
                    }
                }
                iter.previous();
            }
        }
//        removeSelfIntersections();
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

//    private void removeSelfIntersections() {
//        System.out.println("Removing self intersections");
//        int count = 0;
//        int i = 0;
//        int j;
//
//        ListIterator<Point2D> jter = this.listIterator();
//        HashSet<Intersection> intersections = new HashSet<>();
//
//        Point2D pi;
//        Point2D qi;
//        Point2D pj;
//        Point2D qj;
//        Point2D swap;
//        for(ListIterator<Point2D> iter = this.listIterator(); iter.hasNext(); i++) {
//            pi = iter.next();
//            if(iter.hasNext()) {
//                qi = iter.next();
//                iter.previous();
//                j = 0;
//                jter = this.listIterator();
//                for(; jter.hasNext() && j < i - 1; j++) {
//                    pj = jter.next();
//                    qj = jter.next();
//                    jter.previous();
//                    if(PointUtils.crosses(pi.getX(), pi.getY(), qi.getX(), qi.getY(), pj.getX(), pj.getY(), qj.getX(), qj.getY())) {
//                        count++;
//                        Intersection is = new Intersection(new LineSegment(pi, qi), new LineSegment(pj, qj));
//                        if(intersections.contains(is)) {
//                            //Probable repeat intersection. Remove qi.
//                            iter.remove();
//                            //remove is from the set of known intersections
//                            intersections.remove(is);
//                            //move iter back one
//                            iter.previous();
//                            //decrement i
//                            i--;
//                            //reset jiter & j
//                            break;
//                        } else {
//                            intersections.add(is);
//                            //swap pi & qj
//                            swap = pi;
//                            iter.previous();
//                            iter.set(qj);
//                            jter.set(swap);
//                            //move jter back one
//                            jter.previous();
//                            //set iter = jter
//                            iter = jter;
//                            //set i = j
//                            i = j;
//                        }
//                    }
//                }
//            } else {
//                qi = this.getFirst();
//                j = 1;
//                jter = this.listIterator();
//                jter.next();
//                for(; jter.hasNext() && j < i - 1; j++) {
//                    pj = jter.next();
//                    qj = jter.next();
//                    jter.previous();
//                    if(PointUtils.crosses(pi.getX(), pi.getY(), qi.getX(), qi.getY(), pj.getX(), pj.getY(), qj.getX(), qj.getY())) {
//                        count++;
//                        Intersection is = new Intersection(new LineSegment(pi, qi), new LineSegment(pj, qj));
//                        if(intersections.contains(is)) {
//                            //Probable repeat intersection.
//                            //move iter back one
//                            iter.previous();
//                            //Remove qi.
//                            this.removeFirst();
//                            //remove is from the set of known intersections
//                            intersections.remove(is);
//                            //decrement i
//                            i--;
//                            //reset jiter & j
//                            break;
//                        } else {
//                            intersections.add(is);
//                            //swap pi and qj
//                            swap = pi;
//                            iter.previous();
//                            iter.set(qj);
//                            jter.set(swap);
//                            //move jter back one
//                            jter.previous();
//                            //set iter = jter
//                            iter = jter;
//                            //set i = j
//                            i = j;
//                        }
//                    }
//                }
//            }
//        }
//    }
}
