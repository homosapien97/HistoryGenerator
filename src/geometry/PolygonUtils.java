package geometry;

import javafx.geometry.Point2D;
import javafx.scene.shape.*;

import java.util.List;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class PolygonUtils {
    public static Point2D center(Polygon polygon) {
        List<Double> points = polygon.getPoints();
        double x = 0.0;
        double y = 0.0;
        final double div = (double) points.size();
        for(int i = 0; i < points.size(); i++) {
            x += points.get(i) / div;
            i++;
            y += points.get(i) / div;
        }
        return new Point2D(x, y);
    }

    public static Polygon union(Polygon a, Polygon b) {
        if(a == null || b == null || a.getPoints().size() == 0 || b.getPoints().size() == 0) {
            return null;
        } else {
            try {
                Path unionPath = (Path) (Polygon.union(a, b));
                Polygon ret = new Polygon();
                int unions = 0;
                for (PathElement pe : unionPath.getElements()) {
                    if (pe instanceof MoveTo) {
                        if (unions == 0) {
                            unions++;
                            MoveTo mt = (MoveTo) pe;
                            ret.getPoints().addAll(mt.getX(), mt.getY());
                        } else {
                            return null;
                        }
                    } else if (pe instanceof LineTo) {
                        LineTo lt = (LineTo) pe;
                        ret.getPoints().addAll(lt.getX(), lt.getY());
                    }
                }
//                this.setTranslateX(0.0);
//                this.setTranslateY(0.0);
//                this.getPoints().clear();
//                this.getPoints().addAll(union.getPoints());
//                return true;
                return ret;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Polygon union has 0 points");
                return null;
            }
        }
    }
}
