package world;

import geometry.Blob;
import geometry.BlobSettings;
import geometry.RegularPolygon;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by homosapien97 on 4/6/17.
 */
public class MountainRange extends Polygon {
    public ArrayList<Polygon> failures = new ArrayList<>();
    public static final int MAX_SHRINKS = 4;
    public static final double SHRINK_FACTOR = 0.75;
    Continent continent;

    private void setSelfToPolygon(Polygon polygon) {
        System.out.println("Setting self to polygon");
        this.getPoints().clear();
        this.setTranslateX(0.0);
        this.setTranslateY(0.0);
        this.setRotate(0.0);
        this.getPoints().addAll(polygon.getPoints());
    }
    private void setSelfToNewBlob(double scale, double jaggedness, int deformations, Random rand) {
        this.getPoints().clear();
        this.setTranslateX(0.0);
        this.setTranslateY(0.0);
        this.setRotate(0.0);
        this.getPoints().addAll((new Blob(2, scale, jaggedness, deformations, rand)).getDoubleList());
    }

    private void transformSelfNearContinent(Continent continent, Random rand) {
        this.setRotate(360);
        Bounds continentBounds = continent.getBoundsInParent();
        Bounds mountainBounds = this.getBoundsInParent();
        this.setTranslateX(-mountainBounds.getMinX() + mountainBounds.getWidth() / 2 + continentBounds.getMinX() + rand.nextDouble() * continentBounds.getWidth());
        this.setTranslateY(-mountainBounds.getMinY() + mountainBounds.getHeight() / 2 + continentBounds.getMinY() + rand.nextDouble() * continentBounds.getHeight());
    }

    private Polygon subtractionOfSelfAndContinent(Continent continent) {
        Path subtractionPath = (Path) (Polygon.subtract(this, continent));
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

    private Polygon intersectionOfSelfAndContinent(Continent continent) {
        Path intersectionPath = (Path) (Polygon.intersect(this, continent));
//            ArrayList<Polygon> intersections = new ArrayList<>();
        Polygon ret = new Polygon();
        boolean split = false;
        for (PathElement pe : intersectionPath.getElements()) {
            if (pe instanceof MoveTo) {
                if (split) {
                    return null;
                } else {
                    split = true;
                    MoveTo mt = (MoveTo) pe;
                    ret.getPoints().addAll(mt.getX(), mt.getY());
                }
            } else if (pe instanceof LineTo) {
                LineTo lt = (LineTo) pe;
                ret.getPoints().addAll(lt.getX(), lt.getY());
            }
        }
        if(ret.getPoints().size() == 0) {
            return null;
        }
        return ret;
    }

    private boolean shrinkSelfToFit(Continent continent) {
        for(int i = 0; i < MAX_SHRINKS; i++) {
            Polygon intersectionPolygon = intersectionOfSelfAndContinent(continent);
            if(intersectionPolygon == null) {
                return false;
//            } else if(this.getPoints().size() == intersectionPolygon.getPoints().size()) {
            } else {
                Polygon subtractionPolygon = subtractionOfSelfAndContinent(continent);
                if(subtractionPolygon == null) {
                    //We fit
                    setSelfToPolygon(intersectionPolygon);
                    return true;
                }
            }
            this.setScaleX(this.getScaleX() * SHRINK_FACTOR);
            this.setScaleY(this.getScaleY() * SHRINK_FACTOR);

        }
        Polygon intersectionPolygon = intersectionOfSelfAndContinent(continent);
        if(intersectionPolygon == null) {
            return false;
        }
        if(this.getPoints().size() == intersectionPolygon.getPoints().size()) {
            //We fit
            setSelfToPolygon(intersectionPolygon);
            return true;
        }
        Random cr = new Random();
        intersectionPolygon.setFill(new Color(cr.nextDouble(), cr.nextDouble(), cr.nextDouble(), 0.5));
        failures.add(intersectionPolygon);
        return false;
    }

    /**
     * For use when merging mountains
     * @param continent
     * @param polygon
     */
    public MountainRange(Continent continent, Polygon polygon) {
        this.continent = continent;
        setSelfToPolygon(polygon);
    }
    public MountainRange(Continent continent, BlobSettings blobSettings, Random rand) {
        this(continent, blobSettings.scale, blobSettings.jaggedness, blobSettings.deformations, rand);
    }

    public MountainRange(Continent continent, double scale, double jaggedness, int deformations, Random rand) {
        super();
        this.continent = continent;
//        boolean done = false;
        int tries = 0;
        do {
            setSelfToNewBlob(scale, jaggedness, deformations, rand);
            transformSelfNearContinent(continent, rand);
            tries++;
        } while(!shrinkSelfToFit(continent) && tries < 50);

    }
//    public boolean union(MountainRange m) {
//        if(m == null || m.getPoints().size() == 0) {
//            System.out.println("null mountain tried to union");
//            return false;
//        } else {
//            try {
//                Path unionPath = (Path) (Polygon.union(this, m));
//                Polygon union = new Polygon();
//                int unions = 0;
//                for (PathElement pe : unionPath.getElements()) {
//                    if (pe instanceof MoveTo) {
//                        if (unions == 0) {
//                            unions++;
//                            MoveTo mt = (MoveTo) pe;
//                            union.getPoints().addAll(mt.getX(), mt.getY());
//                        } else {
//                            return false;
//                        }
//                    } else if (pe instanceof LineTo) {
//                        LineTo lt = (LineTo) pe;
//                        union.getPoints().addAll(lt.getX(), lt.getY());
//                    }
//                }
//                this.setTranslateX(0.0);
//                this.setTranslateY(0.0);
//                this.getPoints().clear();
//                this.getPoints().addAll(union.getPoints());
//                return true;
//            } catch (ArrayIndexOutOfBoundsException e) {
//                System.out.println("union has 0 points");
//                return false;
//            }
//        }
//    }
}
