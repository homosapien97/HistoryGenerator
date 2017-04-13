package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ui.RenderModifier;
import ui.SceneGestures;
import ui.WorldCanvas;
import world.Continent;
import world.World;

import java.util.ArrayList;
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

//public class Test extends Application {
//    public void start(Stage stage) {
//        Random rand = new Random(1);
//        PannableCanvas canvas = new PannableCanvas();
//        DrawablePolygon p1 = new DrawablePolygon(new Point2D(0,0), 3, 100.0);
//        DrawablePolygon p2 = new DrawablePolygon(new Point2D(0,0), 3, 100.0);
//        p2.setScaleX(0.5);
//        p2.setScaleY(0.5);
//
//        p2.setTranslateX(-50);
//        p2.setRotate(90);
//        p1.setRotate(30);
//        Path p3 = (Path) Polygon.intersect(p1, p2);
//        p3.setFill(Color.RED);
//
//        ArrayList<Polygon> intersections = new ArrayList<>();
//        for (PathElement pe : p3.getElements()) {
//            if (pe instanceof MoveTo) {
//                MoveTo mt = (MoveTo) pe;
//                intersections.add(new Polygon());
//                intersections.get(intersections.size() - 1).getPoints().addAll(mt.getX(), mt.getY());
//            } else if (pe instanceof LineTo) {
//                LineTo lt = (LineTo) pe;
//                intersections.get(intersections.size() - 1).getPoints().addAll(lt.getX(), lt.getY());
//            }
//        }
//
//
//        canvas.getChildren().add(p1);
//        canvas.getChildren().add(p2);
//        canvas.getChildren().add(p3);
//
//        for(Polygon p : intersections) {
//            p.setFill(new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
//            canvas.getChildren().add(p);
//        }
//
//        Scene scene = new Scene(canvas, 1024, 768);
//
//        SceneGestures sceneGestures = new SceneGestures(canvas);
//        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
//        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
//        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
//
//        stage.setScene(scene);
//        stage.show();
//    }
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
//public class Test extends Application {
//    public void start(Stage stage) {
//        Random rand = new Random(2);
////        PannableCanvas canvas = new PannableCanvas();
//        Continent continent = new Continent(8, 1024.0, 2.7, 11, rand);
//        System.out.println("Continent has been created");
//        Mountain mountainRange = new Mountain(continent, 658.0, 2.0, 9, rand);
//        System.out.println("Mountains have been created");
//        mountainRange.setFill(Color.RED);
//
//        World world = new World();
//        world.add(continent);
//        world.add(mountainRange);
//
//        ArrayList<RenderModifier> renderOrder = new ArrayList<>();
//        renderOrder.add(new RenderModifier(Continent.class) {
//            @Override
//            public void changeRenderSettings(Shape s) {
//                s.setFill(Color.BROWN);
//            }
//        });
//        renderOrder.add(new RenderModifier(Mountain.class) {
//            @Override
//            public void changeRenderSettings(Shape s) {
//                s.setFill(Color.WHITE);
//            }
//        });
//
//        WorldCanvas canvas = new WorldCanvas(world, renderOrder);
//
//        Scene scene = new Scene(canvas, 1024, 768);
//        scene.setFill(Color.BLUE);
//
//        SceneGestures sceneGestures = new SceneGestures(canvas);
//        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
//        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
//        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
//
//        stage.setScene(scene);
//        stage.show();
//    }
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
public class Test extends Application {
    public void start(Stage stage) {
        Random seeder = new Random();
        long seed = seeder.nextLong();
        Random rand = new Random(seed);
        System.out.println("Seed: " + seed);
        World world = new World(1, 2, 2, rand);

        ArrayList<RenderModifier> renderOrder = new ArrayList<>();
        renderOrder.add(new RenderModifier(Continent.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.BROWN);
            }
        });

        WorldCanvas canvas = new WorldCanvas(world, renderOrder);

        Scene scene = new Scene(canvas, 1024, 768);
        scene.setFill(Color.BLUE);

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