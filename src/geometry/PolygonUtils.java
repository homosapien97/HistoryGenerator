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

    public static Path multiUnion(Polygon a, Polygon b) {
        a.setFill(Color.BLACK);
        b.setFill(Color.BLACK);
        if(a == null || a.getPoints().size() == 0) {
            if(b == null || b.getPoints().size() == 0) {
                return null;
            } else {
                return (Path) Polygon.union(new Polygon(), b);
            }
        } else {
            if(b == null || b.getPoints().size() == 0) {
//                Polygon ret = new Polygon();
//                ret.getPoints().addAll(a.getPoints());
//                return ret;
                return (Path) Polygon.union(new Polygon(), a);
            } else {
                try {
                    return (Path) (Polygon.union(a, b));
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Polygon union has 0 points");
                    return null;
                }
            }
        }
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
                System.out.println("Both union participants were null");
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
                    System.out.println("Polygon union has 0 points");
                    return null;
                }
            }
        }
    }

    public static Polygon union(Polygon a, Polygon b) {
        a.setFill(Color.BLACK);
        b.setFill(Color.BLACK);
        if(a == null || b == null || a.getPoints().size() == 0 || b.getPoints().size() == 0) {
            System.out.println("a union participant was null/empty");
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
                System.out.println("Polygon union has 0 points");
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

    public static Polygon intersection(Shape a, Shape b) {
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

//    public static Polygon subtract(Shape a, Shape b) {
//        Path subtractionPath = (Path) (Polygon.subtract(a, b));
//        Polygon ret = new Polygon();
//        boolean split = false;
//        for(PathElement pe : subtractionPath.getElements()) {
//            if(pe instanceof MoveTo) {
//                if(split) {
//                    break;
//                } else {
//                    split = true;
//                    MoveTo mt = (MoveTo) pe;
//                    ret.getPoints().addAll(mt.getX(), mt.getY());
//                }
//            } else if(pe instanceof  LineTo) {
//                LineTo lt = (LineTo) pe;
//                ret.getPoints().addAll(lt.getX(), lt.getY());
//            }
//        }
//        if (ret.getPoints().size() == 0) {
//            return null;
//        }
//        return ret;
//    }

    public static Polygon subtract(Polygon a, Polygon b) {
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

//    public static boolean contains(Shape container, double x, double y) {
//        System.out.println("using shape contains");
//        Polygon rect = new Polygon();
//        rect.getPoints().addAll(
//                x - 1.0 / 512.0, y - 1.0 / 512.0,
//                x + 1.0 / 512.0, y - 1.0 / 512.0,
//                x + 1.0 / 512.0, y + 1.0 / 512.0,
//                x - 1.0 / 512.0, y + 1.0 / 512.0
//        );
//        try {
//            Path subt = (Path) Shape.subtract(rect, container);
//            for(PathElement pe : subt.getElements()) {
//                if(pe instanceof LineTo) {
//                    return false;
//                }
//            }
//        } catch (Exception e){
//            return false;
//        }
//        return true;
//    }
}