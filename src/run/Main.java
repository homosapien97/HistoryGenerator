package run;

import geometry.VoronoiCell;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.*;
import world.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by homosapien97 on 4/1/17.
 */
public class Main extends Application {
    public void start(Stage stage) {
        Random seeder = new Random();
        long seed = seeder.nextLong();
//        long seed = -7686109781550864508l;
        Random rand = new Random(seed);
        System.out.println("Seed: " + seed);
        World world = new World(new NameList(NameType.CITY), new NameList(NameType.RIVER), 1, 8, 4, rand);

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
        renderOrder.add(new RenderModifier(VoronoiCell.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(((VoronoiCell) s).parent.culture.getFill());
            }
        });
        renderOrder.add(new RenderModifier(MountainRange.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.RED);
            }
        });
        renderOrder.add(new RenderModifier(City.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.ORANGE);
            }
        });

        WorldCanvas canvas = new WorldCanvas(world, renderOrder);
        HUD hud = new HUD(world);
        Group root = new Group();
        root.getChildren().add(canvas);
        root.getChildren().add(hud);
//        for(Shape s : world.mountains()) {
//            canvas.getChildren().addAll(((Mountain) s).failures);
//        }
//        for(Shape s : world.watersheds()) {
//            Watershed w = (Watershed) s;
//            Circle end = new Circle(w.endPoint.getX(), w.endPoint.getY(), 50.0);
//            end.setFill(w.getFill());
//            canvas.getChildren().add(end);
//        }

        Scene scene = new Scene(root, 1024, 768);

        scene.setFill(Color.LIGHTBLUE);

        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        NodeGestures nodeGestures = new NodeGestures(canvas, hud);
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, nodeGestures.getOnMouseClickedEventHandler());

        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
