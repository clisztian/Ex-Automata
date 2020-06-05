package application;

import java.text.DecimalFormat;
import java.text.ParseException;

import application.AutomatonMachineController.AdjustableNode;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * Creates a feature range chart that is animated. 
 * 
 * The idea is that one inputs a class or regression value range (model output) 
 * and the important input feature ranges given by the conditions on the clauses 
 * will be plotted in the stackedbarchart using three series. The middle series 2 is the 
 * most relevant, with the first and third being "dummy" series for filler. The series
 * should add up to one.
 * 
 *   
 * @author lisztian
 *
 */
public class InterpretableRangeChart extends StackedBarChart {

	
	
	private float[][] extremes;
	private String[] feature_names;
	private DecimalFormat df;
	
	public InterpretableRangeChart() {
		super(new CategoryAxis(), new NumberAxis());
		df = new DecimalFormat("#.##");
	}
	
	public void set(String[] feature_names, float[][] extremes, float[][] ranges) {
		
		this.feature_names = feature_names;
		this.extremes = extremes;
		
		int num_features = feature_names.length;
		XYChart.Series<String,Number> series1;
		XYChart.Series<String,Number> series2;
		XYChart.Series<String,Number> series3;
	    
	    setTitle("Feature importance ranges");
	    setAnimated(true);
	    setLegendVisible(false);
		
	    float[] maxvals = new float[num_features];
	    float[] minvals = new float[num_features];
	    
	    series1 = new XYChart.Series<String,Number>();
	    series2 = new XYChart.Series<String,Number>();
	    series3 = new XYChart.Series<String,Number>();
	    
	    for(int i = 0; i < num_features; i++) {
	    	
	    	minvals[i] = 1f - (extremes[i][1] - ranges[i][0])/(extremes[i][1] - extremes[i][0]);
	    	maxvals[i] = 1f - (extremes[i][1] - ranges[i][1])/(extremes[i][1] - extremes[i][0]);
	    	
	    	
	    	if(maxvals[i] > minvals[i]) {
	    		
	    		series1.getData().add(new XYChart.Data<String, Number>(feature_names[i], minvals[i]));
	    		series2.getData().add(new XYChart.Data<String, Number>(feature_names[i], maxvals[i] - minvals[i]));
	    		series3.getData().add(new XYChart.Data<String, Number>(feature_names[i], 1f - maxvals[i]));
	    	}
	    	else {
	    		series1.getData().add(new XYChart.Data<String, Number>(feature_names[i], 0f));
	    		series2.getData().add(new XYChart.Data<String, Number>(feature_names[i], 0f));
	    		series3.getData().add(new XYChart.Data<String, Number>(feature_names[i], 1f));
	    	}
	    }
      
	    getData().clear();
	    getData().addAll(series1, series2, series3);
	    
        
 
        for (final Data<String, Number> anydata : series2.getData()) {
            	
                Tooltip tooltip = new Tooltip();
                tooltip.setText(anydata.getXValue() + " Range\n" + 
                		anydata.getYValue());
                Tooltip.install(anydata.getNode(), tooltip);         	               	                
        }
        
        

        for(Node n:lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: rgb(15,55,150,0.05);\n "
            		+ " -fx-padding: 5;\n" + 
            		"    -fx-background-insets: 0,1,2;");
        }
        for(Node n:lookupAll(".default-color1.chart-bar")) {
        	n.setStyle("-fx-bar-fill: rgb(45,100,180,0.50);\n "
            		+ " -fx-padding: 5;\n" + 
            		"    -fx-background-color: linear-gradient(derive(-fx-bar-fill,-70%), derive(-fx-bar-fill,-20%)),\n" + 
            		"                          linear-gradient(derive(-fx-bar-fill,100%), derive(-fx-bar-fill, 10%)),\n" + 
            		"                          linear-gradient(derive(-fx-bar-fill,30%), derive(-fx-bar-fill,-10%));\n" + 
            		"    -fx-background-insets: 0,1,2;");
        }
        for(Node n:lookupAll(".default-color2.chart-bar")) {
            n.setStyle("-fx-bar-fill: rgb(15,55,150,0.05);\n "
            		+ " -fx-padding: 5;\n" + 
            		"    -fx-background-insets: 0,1,2;");
        }
		
		
	}
	
	
	/**
	 * Updates the values for the new ranges
	 * Assumes the range values are between [0,1)
	 * @param ranges
	 */
	public void updateChart(float[][] ranges) {
		
		int num_features = feature_names.length;
		float[] maxvals = new float[num_features];
	    float[] minvals = new float[num_features];
	    

	    
		for(int i = 0; i < num_features; i++) {
	    	
			minvals[i] = 1f - (extremes[i][1] - ranges[i][0])/(extremes[i][1] - extremes[i][0]);
	    	maxvals[i] = 1f - (extremes[i][1] - ranges[i][1])/(extremes[i][1] - extremes[i][0]);
	    	
	    	if(maxvals[i] > minvals[i]) {
	    		((Series<String, Number>)getData().get(0)).getData().get(i).setYValue(minvals[i]);
		    	((Series<String, Number>)getData().get(1)).getData().get(i).setYValue(maxvals[i] - minvals[i]);
		    	((Series<String, Number>)getData().get(2)).getData().get(i).setYValue(1f - maxvals[i]);
	    	}
	    	else {
	    		((Series<String, Number>)getData().get(0)).getData().get(i).setYValue(0);
		    	((Series<String, Number>)getData().get(1)).getData().get(i).setYValue(0);
		    	((Series<String, Number>)getData().get(2)).getData().get(i).setYValue(1f);
	    	}
	    	
	    	
	    	System.out.println(feature_names[i] + " " + minvals[i] + " " + (maxvals[i] - minvals[i]) + " " + (1f - maxvals[i]));
		}		
	}
	

	
}
