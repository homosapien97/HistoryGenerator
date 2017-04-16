package geometry;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import utilities.Centralizable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by homosapien97 on 4/15/17.
 */
public class GridMap<T extends Shape & Centralizable> extends HashMap<Point2D, HashSet<T> > {
    public final double gridSize;
    public GridMap(double gridSize) {
        this.gridSize = gridSize;
    }

    public void remove(T c) {
        Point2D key = c.getCenter();
        key = new Point2D(Math.floor(key.getX() / gridSize), Math.floor(key.getY() / gridSize));
        HashSet<Point2D> circle = circle(key, c.getRadius());
        for(Point2D p : circle) {
            if(containsKey(p)) {
                get(p).remove(c);
            } else {
                return;
            }
        }
    }

    public void add(T c) {
        Point2D key = c.getCenter();
//        key = new Point2D(Math.floor(key.getX() / gridSize), Math.floor(key.getY() / gridSize));
//        put(key, c);
        HashSet<Point2D> circle = circle(key, c.getRadius());
        for(Point2D p : circle) {
//            put(p, c);
            if(containsKey(p)) {
                get(p).add(c);
            } else {
                HashSet<T> h = new HashSet<>();
                h.add(c);
                put(p, h);
            }
        }
    }

    public HashSet<Centralizable> get(Point2D center, double radius) {
        HashSet<Point2D> circle = circle(center, radius);
        HashSet<Centralizable> ret = new HashSet<>();
        for(Point2D p : circle) {
            if(containsKey(p)) {
                ret.addAll(get(p));
            }
        }
        return ret;
    }

    private HashSet<Point2D> circle(Point2D center, double radius) {
        HashSet<Point2D> ret = new HashSet<>();
        int rg = (int) (Math.ceil(radius / gridSize));
        for(int i = -rg; i <= rg; i++) {
            for(int j = 0; j * j <= rg * rg - i * i; j++) {
                ret.add(new Point2D(Math.floor(center.getX() / gridSize + i), Math.floor(center.getY() / gridSize + j)));
                ret.add(new Point2D(Math.floor(center.getX() / gridSize + i), Math.floor(center.getY() / gridSize - j)));
            }
        }
        return ret;
    }

    private HashSet<Point2D> circle(int x, int y, int radius) {

        HashSet<Point2D> ret = new HashSet<>();
        for(int i = -radius; i <= radius; i++) {
            for(int j = 0; j * j <= radius * radius - i * i; j++) {
                ret.add(new Point2D(x + i, y + j));
                ret.add(new Point2D(x + i, y - j));
            }
        }
        return ret;
    }
}
