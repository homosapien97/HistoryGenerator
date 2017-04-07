package test;

import geometry.LineSegment;
import geometry.PointUtils;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ui.PannableCanvas;
import ui.SceneGestures;
import world.Continent;

import java.util.Random;

/**
 * Created by homosapien97 on 4/2/17.
 */
//public class Test {
//    public static void main(String[] args) {
////        LineSegment a = new LineSegment(0.0, 0.0, 1.0, 0.0);
////        LineSegment b = new LineSegment(0.5, 0.5, 0.5, -0.5);
////        LineSegment c = new LineSegment(0.0, 1.0, 1.0, 1.0);
////        System.out.println("a intersect b? " + PointUtils.crosses(a, b));
////        System.out.println("a intersect c? " + PointUtils.crosses(a, c));
//    }
//}

public class Test extends Application {
    public void start(Stage stage) {
        PannableCanvas canvas = new PannableCanvas();
        DrawablePolygon p1 = new DrawablePolygon(new Point2D(0,0), 3, 100.0);
        DrawablePolygon p2 = new DrawablePolygon(new Point2D(0,0), 3, 100.0);
        p2.setScaleX(0.5);
        p2.setScaleY(0.5);

//        p2.setTranslateX(-50);
        p2.setRotate(90);
        Path p3 = (Path) Polygon.intersect(p1, p2);
        p3.setFill(Color.RED);
        System.out.println("Polygon has been created");
        canvas.getChildren().add(p1);
        canvas.getChildren().add(p2);
        canvas.getChildren().add(p3);
        Scene scene = new Scene(canvas, 1024, 768);

        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}