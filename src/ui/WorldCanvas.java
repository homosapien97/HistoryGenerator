package ui;

import Utilities.Dependent;
import javafx.scene.shape.Shape;
import world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by homosapien97 on 4/7/17.
 */
public class WorldCanvas extends PannableCanvas implements Dependent{
    World world;
    List<RenderModifier> renderOrder;
    List<Object> dependencies = new ArrayList<>(1);
//    ArrayList<Shape> toRender;
    public WorldCanvas(World world, List<RenderModifier> renderOrder) {
        this.world = world;
        this.renderOrder = renderOrder;
        dependencies.add(world);
        refreshChildren();
    }
    private void refreshChildren() {
        this.getChildren().clear();
        for(RenderModifier rm : renderOrder) {
            try {
                for (Shape s : world.getUnmodifiable(rm.class_)) {
                    rm.changeRenderSettings(s);
                    this.getChildren().add(s);
                }
            } catch (NullPointerException e) {
                System.err.println("NPE CLASS: " + rm.class_);
            }
        }
    }
    @Override
    public List<Object> dependencies() {
        return dependencies;
    }
    @Override
    public void update() {
        refreshChildren();
    }
}
