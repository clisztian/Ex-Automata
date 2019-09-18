package application;
import javafx.fxml.FXML;

import java.text.ParseException;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class GlobalExplainabilityController {

	private AutomatonMachineController myAutomaton;
	
	@FXML
	private StackPane globalExpChart;
	
    private double[] positive_features;
    private double[] negative_features;
	private String[] feature_names;
	private String[] class_names;
	
	@FXML
    private ComboBox<String> featureInterpreterComboBox;

    @FXML
    private CheckBox negatedFeaturesCheckBox;

    @FXML
    private ComboBox<String> expClassComboBox;
	private Window primaryStage;


	private int n_clauses = 0;


	private double[] feature_importance = null;

	private int class_choice;

	private int feature_number = 0;

	private int end_feature = 10;

	private double[] feature_strength = null;


	public void setAutomaton(AutomatonMachineController myAutomaton) {
		this.myAutomaton = myAutomaton;
	}
	
	public void setFeatureNameComboBox(String[] feature_names) {
		
		this.feature_names = feature_names;		
		featureInterpreterComboBox.getItems().clear();
		featureInterpreterComboBox.getItems().add("Global");
		featureInterpreterComboBox.getItems().addAll(feature_names);
		featureInterpreterComboBox.getSelectionModel().selectFirst();
		
	}
	
	public void setClassNameComboBox(String[] class_names) {
		this.class_names = class_names;
		
		expClassComboBox.getItems().clear();
		expClassComboBox.getItems().addAll(class_names);
		expClassComboBox.getSelectionModel().selectFirst();
	}
	
	
    @FXML
    void handleClassChange(ActionEvent event) throws ParseException {

    	class_choice = expClassComboBox.getSelectionModel().getSelectedIndex();
    	myAutomaton.switchClass();
    	sketchCanvas();

    }

    @FXML
    void handleFeatureInterpreterComboBox(ActionEvent event) throws ParseException {

    	end_feature = 10;
    	feature_number = featureInterpreterComboBox.getSelectionModel().getSelectedIndex();
    	sketchCanvas();
    }

    @FXML
    void handleNegatedFeatures(ActionEvent event) throws ParseException {
    	sketchCanvas(); 	
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
    

    @FXML
    void handleNumFeaturesPlotted() {

    }

	public String[] getFeature_names() {
		return feature_names;
	}

	public void setFeature_names(String[] feature_names) {
		this.feature_names = feature_names;
	}

	public String[] getClass_names() {
		return class_names;
	}

	public void setClass_names(String[] class_names) {
		this.class_names = class_names;
	}

	public void initiateCanvas() throws ParseException {
		
		feature_names = new String[] {"No Classes ", "Yet"};
		globalExpChart.getChildren().add(GlobalExpBarChart.createBarChart(feature_names, feature_importance, n_clauses, negatedFeaturesCheckBox.isSelected(), "", "Global Interpretability", 10));
		globalExpChart.setFocusTraversable(true);
	}

    public void sketchCanvas() throws ParseException {
    	
    	if(feature_number > 0) {
    		
    		
    		if(negatedFeaturesCheckBox.isSelected()) {
    			feature_strength  = myAutomaton.getNegFeatureInterpretStrength(feature_number - 1);
    		}
    		else {
    			feature_strength = myAutomaton.getFeatureInterpretStrength(feature_number - 1);
    		}
    		String[] feature_interpret_names = myAutomaton.getFeatureInterpretNames(feature_number - 1);
    	    
    	        	    
    		globalExpChart.getChildren().set(0, GlobalExpBarChart.createBarChart(feature_interpret_names, feature_strength, 
    				n_clauses, negatedFeaturesCheckBox.isSelected(), featureInterpreterComboBox.getSelectionModel().getSelectedItem(), "Global Interpretability", end_feature));
    		
    	}
    	else {
    		
    		feature_strength = positive_features;
    		
        	if(!negatedFeaturesCheckBox.isSelected()) {
        		
        		globalExpChart.getChildren().set(0, GlobalExpBarChart.createBarChart(feature_names, positive_features, n_clauses, negatedFeaturesCheckBox.isSelected(), "", "Global Interpretability", end_feature));
        	}
        	else {
        		globalExpChart.getChildren().set(0, GlobalExpBarChart.createBarChart(feature_names, negative_features, n_clauses, negatedFeaturesCheckBox.isSelected(), "", "Global Interpretability", end_feature ));
        	}
    		
    	}


    }
	
	
	
	public double[] getPositive_features() {
		return positive_features;
	}



	public void setPositive_features(double[] positive_features) {
		this.positive_features = positive_features;
	}



	public double[] getNegative_features() {
		return negative_features;
	}



	public void setNegative_features(double[] negative_features) {
		this.negative_features = negative_features;
	}
    
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	
	public void setNClauses(int n_clauses) {
		this.n_clauses = n_clauses;
	}
	
	public void setFeatureImportance(double[] importance) {
		this.feature_importance = importance;
	}


	public int getClassChoice() {
		return class_choice;
	}
	
}
