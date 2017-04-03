package run;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.stage.Stage;
import ui.SceneGestures;
import world.Continent;

import java.util.Random;

import ui.PannableCanvas;

/**
 * Created by homosapien97 on 4/1/17.
 */
public class Main extends Application {
    public void start(Stage stage) {
        PannableCanvas canvas = new PannableCanvas();
        Continent continent = new Continent(8, 1024.0, 5.0, 12, new Random(9));
        System.out.println("Continent has been created");
        canvas.getChildren().add(continent);
        Scene scene = new Scene(canvas, 1024, 768);

//        Group group = new Group();
//        group.getChildren().add(continent);
//        group.getChildren().add(canvas);
//        Scene scene = new Scene(group, 1024, 768);

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
