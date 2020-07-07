package application;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class InterpretableClusteringController {

	
    @FXML
    private StackPane contrastiveCanvas;

    @FXML
    private Label TimeSeriesLabel1;

    @FXML
    private CheckBox logCheckBox;

    @FXML
    private CheckBox scaleCheckBox;

    @FXML
    private Slider alphaSlider;

    @FXML
    private TextField dimsText;

    @FXML
    private Button computeButton;

    @FXML
    private CheckBox realTimeCheckBox;

    @FXML
    private ComboBox<?> featureCombo;

    @FXML
    void handleAlphaChange() throws ParseException {

    	eps = alphaSlider.getValue();
    	dimsText.setText("" + df.format(eps));
    	
    	if(realTimeCheckBox.isSelected()) {
    		computeInterpretableCluster();
    	}
    	
    }

    @FXML
    void handleCompute(ActionEvent event) throws ParseException {
    	computeInterpretableCluster();
    }

    @FXML
    void handleFeatureFinder(ActionEvent event) {

    }
	
	
	
	Random rng;
	double[][] interpretable_cluster;
	private Window primaryStage;
	private AutomatonMachineController myAutomaton;

	DecimalFormat df = new DecimalFormat("##.#");
	private double eps = 100.0;
	private double[] feature_importance;
	private double[][] local_importance;

	private double[] mins;

	private double[] maxs;
	

	public void setAutomatonController(AutomatonMachineController cont) {
		this.myAutomaton = cont;
	}
	
    public void initializeContrastive() throws ParseException {
    	contrastiveCanvas.getChildren().add(AutomatonMachineController.createScatterChart());
    }
	
    
    public void setFeatureImportance(double[] feature_importance) {
    	this.feature_importance = feature_importance;
    }
    
    public void setLocalImportance(double[][] local_importance) {
    	this.local_importance = local_importance;
    }
    
	
	/**
	 * The feature names, raw global importance for a given class, and raw local importance are the inputs
	 * M is the number of centroids
	 * @param feature_names
	 * @param feature_importance
	 * @param local_importance
	 * @param m
	 */
	public void interpretableCluster() {
		
		
		rng = new Random(23);
		int num_features = feature_importance.length;
		int N = local_importance.length;
		int M = num_features;
				
		interpretable_cluster = new double[local_importance.length][3];
		int[] global_indexes = new int[num_features];
		int[] local_indexes = new int[num_features];
        for(int i = 0; i < num_features; i++) {
        	global_indexes[i] = i;
        	local_indexes[i] = i;
        }
        
        double[] feature_copy = new double[feature_importance.length];
        
        System.arraycopy(feature_importance, 0, feature_copy, 0, feature_copy.length);
        quicksort(feature_copy, global_indexes);
        ArrayUtils.reverse(feature_copy);
        ArrayUtils.reverse(global_indexes);
        
//        for(int i = 0; i < num_features; i++) {
//        	System.out.print(global_indexes[i] + " ");
//        }
//        System.out.println();
        
        double[][] centroids = new double[M][3];
        
        for(int i = 0; i < M; i++) {
        	
        	centroids[i][0] = rng.nextDouble()*500.0;
        	centroids[i][1] = rng.nextDouble()*500.0;
        	centroids[i][2] = rng.nextDouble()*500.0;  	
        }
        
        for(int i = 0; i < local_importance.length; i++) {
        	
        	for(int k = 0; k < local_importance[0].length; k++) local_indexes[k] = k;
        	
        	feature_copy = new double[local_importance[i].length];
        	System.arraycopy(local_importance[i], 0, feature_copy, 0, feature_copy.length);
        	quicksort(feature_copy, local_indexes);
            ArrayUtils.reverse(local_indexes);
            
//            for(int k = 0; k < num_features; k++) {
//            	System.out.print(local_indexes[k] + " ");
//            }
//            System.out.println();

            int main_centroid = local_indexes[0];
            interpretable_cluster[i][0] = centroids[main_centroid][0];
            interpretable_cluster[i][1] = centroids[main_centroid][1];
            interpretable_cluster[i][2] = centroids[main_centroid][2];
            
            double dir1 = (-eps + rng.nextDouble()*2*eps);
            double dir2 = (-eps + rng.nextDouble()*2*eps);
            double dir3 = (-eps + rng.nextDouble()*2*eps);
            
            System.out.print(i + " " + main_centroid + " " +  df.format(interpretable_cluster[i][0]) + " " + 
                df.format(interpretable_cluster[i][1]) + " " + df.format(interpretable_cluster[i][2]));
            
            interpretable_cluster[i][0] += dir1;
            interpretable_cluster[i][1] += dir2;
            interpretable_cluster[i][2] += dir3;
                        
//            int sub_centroid = local_indexes[1];
//            double d = dist3(centroids[main_centroid], centroids[sub_centroid])/eps;
//            
//            //System.out.println(eps + " " + d);
//            
//            interpretable_cluster[i][0] += (centroids[sub_centroid][0] - interpretable_cluster[i][0])*d;
//            interpretable_cluster[i][1] += (centroids[sub_centroid][1] - interpretable_cluster[i][1])*d;
//            interpretable_cluster[i][2] += (centroids[sub_centroid][2] - interpretable_cluster[i][2])*d;
//            
//            int dritte_centroid = local_indexes[2];
//            d = dist3(centroids[sub_centroid], centroids[dritte_centroid])/(eps);
//            
//            interpretable_cluster[i][0] += (centroids[dritte_centroid][0] - interpretable_cluster[i][0])*d;
//            interpretable_cluster[i][1] += (centroids[dritte_centroid][1] - interpretable_cluster[i][1])*d;
//            interpretable_cluster[i][2] += (centroids[dritte_centroid][2] - interpretable_cluster[i][2])*d;   
//            
//            int last_centroid = local_indexes[3];
//            d = dist3(centroids[dritte_centroid], centroids[last_centroid])/(eps);
//            
//            interpretable_cluster[i][0] += (centroids[last_centroid][0] - interpretable_cluster[i][0])*d + (-10.0 + rng.nextDouble()*20.0);
//            interpretable_cluster[i][1] += (centroids[last_centroid][1] - interpretable_cluster[i][1])*d + (-10.0 + rng.nextDouble()*20.0);
//            interpretable_cluster[i][2] += (centroids[last_centroid][2] - interpretable_cluster[i][2])*d + (-10.0 + rng.nextDouble()*20.0);
         
            //System.out.println(i + "  " + df.format(interpretable_cluster[i][0]) + " " + df.format(interpretable_cluster[i][1]) + " " + df.format(interpretable_cluster[i][2]));
        }

	}
	
	
    public void computeInterpretableCluster() throws ParseException {
    	
    	interpretableCluster();	
		findExtremes();
		sketchCanvas();
    }
	
	
	private void sketchCanvas() throws ParseException {		
		contrastiveCanvas.getChildren().set(0,myAutomaton.createContrastiveScatterChart(interpretable_cluster));
	}
	
	public  void updateCanvas(ScatterChart sc) {
		contrastiveCanvas.getChildren().set(0,sc);
	}
	
    public void findExtremes() {
		
		mins = new double[3];
		maxs = new double[3];
		
		for(int i = 0; i < 3; i++) {
			mins[i] = Double.MAX_VALUE;
			maxs[i] = -Double.MAX_VALUE;
		}
		
		for(int i = 0; i < interpretable_cluster.length; i++) {
			
			for(int j = 0; j < 3; j++) {
				
				if(interpretable_cluster[i][j] > maxs[j]) {
					maxs[j] = interpretable_cluster[i][j];
				}
				else if(interpretable_cluster[i][j] < mins[j]) {
					mins[j] = interpretable_cluster[i][j];
				}
			}	
		}
				
	}
	
	double[] getMins() {
		return mins;
	}
    
	double[] getMaxs() {
		return maxs;
	}
	
	
	public static double dist3(double[] x, double[] y) {		
		return Math.sqrt((x[0] - y[0])*(x[0] - y[0]) + (x[1] - y[1])*(x[1] - y[1]) + (x[2] - y[2])*(x[2] - y[2]));
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
	
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
}
