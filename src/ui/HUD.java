package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import world.World;

import java.util.ArrayList;

/**
 * Created by homosapien97 on 4/13/17.
 */
public class HUD extends HBox {
//    String description;
    Text description = new Text();
    Button stepButton = new Button("STEP");
    Button fullscreenButton = new Button("FULLSCREEN");
    Button generalRenderButton = new Button("GENERAL");
    Button continentRenderButton = new Button("CONTINENT");
    Button riverRenderButton = new Button("RIVER");
    Button mountainRenderButton = new Button("MOUNTAIN");
    Button cityRenderButton = new Button("CITY");
    World world;
    Stage stage;
    WorldCanvas worldCanvas;
    boolean fullscreen = false;
    public HUD(World world, WorldCanvas worldCanvas, Stage stage) {
        this.world = world;
        this.prefWidth(Double.MAX_VALUE);
        this.prefHeight(100);
        this.setFillHeight(true);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.description.setText("WORLD");
        fullscreenButton.setOnAction(fullscreenButtonEventHandler);
        stepButton.setOnAction(stepButtonEventHandler);
        generalRenderButton.setOnAction(generalRenderButtonEventHandler);
        continentRenderButton.setOnAction(continentRenderButtonEventHandler);
        riverRenderButton.setOnAction(riverRenderButtonEventHandler);
        mountainRenderButton.setOnAction(mountainRenderButtonEventHandler);
        cityRenderButton.setOnAction(cityRenderButtonEventHandler);
        this.getChildren().add(fullscreenButton);
        this.getChildren().add(generalRenderButton);
        this.getChildren().add(continentRenderButton);
        this.getChildren().add(riverRenderButton);
        this.getChildren().add(mountainRenderButton);
        this.getChildren().add(cityRenderButton);
        this.getChildren().add(stepButton);
        this.getChildren().add(description);
        this.worldCanvas = worldCanvas;
        this.stage = stage;
    }
    public void setDescription(String description) {
        this.description.setText(description);
    }

    private EventHandler<ActionEvent> stepButtonEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            world.step(10);
            event.consume();
        }
    };

    private EventHandler<ActionEvent> fullscreenButtonEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            fullscreen = !fullscreen;
            stage.setFullScreen(fullscreen);
            event.consume();
        }
    };

    private EventHandler<ActionEvent> generalRenderButtonEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            worldCanvas.setRenderMode(RenderModifier.GENERAL);
        }
    };
    private EventHandler<ActionEvent> continentRenderButtonEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            worldCanvas.setRenderMode(RenderModifier.CONTINENT);

        }
    };
    private EventHandler<ActionEvent> riverRenderButtonEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            worldCanvas.setRenderMode(RenderModifier.RIVERS);

        }
    };
    private EventHandler<ActionEvent> mountainRenderButtonEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            worldCanvas.setRenderMode(RenderModifier.MOUNTAINS);

        }
    };
    private EventHandler<ActionEvent> cityRenderButtonEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            worldCanvas.setRenderMode(RenderModifier.CITIES);

        }
    };
}
