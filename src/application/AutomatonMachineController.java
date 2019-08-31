package application;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import automaton.AutomatonLearningMachine;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tools.ReferenceMetrics;

public class AutomatonMachineController {

	private AutomatonLearningMachine myAutomaton;
	private GlobalExplainabilityController globalExpController;
	private LocalExplainabilityController localExpController;
	private DataInterfaceController dataInputController;
	
	private ReferenceMetrics metrics;
    private double number_clause_multiplier = 1f;
    private double learn_rate = 5f;
    private Random rng;

    
	DecimalFormat decimalFormat = new DecimalFormat("#.##");
	List<Integer> training_sample_list;
	
	private Stage primaryStage; 
	private Stage globalExpWindow = new Stage();
	private Scene globalExpScene;
	
	private Stage localExpWindow = new Stage();
	private Scene localExpScene;
	
	private Stage dataInputWindow = new Stage();
	private Scene dataInputScene;
	
	
	


    @FXML
    private Label smoothnessLabel31;

    @FXML
    private Label smoothnessLabel41;

    @FXML
    private Slider numClausesSlider;

    @FXML
    private Slider numStatesSlider;

    @FXML
    private TextField numClausesText;

    @FXML
    private TextField numStatesText;

    @FXML
    private Label TimeSeriesLabel11;

    @FXML
    private Label smoothnessLabel32;

    @FXML
    private Slider traindingRoundsSlider;

    @FXML
    private TextField traindingRoundsText;

    @FXML
    private Label smoothnessLabel321;

    @FXML
    private Slider traindingSplitSlider;

    @FXML
    private TextField traindingRoundsText1;

    @FXML
    private Button beginTrainingButton;

    @FXML
    private ProgressBar trainingProgressBar;

    @FXML
    private Label TimeSeriesLabel111;

    @FXML
    private RadioButton learningMode;

    @FXML
    private RadioButton predictionMode;

    @FXML
    private Button newSampleButton;

    @FXML
    private Button continuousLearning;

    @FXML
    private Button stopButton;

    @FXML
    private Label TimeSeriesLabel1;

    @FXML
    private Label smoothnessLabel3;

    @FXML
    private Label smoothnessLabel4;

    @FXML
    private Slider clauseThresholdSlider;

    @FXML
    private Slider rateSlider;

    @FXML
    private TextField clauseThresholdText;

    @FXML
    private TextField rateSlideText;

    @FXML
    private TabPane frontTabbedPane;

    @FXML
    private CheckMenuItem globalExpCheckbox;

    @FXML
    private CheckMenuItem localExpCheckbox;
    
    @FXML
    private CheckMenuItem dataInterfaceCheckbox;
    
    @FXML
    private Button beginTestButton;
    
    @FXML
    private TextFlow diagnosticTextFlow;
    
    @FXML
    private Button buildAutomaton;
    
    @FXML
    private Tab performanceTab;

    @FXML
    private StackPane historicalPane;
    
    @FXML
    private CheckBox testingOut;
    
    
    
    ToggleGroup group;
    
	private double number_states_multiplier = 1.0;

	private double threshold_multiplier = 1.0;

	private int n_training_rounds = 100;

	private boolean update = true;

	private double split_ratio = .5;

	private int n_samples;
	
	private ArrayList<double[]> performanceRecord;
	
	Thread learnThread;
	Thread playThread;

	Task<Void> playtask;
	Task<Void> learnTask;
	private int training_samples;
	private int test_samples;
	private String[] diagnosticName;

	public void initiateRadioButtons() {
		
		group = new ToggleGroup();		
		learningMode.setToggleGroup(group);
		learningMode.setSelected(true);
		predictionMode.setToggleGroup(group);
		
		rng = new Random();
	}
	
	
	public void initiateAutomaton() throws Exception {
		
		if(dataInputController.getDataInterface() != null) {
			
			myAutomaton = new AutomatonLearningMachine(dataInputController.getBinarizer(), 
                    dataInputController.getData(), 
                    (float)threshold_multiplier, 
                    (float)number_clause_multiplier, 
                    (float)number_states_multiplier, 
                    (float)learn_rate);
						
			n_samples = dataInputController.getData().getN_samples();
		
			globalExpController.setFeatureNameComboBox(dataInputController.getFeatureNames());
			globalExpController.setClassNameComboBox(new String[] {"Class 1", "Class 2"});
			globalExpController.setNClauses(myAutomaton.getNClauses());
			globalExpController.setAutomaton(this);
			
			localExpController.setFeatureNameComboBox(dataInputController.getFeatureNames());
			localExpController.setAutomaton(this);
			
			diagnosticName = new String[] {"TypeIError", "TypeIIError", "Sensitivity", "Specificity", "Accuracy", "ErrorRate", "F1", "F2", "MCC"};
			performanceRecord = new ArrayList<double[]>();
			
			newSampleButton.setDisable(false);
			beginTestButton.setDisable(false);
			stopButton.setDisable(false);
			beginTrainingButton.setDisable(false);
			continuousLearning.setDisable(false);
			
			
			System.out.println("Automaton Initiated");
		}
	}
	

	@FXML
    void handleAutomatonBuild(ActionEvent event) throws Exception {
		initiateAutomaton();	
    }
	
	
    @FXML
    void handleChangeMode(ActionEvent event) {

    	if(learningMode.isSelected()) {
    		update = true;
    	}
    	else {
    		update = false;
    	}
    }

    @FXML
    void handleContinuousLearning(ActionEvent event) throws Exception {

    	
    	if(training_sample_list == null) {
        	training_sample_list = new ArrayList<Integer>();
    		for(int i = 0; i < n_samples; i++) training_sample_list.add(i);
    		Collections.shuffle(training_sample_list);
    	}
		
		int training_samples = (int)(split_ratio*n_samples);		
		int test_samples = n_samples - training_samples - 1;

		
		
		learnTask = new Task<Void>() {
	    
			@Override
			protected Void call() throws Exception {
				
		    int count = 0;
			while(!learnTask.isCancelled()) {
					
				if(update) {
					
					int rand_samp = training_sample_list.get(rng.nextInt(training_samples));		
					int prediction = myAutomaton.update(myAutomaton.getTransformedData()[rand_samp], myAutomaton.getTargets()[rand_samp]);
					
					myAutomaton.computeGlobalFeatureImportance(globalExpController.getClassChoice());
					
					globalExpController.setPositive_features(myAutomaton.getPositiveFeatures());
					globalExpController.setNegative_features(myAutomaton.getNegativeFeatures());
					globalExpController.sketchCanvas();					
				}
				else {
					
					int rand_samp = training_sample_list.get(training_samples + rng.nextInt(test_samples));		
					int[] prediction = myAutomaton.predict_interpret(myAutomaton.getTransformedData()[rand_samp]);
					int pred_class = prediction[prediction.length - 1];					
				}
			}
			return null;

		   }	   
        
      };
	  
      learnThread = new Thread(learnTask, "learn-thread");
	  learnThread.setDaemon(true);
	  learnThread.start();
    	
    }

    @FXML
    void handleNewSample()  throws Exception {

    	/**
    	 * Grab new observation and update or predict
    	 */
    	if(training_sample_list == null) {
        	training_sample_list = new ArrayList<Integer>();
    		for(int i = 0; i < n_samples; i++) training_sample_list.add(i);
    		Collections.shuffle(training_sample_list);
    		training_samples = (int)(split_ratio*n_samples);	
    		test_samples = n_samples - training_samples;
    	}
    	
    	if(update) {
			
			int rand_samp = training_sample_list.get(rng.nextInt(training_samples));		
			int prediction = myAutomaton.update(myAutomaton.getTransformedData()[rand_samp], myAutomaton.getTargets()[rand_samp]);

			
			myAutomaton.computeGlobalFeatureImportance(globalExpController.getClassChoice());
			
			globalExpController.setPositive_features(myAutomaton.getPositiveFeatures());
			globalExpController.setNegative_features(myAutomaton.getNegativeFeatures());
			globalExpController.sketchCanvas();		
			
			if(testingOut.isSelected()) {
				generateTestReport();
			}
			
		}
		else { //making an out-of-sample prediction
					
			int rand_samp = training_sample_list.get(training_samples + rng.nextInt(test_samples));				
			int pred_class = myAutomaton.computeLocalFeatureImportance(myAutomaton.getTransformedData()[rand_samp]);
			
			localExpController.setPositive_features(myAutomaton.getLocPositiveFeatures());
			localExpController.setNegative_features(myAutomaton.getLocNegativeFeatures());
			
			localExpController.setPrediction(pred_class);
			localExpController.setTarget(myAutomaton.getTargets()[rand_samp]);
			localExpController.sketchCanvas();
			printSample(myAutomaton.getOriginalData().getSample(rand_samp), pred_class, myAutomaton.getTargets()[rand_samp]);
		}
    	
    }

    private void printSample(float[] original, int pred, int targ) {
		
    	diagnosticTextFlow.getChildren().clear();
		
		//diagnosticTabArea.setText(sb.toString());
		Text label = new Text("\n\n");
		diagnosticTextFlow.getChildren().add(label);

		String[] names = globalExpController.getFeature_names();
		
		for(int i = 0; i < names.length; i++) {
			
			label = new Text(names[i] + ": ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(original[i] + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier", 20));
			diagnosticTextFlow.getChildren().add(label);
			
		}
		
		label = new Text("Prediction: ");
		label.setFill(Paint.valueOf(Color.AQUA.toString()));
		label.setFont(Font.font ("Courier", 20));
		diagnosticTextFlow.getChildren().add(label);
		label = new Text(pred+ "\n");
		
		if(pred != targ) {
			label.setFill(Paint.valueOf(Color.RED.toString()));
		}
		else {
			label.setFill(Paint.valueOf(Color.LIGHTGREEN.toString()));
		}
		label.setFont(Font.font ("Courier", 20));
		diagnosticTextFlow.getChildren().add(label);
		
		
	}


	@FXML
    void handleNumClausesChange() {

    	number_clause_multiplier = numClausesSlider.getValue();
    	numClausesText.setText(decimalFormat.format(number_clause_multiplier));
    	
    	if(myAutomaton != null) myAutomaton.setNumClauseMultiplier((float)number_clause_multiplier);   	    	
    }

    @FXML
    void handleNumStatesChange() {

    	number_states_multiplier = numStatesSlider.getValue();
    	numStatesText.setText(decimalFormat.format(number_states_multiplier));
    	
    	if(myAutomaton != null) myAutomaton.setNumStatesMultiplier((float)number_states_multiplier);   	    	
    	
    }

    @FXML
    void handleNumberRounds() {

    	n_training_rounds = (int)traindingRoundsSlider.getValue();
    	traindingRoundsText.setText(""+n_training_rounds);
    	
    	if(myAutomaton != null) myAutomaton.setN_rounds(n_training_rounds);
    	
    }

    @FXML
    void handleOpenDataFile(ActionEvent event) {

		if(dataInterfaceCheckbox.isSelected()) {
			dataInputWindow.show();
		}
		else {
			dataInputWindow.close();
		}	
    }

    @FXML
    void handleRateChange() {

    	learn_rate = rateSlider.getValue();
    	rateSlideText.setText(decimalFormat.format(learn_rate));
    	
    	if(myAutomaton != null) myAutomaton.setLearningRate((float)learn_rate);
    	
    	
    	
    }

    @FXML
    void handleStepsChange() {
    	
    	threshold_multiplier = clauseThresholdSlider.getValue();
    	clauseThresholdText.setText(decimalFormat.format(threshold_multiplier));
    	
    	if(myAutomaton != null) myAutomaton.setThresholdMultiplier((float)threshold_multiplier); 
    }

    @FXML
    void handleTrainSetSplit() {

    	split_ratio = traindingSplitSlider.getValue()/100f;;
    	traindingRoundsText1.setText(decimalFormat.format(split_ratio));
    	
    	if(myAutomaton != null) myAutomaton.setTrainSplitRatio((float)split_ratio);
    }

    @FXML
    void handleTraining(ActionEvent event) throws Exception {


    	
    	training_sample_list = new ArrayList<Integer>();
		for(int i = 0; i < n_samples; i++) training_sample_list.add(i);
		Collections.shuffle(training_sample_list);
		
		System.out.println("NSamples" + " " + split_ratio + " " + n_samples);
		training_samples = (int)(split_ratio*n_samples);		
		test_samples = n_samples - training_samples;

		globalExpController.setNClauses(myAutomaton.getNClauses());

			
		learnTask = new Task<Void>() {
	    	

			@Override
			protected Void call() throws Exception {
				
		
			
		    int count = 0;
		    
			for(int n = 0; n < n_training_rounds; n++) {			
				for(int i = 0; i < training_samples; i++) {
					
					int rand_samp = training_sample_list.get(i);	
					int prediction = myAutomaton.update(myAutomaton.getTransformedData()[rand_samp], myAutomaton.getTargets()[rand_samp]);
					
					//System.out.println("Prediction: " + prediction + " " + myAutomaton.getTargets()[rand_samp]);
					
					myAutomaton.computeGlobalFeatureImportance(globalExpController.getClassChoice());
					
					globalExpController.setPositive_features(myAutomaton.getPositiveFeatures());
					globalExpController.setNegative_features(myAutomaton.getNegativeFeatures());
					
//					Platform.runLater(new Runnable() {
//					    @Override
//					    public void run() {
//					    	try {
//								globalExpController.sketchCanvas();
//							} catch (ParseException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//					    }
//					});

					updateProgress(count, n_training_rounds*training_samples); 
					count++;

					if(learnTask.isCancelled()) break;
				}			
			}
			return null;

		   }	   
        
      };

      trainingProgressBar.progressProperty().bind(
			learnTask.progressProperty()
	  );
	  
      learnThread = new Thread(learnTask, "learn-thread");
	  learnThread.setDaemon(true);
	  learnThread.start();
		
		
    }
    
    @FXML
    void handleTesting(ActionEvent event) {

    	if(training_sample_list != null && myAutomaton != null) {
    	
    	
    		int training_samples = (int)(split_ratio*n_samples);	
    		int test_samples = n_samples - training_samples;
    		int target = 0;
    		
	    	int count_false = 0;
			int count_true = 0;
			int count_true_positive = 0;
			int count_true_negative = 0;
			int count_false_negative = 0;
			int count_false_positive = 0;
			
			for(int i = 0; i < test_samples; i++) {
				
					int rand_samp = training_sample_list.get(i + training_samples);
					int prediction = myAutomaton.predict(myAutomaton.getTransformedData()[rand_samp]);
					target = myAutomaton.getTargets()[rand_samp];
					
					System.out.println(i + " " + rand_samp + " " + prediction + " " + target);
					
					
					if(target == 1) {
						count_true++;
						
						if(prediction == 1) {
							count_true_positive++;
						}
						else {
							count_false_negative++;
						}
					}
					else {
						count_false++;
						if(prediction == 0) {
							count_true_negative++;
						}
						else {
							count_false_positive++;
						}
					}
			}	
			
			float X = 1f*count_true_positive/count_true;
			float Y = 1f*count_true_negative/count_false;
			float Z = 1f*count_false_negative/count_true;
			float Q = 1f*count_false_positive/count_false;
			
			
			metrics = new ReferenceMetrics(X, Y, Q, Z);
			
			StringBuilder sb = new StringBuilder();
			sb.append("TypeIError: " + metrics.getTypeI_error() + "\n");
			sb.append("TypeIIError: " + metrics.getTypeII_error() + "\n");
			sb.append("Sensitivity: " + metrics.getSensitivity() + "\n");
			sb.append("Specificity: " + metrics.getSpecificity() + "\n");
			sb.append("Accuracy: " + metrics.getAccuracy() + "\n");
			sb.append("ErrorRate: " + metrics.getError_rate() + "\n");
			sb.append("F1: " + metrics.getF1_score() + "\n");
			sb.append("F2: " + metrics.getF2_score() + "\n");
			sb.append("MCC: " + metrics.getMCC() + "\n");
			
			diagnosticTextFlow.getChildren().clear();
			
			//diagnosticTabArea.setText(sb.toString());
			
			Text label = new Text("TypeIError: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getTypeI_error() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);

			label = new Text("TypeIIError: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getTypeII_error() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Sensitivity: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getSensitivity() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Specificity: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getSpecificity() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Accuracy: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getAccuracy() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("ErrorRate: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getError_rate() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("F1: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getF1_score() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("F2: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getF2_score() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("MCC: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getMCC() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
            
    	}
    	
    	
    }
    

    public void generateTestReport() {
    	
    	
    	if(training_sample_list != null && myAutomaton != null) {
        	
        	
    		int training_samples = (int)(split_ratio*n_samples);	
    		int test_samples = n_samples - training_samples;
    		int target = 0;
    		
	    	int count_false = 0;
			int count_true = 0;
			int count_true_positive = 0;
			int count_true_negative = 0;
			int count_false_negative = 0;
			int count_false_positive = 0;
			
			for(int i = 0; i < test_samples; i++) {
				
					int rand_samp = training_sample_list.get(i + training_samples);
					int prediction = myAutomaton.predict(myAutomaton.getTransformedData()[rand_samp]);
					target = myAutomaton.getTargets()[rand_samp];
										
					if(target == 1) {
						count_true++;
						
						if(prediction == 1) {
							count_true_positive++;
						}
						else {
							count_false_negative++;
						}
					}
					else {
						count_false++;
						if(prediction == 0) {
							count_true_negative++;
						}
						else {
							count_false_positive++;
						}
					}
			}	
			
			float X = 1f*count_true_positive/count_true;
			float Y = 1f*count_true_negative/count_false;
			float Z = 1f*count_false_negative/count_true;
			float Q = 1f*count_false_positive/count_false;
			
			
			metrics = new ReferenceMetrics(X, Y, Q, Z);
			
			
			diagnosticTextFlow.getChildren().clear();
			
			//diagnosticTabArea.setText(sb.toString());
			Text label = new Text("\n\n");
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("TypeIError: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getTypeI_error() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);

			label = new Text("TypeIIError: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getTypeII_error() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Sensitivity: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getSensitivity() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Specificity: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getSpecificity() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Accuracy: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getAccuracy() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("ErrorRate: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getError_rate() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("F1: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getF1_score() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("F2: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getF2_score() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("MCC: ");
			label.setFill(Paint.valueOf(Color.AQUA.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getMCC() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
            
			double[] perf = new double[] {metrics.getTypeI_error(), 
					                      metrics.getTypeII_error(), 
					                      metrics.getSensitivity(), 
					                      metrics.getSpecificity(),
					                      metrics.getAccuracy(),
					                      metrics.getError_rate(),
					                      metrics.getF1_score(),
					                      metrics.getF2_score(),
					                      metrics.getMCC()};
			
			performanceRecord.add(perf);
					
			historicalPane.getChildren().set(0, TimeSeriesCanvas.createBarChart(performanceRecord, diagnosticName));
					                      
			
    	} 	
    }
    
    

    public void initiateCanvas() throws ParseException {
		
		historicalPane.getChildren().add(TimeSeriesCanvas.createBarChart(performanceRecord, diagnosticName));
		
	}
    
    
    @FXML
    void stopLearningButton(ActionEvent event) {

    	learnThread.interrupt();
    	learnTask.cancel();
    }
	
    
    public void computeGlobalFeatureImportance() {
    	
    	myAutomaton.computeGlobalFeatureImportance(globalExpController.getClassChoice());
    	globalExpController.setPositive_features(myAutomaton.getPositiveFeatures());
		globalExpController.setNegative_features(myAutomaton.getNegativeFeatures());
    }
    
    
    public void setLocalExpController() throws IOException, ParseException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("localExplainabilityInterface.fxml"));
		Parent root = (Parent)loader.load();
		localExpController = loader.getController();

		
		localExpScene = new Scene(root);
		localExpScene.getStylesheets().add("css/WhiteOnBlack.css");
		localExpWindow = new Stage();
		localExpWindow.setScene(localExpScene);
		localExpWindow.setX(primaryStage.getX() + 250);
		localExpWindow.setY(primaryStage.getY() + 100);
		
		localExpWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	localExpCheckbox.setSelected(false);
            }
        });
		
		localExpController.initiateCanvas();
		localExpController.setStage(primaryStage);
		
	}
    
    

    public void setGlobalExpController() throws IOException, ParseException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("globalExplainabilityInterface.fxml"));
		Parent root = (Parent)loader.load();
		globalExpController = loader.getController();

		
		globalExpScene = new Scene(root);
		globalExpScene.getStylesheets().add("css/WhiteOnBlack.css");
		globalExpWindow = new Stage();
		globalExpWindow.setScene(globalExpScene);
		globalExpWindow.setX(primaryStage.getX() + 250);
		globalExpWindow.setY(primaryStage.getY() + 100);
		
		globalExpWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	globalExpCheckbox.setSelected(false);
            }
        });
		
		globalExpController.initiateCanvas();
		globalExpController.setStage(primaryStage);
		
	}
    
    
    public void setDataInputController() throws IOException, ParseException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("DataInterfacePanel.fxml"));
		Parent root = (Parent)loader.load();
		dataInputController = loader.getController();

		
		dataInputScene = new Scene(root);
		dataInputScene.getStylesheets().add("css/WhiteOnBlack.css");
		dataInputWindow = new Stage();
		dataInputWindow.setScene(dataInputScene);
		dataInputWindow.setX(primaryStage.getX() + 250);
		dataInputWindow.setY(primaryStage.getY() + 100);
		
		dataInputWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	dataInterfaceCheckbox.setSelected(false);
            }
        });
		
		dataInputController.initializeController();
		dataInputController.setStage(primaryStage);
		
	}
    
    
    @FXML
    void handleGlobalExpCheckbox(ActionEvent event) {

		if(globalExpCheckbox.isSelected()) {
			globalExpWindow.show();
		}
		else {
			globalExpWindow.close();
		}	
    }

    @FXML
    void handleLocalExpCheckbox(ActionEvent event) {

    	if(localExpCheckbox.isSelected()) {
    		localExpWindow.show();
		}
		else {
			localExpWindow.close();
		}	
    	
    }
    
    
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}


	public String[] getFeatureInterpretNames(int feature_number) {
		return myAutomaton.getFeatureInterpreterNames(feature_number);
	}


	public double[] getFeatureInterpretStrength(int feature_number) {
		return myAutomaton.getFeatureInterpreter(feature_number);
	}


	public double[] getNegFeatureInterpretStrength(int feature_number) {
		return myAutomaton.getNegFeatureInterpreter(feature_number);
	}


	public double[] getLocalNegFeatureInterpretStrength(int feature_number) {
		return myAutomaton.getLocNegFeatureInterpreter(feature_number);
	}


	public double[] getLocalFeatureInterpretStrength(int feature_number) {
		return myAutomaton.getLocFeatureInterpreter(feature_number);
	}

	






    
    
    
	
}
