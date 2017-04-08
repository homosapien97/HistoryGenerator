package ui;

import javafx.scene.shape.Shape;

/**
 * Created by homosapien97 on 4/7/17.
 */
public abstract class RenderModifier{
    public final Class class_;
    public RenderModifier(Class class_) {
        this.class_ = class_;
    }
    public abstract void changeRenderSettings(Shape s);
}
