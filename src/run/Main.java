package run;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import ui.RenderModifier;
import ui.SceneGestures;
import ui.WorldCanvas;
import world.Continent;

import java.util.ArrayList;
import java.util.Random;

import ui.PannableCanvas;
import world.MountainRange;
import world.World;

/**
 * Created by homosapien97 on 4/1/17.
 */
public class Main extends Application {
    public void start(Stage stage) {
        Random seeder = new Random();
        long seed = seeder.nextLong();
//        long seed = 5760597914071523077l;
        //bad seeds: 5760597914071523077l
        Random rand = new Random(seed);
        System.out.println("Seed: " + seed);
        World world = new World(1, 5, rand);

        ArrayList<RenderModifier> renderOrder = new ArrayList<>();
        renderOrder.add(new RenderModifier(Continent.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.GRAY);
            }
        });
        renderOrder.add(new RenderModifier(MountainRange.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.RED);
            }
        });

        WorldCanvas canvas = new WorldCanvas(world, renderOrder);

        for(Shape s : world.mountains()) {
            canvas.getChildren().addAll(((MountainRange) s).failures);
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
