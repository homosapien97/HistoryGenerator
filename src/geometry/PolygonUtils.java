package geometry;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javax.sound.sampled.LineEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    public static Polygon subtract(Shape a, Shape b) {
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

//    public static Polygon removeTransforms(Polygon p) {
//        if(p.getTranslateX() != 0.0 || p.getTranslateY() != 0.0 || p.getRotate() != 0.0 || p.getScaleX() != 1.0 || p.getScaleY() != 1.0) {
//            p.parentToLocal()
//            Polygon ret = copy(p);
//            p.setTranslateX(0.0);
//            p.setTranslateY(0.0);
//            p.setRotate(0.0);
//            p.setScaleX(1.0);
//            p.setScaleY(1.0);
//            p.getPoints().clear();
//            p.getPoints().addAll(ret.getPoints());
//        }
//        return p;
//    }

    public static boolean contains(Polygon container, double x, double y) {
//        System.out.println("using polgyon contains");
//        removeTransforms(container);
        Point2D pt = container.parentToLocal(x, y);
        return container.contains(pt);
        /*
        //this is so stupid
        Bounds cb = container.getBoundsInParent();
//        if(cb.getWidth() < 10.0 || cb.getHeight() < 10.0) {
//            return Math.abs(x - cb.getMinX() - cb.getWidth() / 2) < 2.0 && Math.abs(y - cb.getMinY() - cb.getHeight() / 2) < 2.0;
//        }
        Polygon rect = new Polygon();
        rect.getPoints().addAll(
                x - Double.MIN_VALUE, y - Double.MIN_VALUE,
                x + Double.MIN_VALUE, y - Double.MIN_VALUE,
                x + Double.MIN_VALUE, y + Double.MIN_VALUE,
                x - Double.MIN_VALUE, y + Double.MIN_VALUE
        );
        try {
            Polygon subt = PolygonUtils.subtract(rect, container);
            if(subt == null || subt.getPoints().size() == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        */
    }

    public static boolean contains(Shape container, double x, double y) {
        System.out.println("using shape contains");
        Polygon rect = new Polygon();
        rect.getPoints().addAll(
                x - 1.0 / 512.0, y - 1.0 / 512.0,
                x + 1.0 / 512.0, y - 1.0 / 512.0,
                x + 1.0 / 512.0, y + 1.0 / 512.0,
                x - 1.0 / 512.0, y + 1.0 / 512.0
        );
        try {
            Path subt = (Path) Shape.subtract(rect, container);
            for(PathElement pe : subt.getElements()) {
                if(pe instanceof LineTo) {
                    return false;
                }
            }
        } catch (Exception e){
            return false;
        }
        return true;
    }
}
