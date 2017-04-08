package world;

import Utilities.Dependable;
import Utilities.Dependent;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.*;

/**
 * Created by homosapien97 on 4/7/17.
 */
public class World extends HashMap<Class, HashSet<Shape>> implements Dependable{
    int modificationCount = 0;
    ArrayList<Dependent> dependents = new ArrayList<>();

    @Override
    public void addDependent(Dependent dependent) {
        dependents.add(dependent);
    }

    @Override
    public void updateDependents() {
        for(Dependent dependent : dependents) {
            dependent.update();
        }
    }

    public void add(Shape s) {
        Class c;
        if(s instanceof Continent) {
            c = Continent.class;
            if(containsKey(c)) {
                Continent continent = (Continent) s;
                HashSet<Shape> continents = this.get(c);
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
                MountainRange mountainRange = (MountainRange) s;
                HashSet<Shape> mountains = this.get(c);
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
                this.get(c).add(s);
            } else {
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        }
        updateDependents();
    }

    @Override
    public HashSet<Shape> get(Object o) {
        modificationCount++;
        return super.get(o);
    }

    public Set<Shape> getUnmodifiable(Object o) {
        return Collections.unmodifiableSet(super.get(o));
    }

    public Set<Shape> continents() {
        return this.get(Continent.class);
    }
    public Set<Shape> mountains() {
        return this.get(MountainRange.class);
    }
    public Set<Shape> others() {
        return this.get(Shape.class);
    }

    public Set<Shape> unmodifiableContinents() {
        modificationCount--;
        return Collections.unmodifiableSet(super.get(Continent.class));
    }
    public Set<Shape> unmodifiableMountains() {
        modificationCount--;
        return Collections.unmodifiableSet(super.get(MountainRange.class));
    }
    public Set<Shape> unmodifiableOthers() {
        modificationCount--;
        return Collections.unmodifiableSet(super.get(Shape.class));
    }
}
