import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by homosapien97 on 3/11/17.
 */
public class BoxList extends LinkedList<IntPair> {
    public final double scale;

    public BoxList(double scale) {
        this.scale = scale;
    }

    public BoxList(Collection<? extends Rectangle2D> c, double scale) {
        this.scale = scale;
        addAll(c, false);
    }

    public IntPair convert(Rectangle2D r) {
        return new IntPair((int)Math.floor(r.getMinX() / scale), (int)Math.floor(r.getMinY() / scale));
    }

    public IntPair convert(Point2D p) {
        return new IntPair((int)Math.floor(p.getX() / scale), (int)Math.floor(p.getY() / scale));
    }

    public Rectangle2D convert(IntPair i) {
        return new Rectangle2D(i.a * scale, i.b * scale, scale, scale);
    }

    public Point2D tr(IntPair i) {
        return new Point2D(i.a * scale + scale, i.b * scale + scale);
    }

    public Point2D br(IntPair i) {
        return new Point2D(i.a * scale + scale, i.b * scale);
    }

    public boolean isCorner(Point2D p) {
        return p.getX() % scale == 0.0 && p.getY() % scale == 0.0;
    }

    public boolean isSide(Point2D p) {
        return p.getX() % scale == 0.0 || p.getY() % scale == 0.0;
    }

    public boolean isVerticalSide(Point2D p) {
        return p.getX() % scale == 0.0;
    }

    public boolean isHorizontalSide(Point2D p) {
        return p.getY() % scale == 0.0;
    }

    private boolean verify(Rectangle2D r) {
        return scale == r.getWidth() && scale == r.getHeight();
    }

    public boolean add(Rectangle2D r) {
//        if(scale == r.getWidth() && scale == r.getHeight()) {
//            return this.add(convert(r));
//        }
//        return false;
        return verify(r) && this.add(convert(r));
    }

    public boolean add(Point2D p) {
        return this.add(convert(p));
    }

    public void add(int index, Rectangle2D r) {
//        if(scale == r.getWidth() && scale == r.getHeight()) {
//            this.add(index, convert(r));
//        }
        if(verify(r)) {
            this.add(index, convert(r));
        }
        throw new IllegalArgumentException("Cannot add box of dimension " + r.getWidth() + " x " + r.getHeight() + " to a BoxList of scale " + scale);
    }

    public void add(int index, Point2D p) {
        this.add(index, convert(p));
    }

    public boolean addAll(Collection<? extends Rectangle2D> c, boolean dummy) {
        boolean success = true;
        for(Rectangle2D r : c) {
            success = this.add(r) && success;
        }
        return success;
    }

    public boolean addAll(Collection<? extends Point2D> c, char dummy) {
        boolean success = true;
        for(Point2D p : c) {
            success = this.add(p) && success;
        }
        return success;
    }

    public boolean addAll(int index, Collection<? extends Rectangle2D> c, boolean dummy) {
        boolean success = true;
        ListIterator<IntPair> iter = this.listIterator(index);
        for(Rectangle2D r : c) {
//            if(scale == r.getWidth() && scale == r.getHeight()) {
//                iter.add(convert(r));
//            } else {
//                success = false;
//            }
            if(verify(r)) {
                iter.add(convert(r));
            } else {
                success = false;
            }
        }
        return success;
    }

    public boolean addAll(int index, Collection<? extends Point2D> c, char dummy) {
        boolean success = true;
        ListIterator<IntPair> iter = this.listIterator(index);
        for(Point2D p : c) {
            iter.add(convert(p));
        }
        return true;
    }

    public boolean contains(Rectangle2D r) {
//        if(scale == r.getWidth() && scale == r.getHeight()) {
//            return this.contains(convert(r));
//        }
//        return false;
        return verify(r) && this.contains(convert(r));
    }

    public boolean contains(Point2D p) {
        return this.contains(convert(p));
    }

    public int indexOf(Rectangle2D r) {
//        if(scale == r.getWidth() && scale == r.getHeight()) {
//            return indexOf(this.convert(r));
//        }
        return verify(r) ? indexOf(convert(r)) : -1;
    }

    public int indexOf(Point2D p) {
        return indexOf(convert(p));
    }

    public int lastIndexOf(Rectangle2D r) {
        return verify(r) ? lastIndexOf(convert(r)) : -1;
    }

    public int lastIndexOf(Point2D p) {
        return lastIndexOf(convert(p));
    }

    public boolean offer(Rectangle2D r) {
        return verify(r) && offer(convert(r));
    }

    public boolean offer(Point2D p) {
        return offer(convert(p));
    }

    public boolean offerFirst(Rectangle2D r) {
        return verify(r) && offerFirst(convert(r));
    }

    public boolean offerFirst(Point2D p) {
        return offerFirst(convert(p));
    }

    public boolean offerLast(Rectangle2D r) {
        return verify(r) && offerLast(convert(r));
    }

    public boolean offerLast(Point2D p) {
        return offerLast(convert(p));
    }

    public void push(Rectangle2D r) {
        if(verify(r)) push(convert(r));
    }

    public void push(Point2D p) {
        push(convert(p));
    }

    public boolean remove(Rectangle2D r) {
        return verify(r) && remove(convert(r));
    }

    public boolean remove(Point2D p) {
        return remove(convert(p));
    }

    public boolean removeFirstOccurence(Rectangle2D r) {
        return verify(r) && removeFirstOccurrence(convert(r));
    }

    public boolean removeFirstOccurence(Point2D p) {
        return removeFirstOccurrence(convert(p));
    }

    public boolean removeLastOccurence(Rectangle2D r) {
        return verify(r) && removeLastOccurrence(convert(r));
    }

    public boolean removeLastOccurence(Point2D p) {
        return removeLastOccurrence(convert(p));
    }

    public IntPair set(int index, Rectangle2D r) {
        if(verify(r)) {
            return set(index, convert(r));
        }
        throw new IllegalArgumentException("Cannot add box of dimension " + r.getWidth() + " x " + r.getHeight() + " to a BoxList of scale " + scale);
    }

    public IntPair set(int index, Point2D p) {
        return set(index, convert(p));
    }

    public int hashCode() {
        return (((int) scale) << 16) + (((int) scale) >>> 16) + super.hashCode();
    }

    public boolean equals(Object o) {
        if(o instanceof BoxList) {
            if(this == o) {
                return true;
            } else if(this.hashCode() == o.hashCode()) {
                BoxList rhs = (BoxList) o;
                return scale == rhs.scale && super.equals(rhs);
            }
        }
        return false;
    }
}
