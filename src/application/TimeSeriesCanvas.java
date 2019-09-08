package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;


public class TimeSeriesCanvas {

	static SimpleDateFormat format = new SimpleDateFormat("YYYY-mm-HH");
	static double minYValue = Double.MAX_VALUE;
	static DropShadow shadow = new DropShadow(10, 0, 2, Color.GREY);
	static String[] linestyle;
	private static int screenMAX = 100;


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static XYChart<CategoryAxis, NumberAxis> createBarChart(ArrayList<double[]> data, String[] names) {
		
		NumberAxis yAxis;
		CategoryAxis xAxis = new CategoryAxis();
    	if(data == null) {
    		yAxis = new NumberAxis();
    	}
    	else {
    		
    		if(data.get(0).length > 2) {
    			yAxis = new NumberAxis(0, 1, .01);
    		}
    		else {
    			yAxis = new NumberAxis();
    		}
    		
    	}
        
        LineChart lc = new LineChart<>(xAxis, yAxis);

	      lc.setTitle("Historical KPIs");	 
	      lc.setAnimated(false);
	      lc.setLegendVisible(true);
	      //lc.setCreateSymbols(false);
	      //lc.applyCss();

	      if(data != null ) {
	    	  
	    	  lc.setData(getChartData(data, names));     	  
	      }
	      
	      
	      return lc;

	}
	
	
	public static ObservableList<XYChart.Series<String, Double>> getChartData(ArrayList<double[]> series, String[] names) {


		ObservableList<XYChart.Series<String, Double>> data = FXCollections.observableArrayList();
		
		int numSeries = series.get(0).length;
		ArrayList<Series<String, Double>> anySignals = new ArrayList<Series<String, Double>>();
		
		for(int i = 0; i < numSeries; i++) {
		 
			Series<String, Double> signal = new Series<>();
			anySignals.add(signal);
			anySignals.get(i).setName(names[i]);
		}
		
        int start = Math.max(0, series.size() - screenMAX );
    	
        try {
        	
	        for (int i = start; i < series.size(); i++) {
	        	
	        	double[] value = series.get(i);
	        	
	            for(int n = 0; n < value.length; n++) {
	            	(anySignals.get(n)).getData().add(new XYChart.Data<>(Integer.toString(i), value[n]));        	
	            } 	
	        }
	                
	   }
       catch (Exception e) {
			e.printStackTrace();
	   }
        
        for (final Series<String, Double> myseries : anySignals) {
            for (final Data<String, Double> anydata : myseries.getData()) {
            	
                Tooltip tooltip = new Tooltip();
                tooltip.setText(myseries.getName() + " " + anydata.getXValue());
                Tooltip.install(anydata.getNode(), tooltip);
                	               	                
            }
        }
        
        
        
       
	   for(int i = 0; i < numSeries; i++) {			 
			data.add(anySignals.get(i));
	   } 
        
        
      			
	   return data;
	}		
	
	
	
	
	
	
	
}