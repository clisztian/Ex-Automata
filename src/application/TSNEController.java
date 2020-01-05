package application;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Random;

import com.jujutsu.tsne.MemOptimizedTSne;
import com.jujutsu.tsne.TSne;
import com.jujutsu.tsne.TSneConfiguration;
import com.jujutsu.utils.MatrixOps;
import com.jujutsu.utils.TSneUtils;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class TSNEController {

	private double[][] myData;
	private double[][] t_sneOutput;
	
	private int nIterations = 1000;
	private int nDimension = 50;
	private double perplexity = 20.0;
	Random rng;
	
    @FXML
    private StackPane tsneCanvas;

    @FXML
    private Label TimeSeriesLabel1;

    @FXML
    private CheckBox logCheckBox;

    @FXML
    private CheckBox scaleCheckBox;

    @FXML
    private Slider dimsSlider;

    @FXML
    private TextField dimsText;

    @FXML
    private Slider perplexitySlider;

    @FXML
    private TextField perplexityText;

    @FXML
    private Slider iterationsSlider;

    @FXML
    private TextField perplexityText1;

    @FXML
    private Button computeButton;

    @FXML
    private CheckBox realTimeCheckBox;

    DecimalFormat df = new DecimalFormat();
	private Window primaryStage;
	private AutomatonMachineController myAutomaton;
	private double[] mins;
	private double[] maxs;
	
	
	public void setAutomatonController(AutomatonMachineController cont) {
		this.myAutomaton = cont;
	}
	
	
    public void initializeTSNE() throws ParseException {
    	
    	df.setMaximumFractionDigits(2);
    	tsneCanvas.getChildren().add(AutomatonMachineController.createScatterChart());
    }
    
    
    @FXML
    void handleCompute(ActionEvent event) throws ParseException {
    	computeTSNE();	
    }

    @FXML
    void handleDimensionChange() throws ParseException {

    	nDimension = (int)dimsSlider.getValue();
    	dimsText.setText("" + nDimension);
    	
    	if(realTimeCheckBox.isSelected()) {
    		computeTSNE();	
    	}
    	
    }

    @FXML
    void handleIterationsChange() throws ParseException {

    	nIterations = (int)iterationsSlider.getValue();
    	perplexityText1.setText("" + nIterations);
    	
    	if(realTimeCheckBox.isSelected()) {
    		computeTSNE();	
    	}
    }

    @FXML
    void handlePerplexityChange() throws ParseException {

    	perplexity = perplexitySlider.getValue();
    	perplexityText.setText("" + perplexity);
    	
    	if(realTimeCheckBox.isSelected()) {
    		computeTSNE();	
    	}
    }

    
    public void setData(double[][] myData) {
    	this.myData = myData; 	
    }
    
    
    public void computeTSNE() throws ParseException {
    	
    	if(myData != null) {
    		
    		double[][] mycopy = myData.clone();
    		if(logCheckBox.isSelected()) mycopy = MatrixOps.log(mycopy, true); 
    		if(scaleCheckBox.isSelected()) mycopy = MatrixOps.centerAndScale(mycopy);

    		//System.out.println(MatrixOps.doubleArrayToPrintString(myData,5,5,20));

    		TSne tsne = new MemOptimizedTSne();
    		//TSne tsne = new BlasTSne();
    		long t1 = System.currentTimeMillis();

    		TSneConfiguration config = TSneUtils.buildConfig(mycopy, 3, nDimension, perplexity, nIterations, true, 0.5, true, false);
    		t_sneOutput = tsne.tsne(config);
    		
    		if(t_sneOutput.length > 2000) {
    			
    			int[] indices = new int[2000];
        		for(int i = 0; i < 2000; i++) {
        			indices[i] = rng.nextInt(t_sneOutput.length);
        		}
        		t_sneOutput = MatrixOps.copyRows(t_sneOutput, indices);
    		}
    		

    		long t2 = System.currentTimeMillis();
    		
    		findExtremes();
    		sketchCanvas();
    	}
    	
    	
    }


	private void sketchCanvas() throws ParseException {		
		tsneCanvas.getChildren().set(0,myAutomaton.createScatterChart(t_sneOutput));
	}
	
	public  void updateCanvas(ScatterChart sc) {
		tsneCanvas.getChildren().set(0,sc);
	}
    
	
	public void findExtremes() {
		
		mins = new double[3];
		maxs = new double[3];
		
		for(int i = 0; i < 3; i++) {
			mins[i] = Double.MAX_VALUE;
			maxs[i] = -Double.MAX_VALUE;
		}
		
		for(int i = 0; i < t_sneOutput.length; i++) {
			
			for(int j = 0; j < 3; j++) {
				
				if(t_sneOutput[i][j] > maxs[j]) {
					maxs[j] = t_sneOutput[i][j];
				}
				else if(t_sneOutput[i][j] < mins[j]) {
					mins[j] = t_sneOutput[i][j];
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
	
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
}

