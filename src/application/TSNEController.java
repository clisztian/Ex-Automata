package application;

import java.text.DecimalFormat;
import java.text.ParseException;

import com.jujutsu.tsne.MemOptimizedTSne;
import com.jujutsu.tsne.TSne;
import com.jujutsu.tsne.TSneConfiguration;
import com.jujutsu.utils.MatrixOps;
import com.jujutsu.utils.TSneUtils;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    		long t2 = System.currentTimeMillis();
    		sketchCanvas();
    	}
    	
    	
    }


	private void sketchCanvas() throws ParseException {
		
		tsneCanvas.getChildren().set(0,myAutomaton.createScatterChart(t_sneOutput));

		
	}
    
    
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
}
