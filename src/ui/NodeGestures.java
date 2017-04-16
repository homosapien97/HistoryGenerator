package ui;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import utilities.Describable;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 * @author Jens-Peter Haack http://stackoverflow.com/users/4217556/jens-peter-haack
 */
public class NodeGestures {

    private DragContext nodeDragContext = new DragContext();

    PannableCanvas canvas;
    HUD hud;

    public NodeGestures( PannableCanvas canvas, HUD hud) {
        this.canvas = canvas;
        this.hud = hud;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
        return onMouseClickedEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

            // left mouse button => dragging
            if( !event.isPrimaryButtonDown())
                return;

            nodeDragContext.mouseAnchorX = event.getSceneX();
            nodeDragContext.mouseAnchorY = event.getSceneY();

            Node node = (Node) event.getSource();

            nodeDragContext.translateAnchorX = node.getTranslateX();
            nodeDragContext.translateAnchorY = node.getTranslateY();

        }

    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

            // left mouse button => dragging
            if( !event.isPrimaryButtonDown())
                return;

            double scale = canvas.getScale();

            Node node = (Node) event.getSource();

            node.setTranslateX(nodeDragContext.translateAnchorX + (( event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
            node.setTranslateY(nodeDragContext.translateAnchorY + (( event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));

            event.consume();

        }
    };

    private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            if(event.getClickCount() == 1) {
                if(!(event.getTarget() instanceof Button)) {
                    System.out.println("Selections: " + Selection.allSelections.size());
                    for (Selection s : Selection.allSelections) {
                        System.out.println("Clearing selection");
                        s.clear();
                    }
                }
                Node target = (Node) event.getTarget();
                if(target instanceof Describable) {
                    hud.setDescription(((Describable) target).getDescription());
                }
                if(target instanceof Selectable) {
                    ((Selectable) target).select();
                }
            }
            event.consume();
        }
    };
}
