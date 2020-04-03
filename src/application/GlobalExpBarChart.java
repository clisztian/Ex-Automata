package application;

import java.text.ParseException;

import org.apache.commons.lang3.ArrayUtils;

import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;

public class GlobalExpBarChart {


//	public static void main(String[] args) {
//		
//		double[] vals = new double[] {4,1,6,3,7,2};
//		int[] index = new int[] {0,1,2,3,4,5};
//		
//		quicksort(vals, index);
//		
//		for(int i = 0; i < vals.length; i++) {
//			System.out.println(index[i] + " " + vals[i]);
//		}
//		
//	}
	
	
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
	        
	        int[] indexes = new int[num_features];
	        for(int i = 0; i < num_features; i++) {
	        	indexes[i] = i;
	        }
	        
	        double sum = 0;
	        if(title.equalsIgnoreCase("Local Interpretability")) {
	        	for(int i = 0; i < num_features; i++) {
	        		
		        	sum += feature_importance[i];
		        }
		        if(sum == 0) sum = 1;
	        }
	        else sum = 1;
	        
	        
	        quicksort(feature_importance, indexes);
	        ArrayUtils.reverse(feature_importance);
	        ArrayUtils.reverse(indexes);
	        
	        
	        int start = Math.max(0, end_features - screenMAX  );
	        
	        for(int i = start; i < end_features; i++) {
	        	
	        	pin.getData().add(new XYChart.Data(feature_names[indexes[i]], feature_importance[i]/sum));
	        	
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

	
	public static void quicksort(double[] main, int[] index) {
	    quicksort(main, index, 0, index.length - 1);
	}

	// quicksort a[left] to a[right]
	public static void quicksort(double[] a, int[] index, int left, int right) {
	    if (right <= left) return;
	    int i = partition(a, index, left, right);
	    quicksort(a, index, left, i-1);
	    quicksort(a, index, i+1, right);
	}

	// partition a[left] to a[right], assumes left < right
	private static int partition(double[] a, int[] index, 
	int left, int right) {
	    int i = left - 1;
	    int j = right;
	    while (true) {
	        while (less(a[++i], a[right]))      // find item on left to swap
	            ;                               // a[right] acts as sentinel
	        while (less(a[right], a[--j]))      // find item on right to swap
	            if (j == left) break;           // don't go out-of-bounds
	        if (i >= j) break;                  // check if pointers cross
	        exch(a, index, i, j);               // swap two elements into place
	    }
	    exch(a, index, i, right);               // swap with partition element
	    return i;
	}

	// is x < y ?
	private static boolean less(double x, double y) {
	    return (x < y);
	}

	// exchange a[i] and a[j]
	private static void exch(double[] a, int[] index, int i, int j) {
	    double swap = a[i];
	    a[i] = a[j];
	    a[j] = swap;
	    int b = index[i];
	    index[i] = index[j];
	    index[j] = b;
	}
	

}
