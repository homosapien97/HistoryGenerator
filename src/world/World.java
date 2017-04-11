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
//    private static BlobSettings defaultMountainRangeBlobSettings = new BlobSettings(2, 228.0, 2.0, 8);

    public World(int numContinents, int numMountainsPerContinent, Random rand) {
        super();
        for(int i = 0; i < numContinents; i++) {
            //TODO make this actually work nicely with more than 1 continent
            Continent continent = new Continent(defaultContinentBlobSettings, rand);

            //Add watersheds
            HashSet<Polygon> continentCopy = new HashSet<>();
            continentCopy.add(PolygonUtils.copy(continent));
            HashSet<Polygon> safeCopy;
            do {
                Watershed w = new Watershed(continentCopy, continent.scale, continent.deformations, rand);
                try {
                    safeCopy = PolygonUtils.subtract(continentCopy, w);
                    System.out.println("safecopy size " + safeCopy.size());
                    continentCopy = safeCopy;
                } catch (Exception e) {
                    System.out.println("watershed subtraction fail");
                }
                add(w);
            } while(continentCopy.size() != 0);

            //Add mountain ranges
            continentCopy = new HashSet<>();
            continentCopy.add(PolygonUtils.copy(continent));
            for(int j = 0; j < numMountainsPerContinent; j++) {
                add(new MountainRange(continentCopy, continent.scale, continent.deformations, rand));
            }

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
        if (s instanceof Continent) {
            c = Continent.class;
            if (containsKey(c)) {
                Continent continent = (Continent) s;
                HashSet<Shape> continents = this.get(c);
                Iterator<Shape> iterator = continents.iterator();
                while (iterator.hasNext()) {
                    Continent element = (Continent) iterator.next();
                    if (continent.union(element)) {
                        iterator.remove();
                    }
                }
                continents.add(continent);
            } else {
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        } else if (s instanceof Mountain) {
            c = Mountain.class;
            if (containsKey(c)) {
                Mountain mountainRange = (Mountain) s;
                HashSet<Shape> mountains = this.get(c);
                Iterator<Shape> iterator = mountains.iterator();
                while (iterator.hasNext()) {
//                    Mountain element = (Mountain) iterator.next();
//                    if(mountainRange.union(element)) {
//                        iterator.remove();
//                    }
                    Polygon union = PolygonUtils.union(mountainRange, (Mountain) (iterator.next()));
                    if (union != null) {
                        iterator.remove();
                    }
                }
                mountains.add(mountainRange);
            } else {
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        } else if (s instanceof Watershed) {
            System.out.println("Adding a watershed");
            c = Watershed.class;
            if (containsKey(c) && get(c) != null) {
                Watershed watershed = (Watershed) s;
                HashSet<Shape> watersheds = this.get(c);
                Watershed toRemove = null;
                for (Shape shape : watersheds) {
                    Watershed extant = (Watershed) shape;
                    if (PolygonUtils.contains(extant, watershed.endPoint.getX(), watershed.endPoint.getY())) {
                        System.out.println("New watershed's endpoint is already in an extant watershed");
                        if (PolygonUtils.contains(watershed, extant.endPoint.getX(), extant.endPoint.getY())) {
                            System.out.println("Extant watershed's endpoint is also in new watershed!");
                        }
                        Polygon safeUnion = PolygonUtils.union(watershed, extant);
                        if (safeUnion != null && safeUnion.getPoints().size() != 0) {
                            System.out.println("Union successful");
                            toRemove = extant;
                            Watershed newWatershed = new Watershed(safeUnion, extant.endPoint);
                            watersheds.add(newWatershed);
                            break;
                        }
                    }
                }
                if (toRemove != null) {
                    System.out.println("removing old watershed");
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
        } else if(s instanceof MountainRange) {
            System.out.println("Adding a mountain range");
            c = MountainRange.class;
            if (containsKey(c)) {
                MountainRange mountainRange = (MountainRange) s;
                HashSet<Shape> mountainRanges = this.get(c);
//                Iterator<Shape> iterator = mountainRanges.iterator();
//                boolean cross = false;
//                while (iterator.hasNext()) {
//                    MountainRange next = (MountainRange) iterator.next();
//                    Polygon intersection = PolygonUtils.intersection(mountainRange, next);
//                    Polygon union = PolygonUtils.union(mountainRange, next);
//                    if ((intersection != null || intersection.getPoints().size() != 0)
//                            &&
//                            (union != null || union.getPoints().size() != 0)) {
//                        System.out.println("Mountain range crossed other mountain range");
////                        iterator.remove();
////                        mountainRange.setPolygon(union);
//                        next.setPolygon(union);
//                        cross = true;
//                        break;
//                    }
//                }
//                if(cross) {
//
//                }
                mountainRanges.add(mountainRange);
            } else {
                System.out.println("First mountain range");
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
    public Set<Shape> unmodifiableWatersheds() {
        modificationCount--;
        return Collections.unmodifiableSet(super.get(Watershed.class));
    }
    public Set<Shape> unmodifiableOthers() {
        modificationCount--;
        return Collections.unmodifiableSet(super.get(Shape.class));
    }
}
