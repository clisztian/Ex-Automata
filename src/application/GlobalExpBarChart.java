package application;

import java.text.ParseException;


import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;

public class GlobalExpBarChart {


	private static int screenMAX = 20;

	public static BarChart createBarChart(String[] feature_names, double[] feature_importance, int n_clauses, boolean negated, String plot_name, String title, int end_features) throws ParseException {
	
		int num_features = feature_names.length;
		final CategoryAxis xAxis = new CategoryAxis();
		

			
		//System.out.println("NumFeatures " + num_features + " " + n_clauses);
	    final NumberAxis yAxis = new NumberAxis();
		
	    String titleGood = plot_name + " Feature Strength";
	    String titleBad = plot_name + " Negated Feature Strength";
	    
		final BarChart<String,Number> bc =  new BarChart<String,Number>(xAxis,yAxis);
	    bc.setTitle(title);
	    bc.setAnimated(false);
	    bc.setLegendVisible(false);
	    
	    if(!negated) {
	    	xAxis.setLabel(titleGood);  
	    }
	    else {
	    	xAxis.setLabel(titleBad);
	    }
	    yAxis.setLabel("Clause Feature Strength");
		
	    String colorMe = "#6495ED";
	    
	    if(title.contains("Local")) {
	    	colorMe = "#2E8B57";
	    }
	    
	    if(negated) colorMe = "red";
	    
	    if(feature_importance != null && end_features <= feature_importance.length) {
	
	        
	        XYChart.Series pin = new XYChart.Series();
	        //pin.setName("Global Feature Strength"); 
	        
	        double sum = 0;
	        if(title.equalsIgnoreCase("Local Interpretability")) {
	        	for(int i = 0; i < num_features; i++) {
		        	sum += feature_importance[i];
		        }
		        if(sum == 0) sum = 1;
	        }
	        else sum = 1;
	        
	        
	        
	        int start = Math.max(0, end_features - screenMAX  );
	        
	        for(int i = start; i < end_features; i++) {
	        	
	        	pin.getData().add(new XYChart.Data(feature_names[i], feature_importance[i]/sum));
	        }      
	        bc.getData().addAll(pin);
	        
	        for (final Series<String, Number> series : bc.getData()) {
	            for (final Data<String, Number> anydata : series.getData()) {
	            	
	                Tooltip tooltip = new Tooltip();
	                tooltip.setText(anydata.getXValue() + " Strength\n" + 
	                		anydata.getYValue() + " of " + n_clauses);
	                Tooltip.install(anydata.getNode(), tooltip);
	                	               	                
	            }
	        }
	        
	
	        for(Node n:bc.lookupAll(".default-color0.chart-bar")) {
	            n.setStyle("-fx-bar-fill: " + colorMe + ";\n "
	            		+ " -fx-padding: 5;\n" + 
	            		"    -fx-background-color: linear-gradient(derive(-fx-bar-fill,-70%), derive(-fx-bar-fill,-20%)),\n" + 
	            		"                          linear-gradient(derive(-fx-bar-fill,100%), derive(-fx-bar-fill, 10%)),\n" + 
	            		"                          linear-gradient(derive(-fx-bar-fill,30%), derive(-fx-bar-fill,-10%));\n" + 
	            		"    -fx-background-insets: 0,1,2;");
	        }
	        
	        //System.out.println("Sending it out");
	    }
		
	    
		return bc;
    }


}
