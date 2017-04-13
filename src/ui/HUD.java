package ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Created by homosapien97 on 4/13/17.
 */
public class HUD extends HBox {
    public HUD(Scene scene) {
        this.prefWidthProperty().bind(scene.widthProperty());
        this.prefHeight(100);
        this.setFillHeight(true);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

    }
}
