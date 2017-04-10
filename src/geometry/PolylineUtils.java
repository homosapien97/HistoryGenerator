package geometry;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.HashSet;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class PolylineUtils {
    public static Polyline intersection(Polyline a, HashSet<Polygon> b) {
        if(a == null || b == null || a.getPoints().size() == 0) {
            return null;
        }
        a.setFill(Color.BLACK);
        Shape totalIntersectionShape = null;
        boolean first = true;
        for(Polygon p : b) {
            if(p == null) {
                //TODO something?
            } else {
                p.setFill(Color.BLACK);
                Shape currentIntersectionPath = Polyline.intersect(a, p);
                if (first) {
                    totalIntersectionShape = currentIntersectionPath;
                    first = false;
                }
                Shape safeUnion = Shape.union(totalIntersectionShape, currentIntersectionPath);
                totalIntersectionShape = safeUnion;
            }
        }
        return largest(totalIntersectionShape);
    }

    public static Polyline largest(Shape s) {
        Path path = (Path) s;
        Polyline ret = new Polyline();
        Polyline current = new Polyline();
        for(PathElement pe : path.getElements()) {
            if(pe instanceof MoveTo) {
                if(current.getPoints().size() > ret.getPoints().size()) {
                    ret = current;
                }
                current = new Polyline();
                MoveTo mt = (MoveTo) pe;
                ret.getPoints().addAll(mt.getX(), mt.getY());
            } else if(pe instanceof LineTo){
                LineTo lt = (LineTo) pe;
                current.getPoints().addAll(lt.getX(), lt.getY());
            }
        }
        if(current.getPoints().size() > ret.getPoints().size()) {
            ret = current;
        }
        return ret;
    }

    public static Polyline intersection(Polyline a, Polygon b) {
        if(a == null || b == null || a.getPoints().size() == 0 || b.getPoints().size() == 0) {
            return null;
        } else {
            a.setFill(Color.BLACK);
            b.setFill(Color.BLACK);
            try {
                Path intersectionPath = (Path) (Polyline.intersect(a, b));
                Polyline ret = new Polyline();
                int intersections = 0;
                for (PathElement pe : intersectionPath.getElements()) {
//                    System.out.println("pe " + pe);
                    if (pe instanceof MoveTo) {
                        if (intersections == 0) {
                            intersections++;
                            MoveTo mt = (MoveTo) pe;
                            ret.getPoints().addAll(mt.getX(), mt.getY());
                        } else {
                            return ret;
                        }
                    } else if (pe instanceof LineTo) {
                        LineTo lt = (LineTo) pe;
                        ret.getPoints().addAll(lt.getX(), lt.getY());
                    }
                }
                return ret;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Polyline intersection has 0 points");
                return null;
            }
        }
    }
    public static Polyline intersection(Polyline a, Shape b) {
        a.setFill(Color.BLACK);
        b.setFill(Color.BLACK);
        if(a == null || b == null || a.getPoints().size() == 0) {
            return null;
        } else {
            try {
                Path intersectionPath = (Path) (Polyline.intersect(a, b));
                Polyline ret = new Polyline();
                int intersections = 0;
                for (PathElement pe : intersectionPath.getElements()) {
//                    System.out.println("pe " + pe);
                    if (pe instanceof MoveTo) {
                        if (intersections == 0) {
                            intersections++;
                            MoveTo mt = (MoveTo) pe;
                            ret.getPoints().addAll(mt.getX(), mt.getY());
                        } else {
                            return ret;
                        }
                    } else if (pe instanceof LineTo) {
                        LineTo lt = (LineTo) pe;
                        ret.getPoints().addAll(lt.getX(), lt.getY());
                    }
                }
                return ret;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Polyline intersection has 0 points");
                return null;
            }
        }
    }
}
