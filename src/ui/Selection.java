package ui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import world.City;
import world.Watershed;

/**
 * Created by homosapien97 on 4/15/17.
 */
public class Selection extends HashSet<Shape> {
    private static int ids = 0;
    private static final HashMap<Class, Color> cityFills = new HashMap<>();
    private static final HashMap<Class, Color> watershedFills = new HashMap<>();
    static {
        cityFills.put(City.class, Color.GREEN);
        cityFills.put(Watershed.class, Color.CYAN);
        watershedFills.put(City.class, Color.GREEN);
        watershedFills.put(Watershed.class, Color.CYAN);
    }
    public static final HashSet<Selection> allSelections = new HashSet<>();
    public static final Selection citySelection = new Selection(cityFills, Color.DARKRED, true);
    public static final Selection watershedSelection = new Selection(watershedFills, Color.DARKRED, true);
//    private Color fill;
    private HashMap<Class, Color> fills;
    private Color uniqueFill;
    private boolean colorOverride;
    private HashMap<Shape, Paint> originalFill = new HashMap<>();
    private Shape unique = null;
    private int id;

    public Selection(HashMap<Class, Color> fills, Color uniqueFill, boolean override) {
        System.out.println("Creating selection");
        this.fills = fills;
        this.uniqueFill = uniqueFill;
        this.colorOverride = override;
        id = ids++;
        allSelections.add(this);
    }

    public boolean addUnique(Shape s) {
        if(unique == null) {
            if(this.contains(s)) {
                this.remove(s);
                s.setFill(originalFill.get(s));
            }
            originalFill.put(s, s.getFill());
            if (!colorOverride) {
                s.setFill(add(uniqueFill, (Color) s.getFill()));
            } else {
                s.setFill(uniqueFill);
            }
            unique = s;
            return super.add(s);
        } else {
            return false;
        }
    }

    public boolean removeUnique() {
        if(unique == null) {
            return false;
        } else {
            boolean ret = this.remove(unique);
            unique = null;
            return ret;
        }
    }

    @Override
    public boolean add(Shape s) {
        if(!colorOverride) {
            if(!this.contains(s)) {
                originalFill.put(s, s.getFill());
                s.setFill(add(fills.get(s.getClass()), (Color) s.getFill()));
            }
        } else {
            if(!this.contains(s)) {
                originalFill.put(s, s.getFill());
                s.setFill(fills.get(s.getClass()));
            }
        }
        return super.add(s);
    }
    @Override
    public boolean addAll(Collection<? extends Shape> collection) {
        boolean change = false;
        for(Shape s : collection) {
            change = add(s) || change;
        }
        return change;
    }
    @Override
    public void clear() {
        Iterator<Shape> iter = iterator();
        Shape s;
        while(iter.hasNext()) {
            s = iter.next();
            s.setFill(originalFill.get(s));
            iter.remove();
        }
        originalFill.clear();
        unique = null;
    }
    @Override
    public boolean remove(Object o) {
        if(super.remove(o)) {
            ((Shape) o).setFill(originalFill.get(o));
            originalFill.remove(o);
            if(o == unique) {
                unique = null;
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean change = false;
        for(Object o : collection) {
            change = remove(o) || change;
        }
        return change;
    }

    private Paint add(Color a, Color b) {
        if(a == null) {
            return b;
        } else if(b == null) {
            return a;
        }
        return new Color(clamp((a.getRed() + b.getRed())    / 2.0, 0.0, 1.0),
                clamp((a.getGreen() + b.getGreen())         / 2.0, 0.0, 1.0),
                clamp((a.getBlue() + b.getBlue())           / 2.0, 0.0, 1.0),
                clamp((a.getOpacity() + b.getOpacity())     / 2.0, 0.0, 1.0));
    }

    private double clamp(double c, double min, double max) {
        if(c < min) return min;
        if(c > max) return max;
        return c;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + id;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && id == ((Selection) o).id;
    }
}
