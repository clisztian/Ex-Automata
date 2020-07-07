package application;
import java.text.DecimalFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



public class InterpretableRangeController {

	private DecimalFormat df = new DecimalFormat("#.##");
	private AutomatonMachineController myAutomaton;
	private InterpretableRangeChart interpretChart;
	private String[] feature_names;

	
	int myClassOutput;
	
	@FXML
    private StackPane stackBarChart;


	@FXML
	private Slider outputSlider;

	@FXML
	private TextField modelOutputText;
	
	@FXML
    private Slider topClauseSlider;

	@FXML
    private TextField topClauseText;

	private Stage primaryStage;
	private int modelchoice = 0;

    @FXML
	void handleTopClauseSlider() {

    	double top = topClauseSlider.getValue()/100.0;
    	topClauseText.setText(df.format(top));
    	System.out.println("Top: " + top);
    	updateChart(top);
	}
	

    public void setOutputSliderMax(int max) {
    	outputSlider.setMax(max);
    }
    
    
	@FXML
	void handleOutputClassChanged() {

		 myClassOutput = (int)outputSlider.getValue(); 
		 modelchoice = myClassOutput;
		 if(!myAutomaton.isRegression()) {
			 
			 modelchoice  = (int)(myAutomaton.getNumberTargets()*(myClassOutput/outputSlider.getMax()));			 
			 modelOutputText.setText("" + modelchoice);			 
			 if(myAutomaton != null && modelchoice < myAutomaton.getNumberTargets()) {
				 myAutomaton.setRangeClass(modelchoice);	 
				 interpretChart.updateChart(myAutomaton.getRanges());
			 } 
		 }
		 else {
			 
			 modelOutputText.setText("" + modelchoice);			 
			 if(myAutomaton != null && modelchoice < myAutomaton.getThreshold()) {
				 myAutomaton.setRangeClassRegression(modelchoice,(int)((topClauseSlider.getValue()/100.0)*myAutomaton.getThreshold()));	 
				 interpretChart.updateChart(myAutomaton.getRanges());
			 } 
			 
		 }
		
	}

	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;	
	}

	public void initiateCanvas() {

		interpretChart = new InterpretableRangeChart();	
		stackBarChart.getChildren().add(interpretChart);
		stackBarChart.setFocusTraversable(true);

	}
	
	public void setCanvas() {
		
		myAutomaton.computeIntitialFeatureImportance();
		interpretChart.set(feature_names, myAutomaton.getFeatureMaxRange(), myAutomaton.getRanges());
	}
	


	
	public void updateChart() {
		interpretChart.updateChart(myAutomaton.getRanges());
	}

	public void updateChart(double top) {
		
		if(myAutomaton != null && modelchoice < myAutomaton.getNumberTargets()) {
		  
			myAutomaton.computeFeatureImportance(modelchoice, top);
		    interpretChart.updateChart(myAutomaton.getRanges());
		}
	}

	
	public void setFeatureNames(String[] featureNames) {
		this.feature_names = featureNames;	
	}

	public void setAutomaton(AutomatonMachineController automatonMachineController) {
		this.myAutomaton = automatonMachineController;
	}
	 
	
	 
}
