package ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;

/**
 * Listeners for making the scene's canvas draggable and zoomable
 *
 * @author Jens-Peter Haack http://stackoverflow.com/users/4217556/jens-peter-haack
 */
public class SceneGestures {

    private static final double MAX_SCALE = 100.0d;
    private static final double MIN_SCALE = .001d;


    private DragContext sceneDragContext = new DragContext();

    PannableCanvas canvas;

    public SceneGestures( PannableCanvas canvas) {
        this.canvas = canvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

            // right mouse button => panning
            if( !event.isSecondaryButtonDown())
                return;

            sceneDragContext.mouseAnchorX = event.getSceneX();
            sceneDragContext.mouseAnchorY = event.getSceneY();

            sceneDragContext.translateAnchorX = canvas.getTranslateX();
            sceneDragContext.translateAnchorY = canvas.getTranslateY();

        }

    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

            // right mouse button => panning
            if( !event.isSecondaryButtonDown())
                return;

            canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
            canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

            event.consume();
        }
    };

    /**
     * Mouse wheel handler: zoom to pivot point
     */
    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

        /*
        @Override
        public void handle(ScrollEvent event) {
            double scale = canvas.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            scale *= Math.sqrt(Math.pow(1.01, event.getDeltaY()));

//            if (scale <= MIN_SCALE) {
//                scale = MIN_SCALE;
//            } else if (scale >= MAX_SCALE) {
//                scale = MAX_SCALE;
//            }

            double f = (scale / oldScale) - 1.0;

            double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth()/2.0 + canvas.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight()/2.0 + canvas.getBoundsInParent().getMinY()));
//
            canvas.setPivot(f*dx, f*dy);
//            canvas.setScale(scale);
            canvas.getTransforms().add(new Scale(scale, scale));
        }
        */
        @Override
        public void handle(ScrollEvent event) {

            double scale = 1.01;

            if(event.getDeltaY() < 0) {
                scale = 1.0 / scale;
            } else {
                scale = 1.0 * scale;
            }

//            double scale = canvas.getScale(); // currently we only use Y, same value is used for X
//            double oldScale = scale;
//
//            if (event.getDeltaY() < 0)
//                scale /= delta;
//            else
//                scale *= delta;
//
//            scale = clamp( scale, MIN_SCALE, MAX_SCALE);
//
//            double f = (scale / oldScale)-1.0;
//
//            double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth()/2.0 + canvas.getBoundsInParent().getMinX()));
//            double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight()/2.0 + canvas.getBoundsInParent().getMinY()));
//
//            canvas.setScale(scale);
//
//            // note: pivot value must be untransformed, i. e. without scaling
//            canvas.setPivot(f*dx, f*dy);
//
//            event.consume();

            Bounds canvasBounds = canvas.getBoundsInLocal();
            double centerX = canvasBounds.getWidth() / 2.0 + canvasBounds.getMinX();
            double centerY = canvasBounds.getHeight() / 2.0 + canvasBounds.getMinY();
            Point2D localClick = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());

            double originalDX = localClick.getX() - centerX;
            double originalDY = localClick.getY() - centerY;
            double ERROR = 1.0 / canvas.getScale();
            originalDX /= ERROR;
            originalDY /= ERROR;

            double dx = originalDX - scale * originalDX;
            double dy = originalDY - scale * originalDY;

            canvas.setScale(canvas.getScaleX() * scale);
            canvas.setTranslateX(canvas.getTranslateX() + dx);
            canvas.setTranslateY(canvas.getTranslateY() + dy);
//            System.out.println("scale, dx : " + canvas.getScale() + ", " + dx);
            event.consume();
        }
    };


    public static double clamp( double value, double min, double max) {

        if( Double.compare(value, min) < 0)
            return min;

        if( Double.compare(value, max) > 0)
            return max;

        return value;
    }
}
