package world;

import Utilities.Dependable;
import Utilities.Dependent;
import geometry.BlobSettings;
import geometry.PointUtils;
import geometry.PolygonUtils;
import javafx.geometry.Bounds;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.*;

/**
 * Created by homosapien97 on 4/7/17.
 */
public class World extends HashMap<Class, HashSet<Shape>> implements Dependable{
    int modificationCount = 0;
    LinkedList<Dependent> dependents = new LinkedList<>();
    private static BlobSettings defaultContinentBlobSettings = new BlobSettings(8, 1024.0, 2.7, 11);
    private static BlobSettings defaultMountainRangeBlobSettings = new BlobSettings(2, 228.0, 2.0, 8);

    public World(int numContinents, int numMountainsPerContinent, int numWatershedsPerContinent, Random rand) {
        super();
        for(int i = 0; i < numContinents; i++) {
            //TODO make this actually work nicely with more than 1 continent
            Continent continent = new Continent(defaultContinentBlobSettings, rand);
            for(int j = 0; j < numMountainsPerContinent; j++) {
                add(new Mountain(continent, defaultMountainRangeBlobSettings, rand));
            }
//            Polygon continentCopy = PolygonUtils.copy(continent);
            HashSet<Polygon> continentCopy = new HashSet<>();
            continentCopy.add(PolygonUtils.copy(continent));
//            Polygon safeCopy;
            HashSet<Polygon> safeCopy;
//            Bounds ccBounds;
            do {
//                System.out.println("Adding a watershed");
                Watershed w = new Watershed(continentCopy, continent.scale, continent.deformations, rand);
                try {
                    safeCopy = PolygonUtils.subtract(continentCopy, w);
                    System.out.println("safecopy size " + safeCopy.size());
                    continentCopy = safeCopy;
                } catch (Exception e) {
                    System.out.println("watershed subtraction fail");
//                    break;
                }
//                ccBounds = continentCopy.getBoundsInParent();
                add(w);
//            } while(continentCopy != null && continentCopy.getPoints().size() != 0 /*&& ccBounds.getWidth() * ccBounds.getHeight() > 1.0*/);
            } while(continentCopy.size() != 0);
//            System.out.print("Stopped making watersheds because " );
//            if(continentCopy == null) {
//                System.out.println("continent copy was null");
//            } else {
//                System.out.println("continent copy had no points");
//            }
            add(continent);
        }
    }

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
        } else if(s instanceof Mountain) {
            c = Mountain.class;
            if(containsKey(c)) {
                Mountain mountainRange = (Mountain) s;
                HashSet<Shape> mountains = this.get(c);
                Iterator<Shape> iterator = mountains.iterator();
                while(iterator.hasNext()) {
//                    Mountain element = (Mountain) iterator.next();
//                    if(mountainRange.union(element)) {
//                        iterator.remove();
//                    }
                    Polygon union = PolygonUtils.union(mountainRange, (Mountain) (iterator.next()));
                    if(union != null) {
                        iterator.remove();
                    }
                }
                mountains.add(mountainRange);
            } else {
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        } else if(s instanceof  Watershed) {
            System.out.println("Adding a watershed");
            c = Watershed.class;
            if(containsKey(c) && get(c) != null) {
                Watershed watershed = (Watershed) s;
                HashSet<Shape> watersheds = this.get(c);
                Watershed toRemove = null;
                for(Shape shape : watersheds) {
                    Watershed extant = (Watershed) shape;
                    if(PolygonUtils.contains(extant, watershed.endPoint.getX(), watershed.endPoint.getY())) {
                        System.out.println("New watershed's endpoint is already in an extant watershed");
                        if(PolygonUtils.contains(watershed, extant.endPoint.getX(), extant.endPoint.getY())) {
                            System.out.println("Extant watershed's endpoint is also in new watershed!");
                        }
                        Polygon safeUnion = PolygonUtils.union(watershed, extant);
                        if(safeUnion != null && safeUnion.getPoints().size() != 0) {
                            System.out.println("Union successful");
                            toRemove = extant;
                            watersheds.add(new Watershed(safeUnion, extant.endPoint));
                            break;
                        }
                    }
                }
                if(toRemove != null) {
                    watersheds.remove(toRemove);
                } else {
                    watersheds.add(watershed);
                }
            } else {
                System.out.println("First watershed!");
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
        return this.get(Mountain.class);
    }
    public Set<Shape> watersheds() {
        return this.get(Watershed.class);
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
        return Collections.unmodifiableSet(super.get(Mountain.class));
    }
    public Set<Shape> unmodifiableOthers() {
        modificationCount--;
        return Collections.unmodifiableSet(super.get(Shape.class));
    }
}
