package geometry;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javax.sound.sampled.LineEvent;
import java.util.*;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class PolygonUtils {

    public static boolean containsEveryVertex(Polygon container, Polygon contained) {
        for(Iterator<Double> iter = contained.getPoints().iterator(); iter.hasNext();) {
            double x = iter.next();
            double y = iter.next();
            if(!contains(container, x, y)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAVertex(Polygon container, Polygon contained) {
        for(Iterator<Double> iter = contained.getPoints().iterator(); iter.hasNext();) {
            double x = iter.next();
            double y = iter.next();
            if(contains(container, x, y)) {
                return true;
            }
        }
        return false;
    }

    public static double area(Polygon polygon) {
        if(polygon == null || polygon.getPoints().size() < 4) return 0.0;
        Iterator<Double> iterator = polygon.getPoints().iterator();
        double x1 = iterator.next();
        double y1 = iterator.next();
        double x2 = 0.0;
        double y2 = 0.0;
        double area = 0.0;
        while(iterator.hasNext()) {
            x2 = iterator.next();
            y2 = iterator.next();
            area += (x1 * y2 - y1 * x2) / 2.0;
            x1 = x2;
            y1 = y2;
        }
        iterator = polygon.getPoints().iterator();
        x1 = iterator.next();
        y1 = iterator.next();
        area += (x1 * y2 - y1 * x2) / 2.0;
        area = Math.abs(area);
        return area;
    }

    public static Point2D approxRandomInteriorPoint(Polygon container, double min, Random rand) {
        Bounds containerParentBounds = container.getBoundsInParent();
        if(containerParentBounds.getWidth() < min && containerParentBounds.getHeight() < min) {
            return new Point2D (containerParentBounds.getMinX() + containerParentBounds.getWidth() / 2,
                    containerParentBounds.getMinY() + containerParentBounds.getHeight() / 2);
        }
        double x;
        double y;
        do {
            x = containerParentBounds.getMinX() + rand.nextDouble() * containerParentBounds.getWidth();
            y = containerParentBounds.getMinY() + rand.nextDouble() * containerParentBounds.getHeight();
        } while(!PolygonUtils.contains(container, x, y));
        return new Point2D(x, y);
    }

    public static Point2D center(Polygon polygon) {
        List<Double> points = polygon.getPoints();
        double x = 0.0;
        double y = 0.0;
        final double div = 0.5 * (double) points.size();
        for(int i = 0; i < points.size(); i++) {
            x += points.get(i) / div;
            i++;
            y += points.get(i) / div;
        }
        return new Point2D(x, y);
    }

    public static double maxDistance(Polygon polygon, Point2D center) {
        List<Double> points = polygon.getPoints();
        double x;
        double y;
        double s = 0.0;
        double c;
        for(int i = 0; i < points.size(); i++) {
            x = points.get(i);
            i++;
            y = points.get(i);
            c = PointUtils.squareDistance(center, new Point2D(x, y));
            if(c > s) {
                s = c;
            }
        }
        return Math.sqrt(s);
    }

    public static LinkedList<Polygon> polygons(Shape s) {
        Path path = (Path) Shape.union(s, s);
        LinkedList<Polygon> ret = new LinkedList<>();
        for(PathElement pe : path.getElements()) {
            if(pe instanceof MoveTo) {
                ret.add(new Polygon());
            } else if(pe instanceof LineTo) {
                LineTo lt = (LineTo) pe;
                ret.getLast().getPoints().addAll(lt.getX(), lt.getY());
            }
        }
        return ret;
    }


    public static Polygon copy(Polygon p) {
        if(p == null) return null;
        Polygon ret = new Polygon();
//        for(double d : p.getPoints()) {
//            ret.getPoints().add(d);
//        }
        ret.getPoints().addAll(p.getPoints());
        ret.setTranslateX(p.getTranslateX());
        ret.setTranslateY(p.getTranslateY());
        ret.setRotate(p.getRotate());
        return ret;
    }

    public static Polygon nullSafeUnion(Polygon a, Polygon b) {
        a.setFill(Color.BLACK);
        b.setFill(Color.BLACK);
        a = copy(a);
        b = copy(b);
        if(a == null || a.getPoints().size() == 0) {
            if(b == null || b.getPoints().size() == 0) {
                System.out.println("Both merge participants were null");
                return null;
            } else {
                return copy(b);
            }
        } else {
            if(b == null || b.getPoints().size() == 0) {
                return copy(a);
            } else {
                try {
                    Path unionPath = (Path) (Polygon.union(a, b));
                    Polygon ret = new Polygon();
                    Polygon current = new Polygon();
                    int unions = 0;
                    for (PathElement pe : unionPath.getElements()) {
                        if (pe instanceof MoveTo) {
                            if (unions == 0) {
                                unions++;
                                MoveTo mt = (MoveTo) pe;
                                current.getPoints().addAll(mt.getX(), mt.getY());
                            } else {
                                if(current.getPoints().size() > ret.getPoints().size()) {
                                    ret = current;
                                }
                                current = new Polygon();
                                unions++;
                                MoveTo mt = (MoveTo) pe;
                                current.getPoints().addAll(mt.getX(), mt.getY());
                            }
                        } else if (pe instanceof LineTo) {
                            LineTo lt = (LineTo) pe;
                            current.getPoints().addAll(lt.getX(), lt.getY());
                        }
                        else if (pe instanceof ClosePath) {
                            if(current.getPoints().size() > ret.getPoints().size()) {
                                ret = current;
                            }
                        }
                    }
                    return ret;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Polygon merge has 0 points");
                    return null;
                }
            }
        }
    }

    public static Polygon union(Polygon a, Polygon b) {
        a.setFill(Color.BLACK);
        b.setFill(Color.BLACK);
        if(a == null || b == null || a.getPoints().size() == 0 || b.getPoints().size() == 0) {
            System.out.println("a merge participant was null/empty");
            return null;
        } else {
            try {
                Path unionPath = (Path) (Polygon.union(a, b));
                Polygon ret = new Polygon();
                Polygon current = new Polygon();
                int unions = 0;
                for (PathElement pe : unionPath.getElements()) {
                    if (pe instanceof MoveTo) {
                        if (unions == 0) {
                            unions++;
                            MoveTo mt = (MoveTo) pe;
                            current.getPoints().addAll(mt.getX(), mt.getY());
                        } else {
                            if(current.getPoints().size() > ret.getPoints().size()) {
                                ret = current;
                            }
                            current = new Polygon();
                            unions++;
                            MoveTo mt = (MoveTo) pe;
                            current.getPoints().addAll(mt.getX(), mt.getY());
                        }
                    } else if (pe instanceof LineTo) {
                        LineTo lt = (LineTo) pe;
                        current.getPoints().addAll(lt.getX(), lt.getY());
                    } else if (pe instanceof ClosePath) {
                        if(current.getPoints().size() > ret.getPoints().size()) {
                            ret = current;
                        }
                    }
                }
                return ret;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Polygon merge has 0 points");
                return null;
            }
        }
    }

    public static Polygon polygon(Shape s) {
        Path path = (Path) s;
        Polygon ret = new Polygon();
        for(PathElement pe : path.getElements()) {
            if(pe instanceof MoveTo) {
                ret = new Polygon();
                MoveTo mt = (MoveTo) pe;
                ret.getPoints().addAll(mt.getX(), mt.getY());
            } else if(pe instanceof LineTo){
                LineTo lt = (LineTo) pe;
                ret.getPoints().addAll(lt.getX(), lt.getY());
            }
        }
        return ret;
    }

    public static Polygon firstNonNull(HashSet<Polygon> polygons) {
        Iterator<Polygon> cIterator = polygons.iterator();
        if(cIterator.hasNext()) {
            Polygon c = null;
            do {
                c = cIterator.next();
            } while (cIterator == null && cIterator.hasNext() && c.getPoints().size() == 0);
            return c;
        }
        return null;
    }

    public static Polygon intersection(Polygon a, Polygon b) {
        a.setFill(Color.BLACK);
        b.setFill(Color.BLACK);
        if(a == null) {
            if(b == null) {
                return null;
            } else {
                if(b instanceof Polygon) {
                    return (Polygon) b;
                } else {
                    return polygon(b);
                }
            }
        } else if (b == null) {
            if(a instanceof Polygon) {
                return (Polygon) a;
            } else {
                return polygon(a);
            }
        } else {
            try {
                Path intersectionPath = (Path) (Polygon.intersect(a, b));
                Polygon ret = new Polygon();
                Polygon current = new Polygon();
                int intersections = 0;
                for (PathElement pe : intersectionPath.getElements()) {
                    if (pe instanceof MoveTo) {
                        if (intersections == 0) {
                            intersections++;
                            MoveTo mt = (MoveTo) pe;
                            current.getPoints().addAll(mt.getX(), mt.getY());
                        } else {
                            if(current.getPoints().size() > ret.getPoints().size()) {
                                ret = current;
                            }
                            current = new Polygon();
                            intersections++;
                            MoveTo mt = (MoveTo) pe;
                            current.getPoints().addAll(mt.getX(), mt.getY());
                        }
                    } else if (pe instanceof LineTo) {
                        LineTo lt = (LineTo) pe;
                        current.getPoints().addAll(lt.getX(), lt.getY());
                    } else if (pe instanceof ClosePath) {
                        if(current.getPoints().size() > ret.getPoints().size()) {
                            ret = current;
                        }
                    }
                }
                return ret;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Polygon intersection has 0 points");
                return null;
            }
        }
    }

    public static Polygon subtract(Polygon a, Polygon b) {
        a.setFill(Color.BLACK);
        b.setFill(Color.BLACK);
        Path subtractionPath = (Path) (Polygon.subtract(a, b));
        Polygon ret = new Polygon();
        boolean split = false;
        for(PathElement pe : subtractionPath.getElements()) {
            if(pe instanceof MoveTo) {
                if(split) {
                    break;
                } else {
                    split = true;
                    MoveTo mt = (MoveTo) pe;
                    ret.getPoints().addAll(mt.getX(), mt.getY());
                }
            } else if(pe instanceof  LineTo) {
                LineTo lt = (LineTo) pe;
                ret.getPoints().addAll(lt.getX(), lt.getY());
            }
        }
        if (ret.getPoints().size() == 0) {
            return null;
        }
        return ret;
    }

    public static HashSet<Polygon> multiSubtract(Polygon a, Polygon b) {
        if(a == null || a.getPoints().size() == 0) {
            if(b == null || b.getPoints().size() == 0) {
                return null;
            } else {
                HashSet<Polygon> ret = new HashSet<>();
                ret.add(b);
                return ret;
            }
        } else {
            a.setFill(Color.BLACK);
            b.setFill(Color.BLACK);
            if(b == null || b.getPoints().size() == 0) {
                HashSet<Polygon> ret = new HashSet<>();
                ret.add(a);
                return ret;
            } else {
                try {
                    Path subtPath = (Path) (Polygon.subtract(a, b));
                    Polygon current = null;
                    HashSet<Polygon> ret = new HashSet<>();
                    for (PathElement pe : subtPath.getElements()) {
                        if (pe instanceof MoveTo) {
                            if(current != null) {
                                ret.add(current);
                            }
                            current = new Polygon();
                            MoveTo mt = (MoveTo) pe;
                            current.getPoints().addAll(mt.getX(), mt.getY());
                        } else if (pe instanceof LineTo) {
                            LineTo lt = (LineTo) pe;
                            current.getPoints().addAll(lt.getX(), lt.getY());
                        } else if (pe instanceof ClosePath) {
                            ret.add(current);
                        }
                    }
                    return ret;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Polygon subtraction has 0 points");
                    return null;
                }
            }
        }
    }

    public static HashSet<Polygon> subtract(HashSet<Polygon> a, Polygon b) {
        HashSet<Polygon> result = new HashSet<>();
        for(Polygon p : a) {
            HashSet<Polygon> subt = multiSubtract(p, b);
            if(subt != null && subt.size() != 0) {
                result.addAll(subt);
            }
        }
        return result;
    }

    public static boolean contains(Polygon container, double x, double y) {
        Point2D pt = container.parentToLocal(x, y);
        return container.contains(pt);
    }
}
