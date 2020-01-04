package application;

import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LocalExplainabilityController {

	
	private AutomatonMachineController myAutomaton;
	
    private double[] positive_features;
    private double[] negative_features;
	private String[] feature_names;
	private String[] class_names;
	
	private Window primaryStage;
	
	@FXML
    private StackPane localExpChart;

    @FXML
    private ComboBox<String> featureInterpreterComboBox;

    @FXML
    private CheckBox negatedFeaturesCheckBox;

    @FXML
    private TextField predictionText;

    @FXML
    private TextField targetText;
	private int feature_number;

	private int n_clauses = 100;

	private float prediction;
	private float target;

	private int end_feature = 10;

	private double[] feature_strength = null;
	
	final DecimalFormat df = new DecimalFormat("#0.00");
	
	public void setAutomaton(AutomatonMachineController myAutomaton) {
		this.myAutomaton = myAutomaton;
	}
	
    
	public void setFeatureNameComboBox(String[] feature_names) {
		

		this.feature_names = feature_names;		
		featureInterpreterComboBox.getItems().clear();
		featureInterpreterComboBox.getItems().add("Local");
		featureInterpreterComboBox.getItems().addAll(feature_names);
		featureInterpreterComboBox.getSelectionModel().selectFirst();
		
	}
    

    
    @FXML
    void handleFeatureInterpreterComboBox(ActionEvent event) {
    	
    	end_feature = 10;
    	feature_number = featureInterpreterComboBox.getSelectionModel().getSelectedIndex();
    	
    	if(feature_number == 0) {
    		end_feature = Math.min(feature_names.length,10);
    	}
    	
    	try {
			sketchCanvas();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }



	@FXML
    void handleNegatedFeatures(ActionEvent event) throws ParseException {
		sketchCanvas(); 
    }

    @FXML
    void handleNumFeaturesPlotted() {

    }
    
    
	public void initiateCanvas() throws ParseException {
		
		feature_names = new String[] {"No Classes ", "Yet"};
		double[] feature_importance = null;
		localExpChart.getChildren().add(GlobalExpBarChart.createBarChart(feature_names, feature_importance, 0, negatedFeaturesCheckBox.isSelected(), "", "Local Interpretability", 10));	
		localExpChart.setFocusTraversable(true);
	}
	
	
    @FXML
    void handleChartShift(KeyEvent event) throws ParseException {

    	if(event.getCode() == KeyCode.RIGHT) {
    		
    		if(end_feature < feature_strength.length) {
    			end_feature++;
    			sketchCanvas();
    			
    		}    
    	} 
    	else if(event.getCode() == KeyCode.LEFT) {
    	
    		if(end_feature > 10) {
    			end_feature--;
    			sketchCanvas();
    		}   
    	}    	
    }
	
	
    public void sketchCanvas() throws ParseException {
    	
    	if(feature_number > 0) {
    		

    		if(negatedFeaturesCheckBox.isSelected()) {
    			feature_strength  = myAutomaton.getLocalNegFeatureInterpretStrength(feature_number - 1);
    		}
    		else {
    			feature_strength = myAutomaton.getLocalFeatureInterpretStrength(feature_number - 1);
    		}
    		String[] feature_interpret_names = myAutomaton.getFeatureInterpretNames(feature_number - 1);
    	        		
    	        	    
    		localExpChart.getChildren().set(0, GlobalExpBarChart.createBarChart(feature_interpret_names, feature_strength, 
    				n_clauses, negatedFeaturesCheckBox.isSelected(), featureInterpreterComboBox.getSelectionModel().getSelectedItem(), "Local Interpretability", end_feature));
    		
    	}
    	else {
    		
    		feature_strength = positive_features;
        	if(!negatedFeaturesCheckBox.isSelected()) {
        		
        		localExpChart.getChildren().set(0, GlobalExpBarChart.createBarChart(feature_names, positive_features, n_clauses, negatedFeaturesCheckBox.isSelected(), "", "Local Interpretability", end_feature));
        	}
        	else {
        		localExpChart.getChildren().set(0, GlobalExpBarChart.createBarChart(feature_names, negative_features, n_clauses, negatedFeaturesCheckBox.isSelected(), "", "Local Interpretability", end_feature));
        	}
    		
    	}
    	
    	predictionText.setText(df.format(prediction));
    	
    	if(target < 0) {
    		targetText.setText("NA");
    	}
    	else {
    		targetText.setText(df.format(target));
    	}
    	
	}
    
    public void sketchNothing() throws ParseException {
    	localExpChart.getChildren().set(0, GlobalExpBarChart.createBarChart(feature_names, null, n_clauses, negatedFeaturesCheckBox.isSelected(), "", "Local Interpretability", end_feature ));
    	predictionText.setText("");
    	targetText.setText("");
    }
    
	
	public void setPositive_features(double[] positive_features) {
		this.positive_features = positive_features;
	}
	
	public void setNegative_features(double[] negative_features) {
		this.negative_features = negative_features;
	}


	public float getPrediction() {
		return prediction;
	}


	public void setPrediction(float prediction) {
		this.prediction = prediction;
	}


	public float getTarget() {
		return target;
	}


	public void setTarget(float target) {
		this.target = target;
	}


	public void setStage(Stage primaryStage2) {
		this.primaryStage = primaryStage2;	
	}
	
	public String[] getFeatureNames() {
		return feature_names;
	}
	
	
	
}
