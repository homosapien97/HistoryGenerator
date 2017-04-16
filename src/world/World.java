package world;

import utilities.Dependable;
import utilities.Dependent;
import utilities.Update;
import geometry.BlobSettings;
import geometry.PolygonUtils;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.*;

/**
 * Created by homosapien97 on 4/7/17.
 */
public class World extends HashMap<Class, HashSet<Shape>> implements Dependable, Dependent {
    private static BlobSettings defaultContinentBlobSettings = new BlobSettings(8, 1024.0, 2.7, 10);
    int modificationCount = 0;
    LinkedList<Dependent> dependents = new LinkedList<>();
    HashSet<Dependable> dependencies = new HashSet<>();
    NameList cityNames;
    NameList riverNames;

    public void step(int num) {
        for(int i = 0; i < num; i++) {
            System.out.println("Stepping world");
            System.out.println("Doing city culture");
            for (Shape s : this.get(City.class)) {
                City city = (City) s;
                for (Shape z : city.inSphere()) {
                    if (z instanceof City) {
                        City c = (City) z;
                        city.influence(c);
                    } else if (z instanceof Watershed) {
                        Watershed w = (Watershed) z;
                        city.influence(w);
                    }
                }
                city.driftCulture();
            }
            System.out.println("Doing watershed culture");
            for (Shape s : this.get(Watershed.class)) {
                Watershed watershed = (Watershed) s;
                for (Shape z : watershed.inSphere()) {
                    if (z instanceof City) {
                        City c = (City) z;
                        watershed.influence(c);
                    } else if (z instanceof Watershed) {
                        Watershed w = (Watershed) z;
                        watershed.influence(w);
                    }
                }
            }
            for (Shape s : this.get(City.class)) {
                City city = (City) s;
                city.finalizeInfluence();
            }
            for (Shape s : this.get(Watershed.class)) {
                Watershed watershed = (Watershed) s;
                watershed.finalizeInfluence();
            }
        }
    }

    @Override
    public Set<Dependable> dependencies() {
        return dependencies;
    }

    @Override
    public void update(Dependable updater, Update update) {
        if(Update.class.equals(Watershed.class)) {
            Watershed updaterWatershed = (Watershed) updater;
            HashSet<Shape> cities = this.get(City.class);
            for (Shape shape : cities) {
                City city = (City) shape;
                if (city.watershed == updater && !updaterWatershed.cities.contains(city)) {
                    cities.remove(city);
                }
            }
            for (City city : updaterWatershed.cities) {
                cities.add(city);
            }
        }
    }


    public World(NameList cityNames, NameList riverNames, int numContinents, int numMountainsPerContinent, int numCitiesPerWatershed, Random rand) {
        super();
        for(int i = 0; i < numContinents; i++) {
            //TODO make this actually work nicely with more than 1 continent
            System.out.println("Making continent");
            Continent continent = new Continent(defaultContinentBlobSettings, rand);

            //Add watersheds
            System.out.println("Adding watersheds");
            HashSet<Polygon> continentCopy = new HashSet<>();
            continentCopy.add(PolygonUtils.copy(continent));
            HashSet<Polygon> safeCopy;
            do {
                Watershed w = new Watershed(riverNames.remove(rand.nextInt(riverNames.size() - 1) + 1), continent,
                        continentCopy, continent.scale, continent.deformations, rand);
                if(riverNames.size() < 2) {
                    riverNames = new NameList(NameType.RIVER);
                }
                try {
                    safeCopy = PolygonUtils.subtract(continentCopy, w);
                    continentCopy = safeCopy;
                } catch (Exception e) {
                }
                add(w);
                dependencies.add(w);
                w.addDependent(this);
            } while(continentCopy.size() != 0);

            System.out.println("Adding cities");
            if(!this.containsKey(City.class)) {
                this.put(City.class, new HashSet<>());
            }
            for(Shape s : super.get(Watershed.class)) {
                Watershed watershed = (Watershed) s;
                System.out.println("Adding " + Math.floor(watershed.area / 27000.0) + " cities");
                for(int j = 0; j < Math.floor(watershed.area / 27000.0); j++) {
//                    add(new City(watershed, rand));
                    watershed.addCity(cityNames.remove(rand.nextInt(cityNames.size() - 1) + 1));
                    if(cityNames.size() < 2) {
                        cityNames = new NameList(NameType.CITY);
                    }
                }
                this.get(City.class).addAll(watershed.cities);
                watershed.setAverageCulture();
            }

            //Add mountain ranges
            System.out.println("Adding mountains");
            continentCopy = new HashSet<>();
            continentCopy.add(PolygonUtils.copy(continent));
            for(int j = 0; j < numMountainsPerContinent; j++) {
                add(new MountainRange(continent, continentCopy, continent.scale, continent.deformations, rand));
            }
            System.out.println("Done with mountains");

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
            dependent.update(this, new Update(World.class));
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
        } else if (s instanceof Watershed) {
//            System.out.println("Adding a watershed");
            c = Watershed.class;
            if (containsKey(c) && get(c) != null) {
                Watershed watershed = (Watershed) s;
                HashSet<Shape> watersheds = this.get(c);
                Watershed toRemove = null;
                for (Shape shape : watersheds) {
                    Watershed extant = (Watershed) shape;
                    if (PolygonUtils.contains(extant, watershed.endPoint.getX(), watershed.endPoint.getY())) {
                        if (PolygonUtils.contains(watershed, extant.endPoint.getX(), extant.endPoint.getY())) {
                        }
                        Polygon safeUnion = PolygonUtils.union(watershed, extant);
                        if (safeUnion != null && safeUnion.getPoints().size() != 0) {
//                            System.out.println("Union successful");
                            toRemove = extant;
                            Watershed newWatershed = new Watershed(extant.name, extant.continent, safeUnion, extant.endPoint, extant.rand);
                            watersheds.add(newWatershed);
                            break;
                        }
                    }
                }
                if (toRemove != null) {
//                    System.out.println("removing old watershed");
                    watersheds.remove(toRemove);
                } else {
                    watersheds.add(watershed);
                }
            } else {
//                System.out.println("First watershed!");
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        } else if(s instanceof MountainRange) {
            c = MountainRange.class;
            if (containsKey(c)) {
                MountainRange mountainRange = (MountainRange) s;
                HashSet<Shape> mountainRanges = this.get(c);
                mountainRanges.add(mountainRange);
            } else {
                HashSet<Shape> al = new HashSet<>();
                al.add(s);
                this.put(c, al);
            }
        } else if(s instanceof City) {
            c = City.class;
            if(containsKey(c)) {
                this.get(c).add(s);
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
    public Set<Shape> unmodifiableWatersheds() {
        modificationCount--;
        return Collections.unmodifiableSet(super.get(Watershed.class));
    }
    public Set<Shape> unmodifiableOthers() {
        modificationCount--;
        return Collections.unmodifiableSet(super.get(Shape.class));
    }
}
