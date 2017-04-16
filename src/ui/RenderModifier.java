package ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import world.City;
import world.Continent;
import world.MountainRange;
import world.Watershed;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by homosapien97 on 4/7/17.
 */
public abstract class RenderModifier{
    public static final ArrayList<RenderModifier> GENERAL = new ArrayList<>();
    public static final ArrayList<RenderModifier> CONTINENT = new ArrayList<>();
    public static final ArrayList<RenderModifier> RIVERS = new ArrayList<>();
    public static final ArrayList<RenderModifier> MOUNTAINS = new ArrayList<>();
    public static final ArrayList<RenderModifier> CITIES = new ArrayList<>();
    static {
        GENERAL.add(new RenderModifier(Continent.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.GRAY);
            }
        });
        GENERAL.add(new RenderModifier(Watershed.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                Random cr = new Random();
                Color stroke = new Color(cr.nextDouble(), cr.nextDouble(), cr.nextDouble(), 0.8);
                Color fill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 0.5);
                s.setStroke(stroke);
                s.setFill(fill);
            }
        });
        GENERAL.add(new RenderModifier(MountainRange.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.RED);
            }
        });
        GENERAL.add(new RenderModifier(City.class) {
            @Override
            public void changeRenderSettings(Shape s) {
//                s.setFill(Color.ORANGE);
                s.setFill(((City) s).culture.getFill());
            }
        });
        CONTINENT.add(new RenderModifier(Continent.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.SANDYBROWN);
            }
        });
        RIVERS.add(new RenderModifier(Watershed.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                Random cr = new Random();
                Color stroke = new Color(cr.nextDouble(), cr.nextDouble(), cr.nextDouble(), 1.0);
                Color fill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 1.0);
                s.setStroke(stroke);
                s.setFill(fill);
            }
        });
        MOUNTAINS.add(new RenderModifier(Continent.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.BEIGE);
            }
        });
        MOUNTAINS.add(new RenderModifier(MountainRange.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.DARKOLIVEGREEN);
            }
        });
        CITIES.add(new RenderModifier(Continent.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.BEIGE);
            }
        });
        CITIES.add(new RenderModifier(Watershed.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(Color.TRANSPARENT);
                s.setStrokeWidth(10.0);
                s.setStroke(Color.RED);
            }
        });
        CITIES.add(new RenderModifier(City.class) {
            @Override
            public void changeRenderSettings(Shape s) {
                s.setFill(((City) s).culture.getFill());
            }
        });
    }
    public final Class class_;
    public RenderModifier(Class class_) {
        this.class_ = class_;
    }
    public abstract void changeRenderSettings(Shape s);
}
