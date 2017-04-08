package world;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.*;

/**
 * Created by homosapien97 on 4/7/17.
 */
public class World extends HashMap<Class, HashSet<Shape>> {
    public void add(Shape s) {
        Class c;
        if(s instanceof Continent) {
            c = Continent.class;
            if(containsKey(c)) {
//                get(c).add(s);
                Continent continent = (Continent) s;
                HashSet<Shape> continents = get(c);
                Iterator<Shape> iterator = continents.iterator();
                while(iterator.hasNext()) {
                    Continent element = (Continent) iterator.next();
                    if(continent.union(element)) {
                        iterator.remove();
                    }
                }
                continents.add(continent);
            } else {
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        } else if(s instanceof MountainRange) {
            c = MountainRange.class;
            if(containsKey(c)) {
//                get(c).add(s);
                MountainRange mountainRange = (MountainRange) s;
                HashSet<Shape> mountains = get(c);
                Iterator<Shape> iterator = mountains.iterator();
                while(iterator.hasNext()) {
                    MountainRange element = (MountainRange) iterator.next();
                    if(mountainRange.union(element)) {
                        iterator.remove();
                    }
                }
                mountains.add(mountainRange);
            } else {
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        } else {
            c = Shape.class;
            if(containsKey(c)) {
                get(c).add(s);
            } else {
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        }
    }
    public HashSet<Shape> continents() {
        return this.get(Continent.class);
    }
    public HashSet<Shape> mountains() {
        return this.get(MountainRange.class);
    }
    public HashSet<Shape> others() {
        return this.get(Shape.class);
    }

}
