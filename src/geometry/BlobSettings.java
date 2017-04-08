package geometry;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class BlobSettings {
    public final int startPolygon;
    public final double scale;
    public final double jaggedness;
    public final int deformations;
    public BlobSettings(int startPolygon, double scale, double jaggedness, int deformations) {
        this.startPolygon = startPolygon;
        this.scale = scale;
        this.jaggedness = jaggedness;
        this.deformations = deformations;
    }
}
