package jxmaps;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;





public class JXMapViewerTab {

	private JXMapKit jXMapKit;
	private DefaultTileFactory tileFactory;
	private ArrayList<MyWaypoint> waypoints = new ArrayList<MyWaypoint>();
	private CustomPainter myPainter =  new CustomPainter();;
	
	
    public void initializeMap(final SwingNode swingNode, String mapstyle) {
    	
    	
    	SwingUtilities.invokeLater(() -> {
            
        	jXMapKit = new JXMapKit();
        	jXMapKit.setZoomSliderVisible(false);
        	jXMapKit.setZoomButtonsVisible(false);
        	
            TileFactoryInfo info = new OSMTileFactoryInfo("OpenStreetMap", mapstyle);
            
            tileFactory = new DefaultTileFactory(info);
            tileFactory.setThreadPoolSize(12);
            jXMapKit.setTileFactory(tileFactory);

            GeoPosition gp = new GeoPosition(47.37, 8.54);     
            

            jXMapKit.setZoom(5);
            jXMapKit.setAddressLocation(gp);

                
            jXMapKit.getMainMap().addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    // ignore
                }
                
                @Override
                public void mouseMoved(MouseEvent e) {
                    
                }
            });

            swingNode.setContent(jXMapKit);
        });
    }
    	
    	
	public void addGeoPosition(double lat, double longi, Color mycolor, String text) {			
		waypoints.add(new MyWaypoint(mycolor, new GeoPosition(lat, longi), text));		
	}
    
	public void removeAllPositions() {		
		waypoints.clear();		
	}
	

    public void createSwingContent(final SwingNode swingNode) {
    	
    	//46.9480° N, 7.4474 46.2044° N, 6.1432
        SwingUtilities.invokeLater(() -> {
            
        	
        	myPainter.setWaypoints(waypoints);
        	myPainter.setRenderer(new FancyWaypointRenderer());
        	jXMapKit.getMainMap().setOverlayPainter(myPainter);            
            jXMapKit.setZoom(11);
            

            jXMapKit.getMainMap().addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    // ignore
                }

                public void mouseMoved(MouseEvent me) {
                    
                	Point2D gp_pt = null;
                	JXMapViewer map = jXMapKit.getMainMap();

                    for (MyWaypoint waypoint : waypoints) {
                        //convert to world bitmap
                        gp_pt = tileFactory.geoToPixel(waypoint.getPosition(), map.getZoom());

                        //convert to screen
                        Rectangle rect = map.getViewportBounds();
                        Point converted_gp_pt = new Point((int) gp_pt.getX() - rect.x,
                            (int) gp_pt.getY() - rect.y);

                        if (converted_gp_pt.distance(me.getPoint()) < 10) {
                            
//                        	final Label mylabel = new Label(waypoint.getMyInfo());
//                        	mylabel.setStyle("-fx-text-fill: cyan; -fx-font: 14 \"courier\"; -fx-padding: 0 0 20 0; -fx-text-alignment: left");
//                        	mylabel.setEffect(new Glow(.7));
//                        	StackPane.setAlignment(mylabel, Pos.TOP_LEFT);
//                            glass.getChildren().addAll(mylabel);             
                        	

                        } else {
                        	
                        }
                    }
                }  
            });

            swingNode.setContent(jXMapKit);
        });
    }
    
    
    class CustomPainter extends WaypointPainter<MyWaypoint> {
        public void setWaypoints(List<? extends MyWaypoint> waypoints) {
            super.setWaypoints(new HashSet<MyWaypoint>(waypoints)); 
        }
    }
    
    
    
}
