package run;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import ui.RenderModifier;
import ui.SceneGestures;
import ui.WorldCanvas;
import world.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by homosapien97 on 4/1/17.
 */
public class Main extends Application {
    public void start(Stage stage) {
//        Random seeder = new Random();
//        long seed = seeder.nextLong();
        long seed = -7686109781550864508l;
        //bad seeds: 5760597914071523077l
        //good seeds: 5107874807822077303l
        //              3644379409043933151l
        //              -7686109781550864508l
        Random rand = new Random(seed);
        System.out.println("Seed: " + seed);
        World world = new World(1, 8, rand);

        ArrayList<RenderModifier> renderOrder = new ArrayList<>();
        renderOrder.add(new RenderModifier(Continent.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.GRAY);
            }
        });
        renderOrder.add(new RenderModifier(Watershed.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                Random cr = new Random();
//                s.setFill(new Color(cr.nextDouble(), cr.nextDouble(), cr.nextDouble(), 0.8));
//                s.setFill(Color.TRANSPARENT);
                Color stroke = new Color(cr.nextDouble(), cr.nextDouble(), cr.nextDouble(), 0.8);
                Color fill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 0.5);
                s.setStroke(stroke);
                s.setFill(fill);
            }
        });
        renderOrder.add(new RenderModifier(MountainRange.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.RED);
            }
        });

        WorldCanvas canvas = new WorldCanvas(world, renderOrder);

//        for(Shape s : world.mountains()) {
//            canvas.getChildren().addAll(((Mountain) s).failures);
//        }
        for(Shape s : world.watersheds()) {
            Watershed w = (Watershed) s;
            Circle end = new Circle(w.endPoint.getX(), w.endPoint.getY(), 50.0);
            end.setFill(w.getFill());
            canvas.getChildren().add(end);
        }

        Scene scene = new Scene(canvas, 1024, 768);
        scene.setFill(Color.LIGHTBLUE);

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
