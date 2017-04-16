package voronoi;

import geometry.PolygonUtils;
import geometry.VoronoiCell;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import world.City;

import java.util.*;

/**
 * Created by homosapien97 on 4/16/17.
 */
public class VoronoiPolygonSet extends HashSet<Shape> {
    Voronoi voronoi;
    public VoronoiPolygonSet(double[] xValuesIn, double[] yValuesIn, double minX, double maxX, double minY, double maxY, Polygon background, List<City> parents) {
        voronoi = new Voronoi(0.00001); //Should this be min value?
        List<GraphEdge> edges = voronoi.generateVoronoi(xValuesIn,yValuesIn, minX, maxX, minY, maxY);
//        HashSet<Site> visited = new HashSet<>();
        HashSet<GraphEdge>[] siteEdges = new HashSet[voronoi.getNumSites()];
        for(GraphEdge edge : edges) {
//            if(siteEdges.size() < edge.site1) {
//                siteEdges.add(edge.site1, new HashSet<>());
//            }
//            HashSet<GraphEdge> s1 = siteEdges.get(edge.site1);
//            if(siteEdges.size() < edge.site2) {
//                siteEdges.add(edge.site2, new HashSet<>());
//            }
            if(siteEdges[edge.site1] == null) {
                siteEdges[edge.site1] = new HashSet<GraphEdge>();
            }
            if(siteEdges[edge.site2] == null) {
                siteEdges[edge.site2] = new HashSet<GraphEdge>();
            }
            HashSet<GraphEdge> s1 = siteEdges[edge.site1];
            HashSet<GraphEdge> s2 = siteEdges[edge.site2];
//            HashSet<GraphEdge> s2 = siteEdges.get(edge.site2);
            s1.add(edge);
            s2.add(edge);
        }
        for(int i = 0; i < siteEdges.length; i++) {
            HashSet<GraphEdge> hs = siteEdges[i];
            Polygon polygon = new Polygon();
            double lastx;
            double lasty;
            Iterator<GraphEdge> iterator = hs.iterator();
            GraphEdge first = iterator.next();
            polygon.getPoints().addAll(first.x1, first.y1);
            polygon.getPoints().addAll(first.x2, first.y2);
            lastx = first.x2;
            lasty = first.y2;
            while(iterator.hasNext()) {
                GraphEdge current = iterator.next();
                if(hasFirst(current, lastx, lasty)) {
                    polygon.getPoints().addAll(current.x2, current.y2);
                    lastx = current.x2;
                    lasty = current.y2;
                    iterator.remove();
                    iterator = hs.iterator();
                } else if(hasSecond(current, lastx, lasty)) {
                    polygon.getPoints().addAll(current.x1, current.y1);
                    lastx = current.x2;
                    lasty = current.y2;
                    iterator.remove();
                    iterator = hs.iterator();
                }
            }
            polygon = PolygonUtils.intersection(background, polygon);
            if(polygon != null) {
                this.add(new VoronoiCell(PolygonUtils.intersection(background, polygon), parents.get(i)));
            }
        }
    }

    private boolean hasFirst(GraphEdge g, double x, double y) {
        return (g.x1 == x && g.y1 == y);
    }
    private boolean hasSecond(GraphEdge g, double x, double y) {
        return (g.x2 == x && g.y2 == y);
    }
}
