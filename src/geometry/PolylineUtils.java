package geometry;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class PolylineUtils {
    public static Polyline intersection(Polyline a, Polygon b) {
        a.setFill(Color.BLACK);
        b.setFill(Color.BLACK);
        if(a == null || b == null || a.getPoints().size() == 0 || b.getPoints().size() == 0) {
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
