package ui;

import utilities.Dependable;
import utilities.Dependent;
import utilities.Update;
import javafx.scene.shape.Shape;
import world.World;

import java.util.HashSet;
import java.util.List;

/**
 * Created by homosapien97 on 4/7/17.
 */
public class WorldCanvas extends PannableCanvas implements Dependent {
    World world;
    List<RenderModifier> renderOrder;
    HashSet<Dependable> dependencies = new HashSet<>();
//    ArrayList<Shape> toRender;
    public WorldCanvas(World world, List<RenderModifier> renderOrder) {
        this.world = world;
        this.renderOrder = renderOrder;
        dependencies.add(world);
        refreshChildren();
    }
    public boolean setRenderMode(List<RenderModifier> rm) {
        if(renderOrder == rm) return false;
        renderOrder = rm;
        refreshChildren();
        return true;
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
    public HashSet<Dependable> dependencies() {
        return dependencies;
    }
    @Override
    public void update(Dependable updater, Update update) {
        if(update.updater.equals(World.class)) {
            refreshChildren();
        }
    }
}
