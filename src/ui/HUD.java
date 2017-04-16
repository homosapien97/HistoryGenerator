package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import utilities.Describable;
import world.World;

/**
 * Created by homosapien97 on 4/13/17.
 */
public class HUD extends HBox {
//    String description;
    Text description = new Text();
    Button stepButton = new Button("Step");
    World world;
    public HUD(World world) {
        this.world = world;
        this.prefWidth(Double.MAX_VALUE);
        this.prefHeight(100);
        this.setFillHeight(true);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.description.setText("World");
        stepButton.setOnAction(buttonClickedEventHandler);
        this.getChildren().add(stepButton);
        this.getChildren().add(description);
    }
    public void setDescription(String description) {
//        this.description = description;
        this.description.setText(description);
    }

    private EventHandler<ActionEvent> buttonClickedEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
//            for(int i = event.getClickCount(); i > 0; i--) {
                world.step(1000);
//            }
            event.consume();
        }
    };
}
