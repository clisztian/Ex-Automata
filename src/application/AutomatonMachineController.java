package application;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.jujutsu.utils.MatrixOps;

import automaton.AutomatonLearningMachine;
import automaton.FastAutomatonLearningMachine;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jxmaps.JXMapViewerTab;
import tools.ReferenceMetrics;

public class AutomatonMachineController {

	private FastAutomatonLearningMachine myAutomaton;
	private GlobalExplainabilityController globalExpController;
	private LocalExplainabilityController localExpController;
	private DataInterfaceController dataInputController;
	private TSNEController tsneController;
	private ContrastiveController contrastiveController;
	private XYChart.Series pin;
	private SyntheticController syntheticController;
	private HiddenStateCanvas hiddenStateController;
	private ScatterChart<Number,Number> sc;
	private JXMapViewerTab jxMap;
	private ReferenceMetrics metrics;
    private int number_clause_multiplier = 300;
    private double learn_rate = 100f;
    private Random rng;
    
    private boolean isTraining = false;
    
	DecimalFormat decimalFormat = new DecimalFormat("#.##");
	List<Integer> training_sample_list;
	
	private Stage primaryStage; 
	private Stage globalExpWindow = new Stage();
	private Scene globalExpScene;
	
	private Stage localExpWindow = new Stage();
	private Scene localExpScene;
	
	private Stage dataInputWindow = new Stage();
	private Scene dataInputScene;
	
	private Stage tsneWindow = new Stage();
	private Scene tsneScene;
	
	private Stage contrastiveWindow = new Stage();
	private Scene contrastiveScene;
	
	private Stage syntheticWindow = new Stage();
	private Scene syntheticScene;
	
	private Stage hiddenStateWindow = new Stage();
	private Scene hiddenStateScene;

	private SwingNode swingNode;

    @FXML
    private Menu geoColorMenu;
	
	@FXML
    private RadioMenuItem darthColor;
    @FXML
    private RadioMenuItem tonerColor;
    @FXML
    private RadioMenuItem tonerLiteColor;
    @FXML
    private RadioMenuItem waterColor;
    @FXML
    private RadioMenuItem burningColor;
    @FXML
    private RadioMenuItem regularColor;
    @FXML
    private RadioMenuItem modernColor;
    @FXML
    private RadioMenuItem blueColor;
	
	
	
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
    private CheckMenuItem tsneCheckbox;
    
    @FXML
    private CheckMenuItem contrastiveCheckbox;
    
    @FXML
    private CheckMenuItem syntheticCheckbox;
    
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
    
    @FXML
    private CheckBox boostCheck;
    
    @FXML
    private StackPane mapPane;
    
    
//    @FXML
//    private StackPane syntheticPane;
    
    ToggleGroup group,colorTheme;
    
	private double number_states_multiplier = 1.0;

	private int threshold_multiplier = 30;

	private int n_training_rounds = 100;

	private boolean update = true;

	private double split_ratio = .5;

	private int n_samples;
	
	private ArrayList<double[]> performanceRecord;
		
	Thread learnThread;
	Thread playThread;
	Thread spinThread;
	Task<Void> playtask;
	Task<Void> learnTask;
	Task<Void> spinTask;
	
	private int training_samples;
	private int test_samples;
	private String[] diagnosticName;
	private boolean regression;
	private boolean encodeContinuous;
	private int nClasses;
	private float[][] localSynthetic;

	
	Color[] regressionColors = null;
	
	Color[] defaultColors = new Color[] {Color.TEAL, Color.FUCHSIA,  Color.CORNFLOWERBLUE, Color.LIME, Color.LIGHTGREEN, Color.CRIMSON, Color.LIGHTPINK, Color.ALICEBLUE, Color.BLUEVIOLET, Color.DARKCYAN, 
			Color.DODGERBLUE, Color.FORESTGREEN, Color.FUCHSIA, Color.GOLD, Color.IVORY, Color.LIGHTGREEN, 	Color.LIGHTPINK, Color.LIME, 
			Color.MEDIUMORCHID, Color.OLIVEDRAB, Color.ORANGERED, Color.ROYALBLUE, Color.SEAGREEN, Color.TEAL};
	private double syntheticPaneHeight;
	private double syntheticPaneWidth;
	private String myMapStyle;
	private String[] mapnames;
	private int mapindex = 0;
	private StackPane glass;
	private Color[] tsneColors;
	private double[][] train_tsne;
	private double[][] test_tsne;
	private double tsne_max;
	private double tsne_min;
	private double[][] tsne_data;
	
	private double contrastive_max;
	private double contrastive_min;
	private double[][] contrastive_data;
	private double[][] train_contrastive;
	private double[][] test_contrastive;
	
	
	
	public void initiateRadioButtons() {
		
		group = new ToggleGroup();		
		learningMode.setToggleGroup(group);
		learningMode.setSelected(true);
		predictionMode.setToggleGroup(group);
		
		rng = new Random();
		
		
		colorTheme = new ToggleGroup();
		darthColor.setToggleGroup(colorTheme);
		darthColor.setSelected(true);
		
		tonerColor.setToggleGroup(colorTheme);
		tonerLiteColor.setToggleGroup(colorTheme);
		waterColor.setToggleGroup(colorTheme);
		burningColor.setToggleGroup(colorTheme);
		regularColor.setToggleGroup(colorTheme);
		modernColor.setToggleGroup(colorTheme);
		blueColor.setToggleGroup(colorTheme);

		mapnames = new String[]{"http://a.tile.stamen.com/toner", 
		"http://a.tile.stamen.com/toner-hybrid", 
		"http://a.tile.stamen.com/toner-lite",
		"http://b.tile.stamen.com/watercolor",
		"http://a.tile.stamen.com/terrain",
		"http://a.tile.stamen.com/mars",
		"https://a.tile.openstreetmap.org",
		"https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all"};
		
		
	}
	
	
	public void initiateAutomaton() throws Exception {
		
		if(dataInputController.getDataInterface() != null) {
			
			myAutomaton = new FastAutomatonLearningMachine(dataInputController.getBinarizer(), 
                    dataInputController.getData(), 
                    threshold_multiplier, 
                    number_clause_multiplier,  
                    (float)learn_rate,
                    dataInputController.isRegression(), 
                    dataInputController.getClassNames().length, 
                    boostCheck.isSelected());
						
			nClasses = dataInputController.getClassNames().length;
			regression = dataInputController.isRegression();
			encodeContinuous = dataInputController.isContinuousEncoder();
			n_samples = dataInputController.getData().getN_samples();
		
			globalExpController.setFeatureNameComboBox(dataInputController.getFeatureNames());
			globalExpController.setClassNameComboBox(dataInputController.getClassNames());
			globalExpController.setNClauses(myAutomaton.getNClauses());
			globalExpController.setAutomaton(this);
			
			localExpController.setFeatureNameComboBox(dataInputController.getFeatureNames());
			localExpController.setAutomaton(this);
			
			diagnosticName = new String[] {"Specificity", "Accuracy", "F1"};
			performanceRecord = new ArrayList<double[]>();
			
			newSampleButton.setDisable(false);
			beginTestButton.setDisable(false);
			stopButton.setDisable(false);
			beginTrainingButton.setDisable(false);
			
			int my_samples = dataInputController.getData().getDoubleData().length;
    		if(my_samples > 2000) {

    			int[] indices = new int[2000];
        		for(int i = 0; i < 2000; i++) {
        			indices[i] = rng.nextInt(my_samples);
        		}
        		double[][] sampled_data = MatrixOps.copyRows(dataInputController.getData().getDoubleData(), indices);
        		tsneController.setData(sampled_data);
    		}
    		else {
    			tsneController.setData(dataInputController.getData().getDoubleData());
    			
    			if(!regression) {								
    				contrastiveController.setForegroundData(dataInputController.getData().getDoubleDataClass(0));
        			contrastiveController.setBackgroundData(dataInputController.getData().getDoubleDataAllButClass(0));
    			}
    			else if(regression) {
    				contrastiveController.setForegroundData(dataInputController.getData().getForegroundRegressionData());
    				contrastiveController.setBackgroundData(dataInputController.getData().getBackgroundRegressionData());
    			}
    				
    		}
			
			
			
			
			tsneController.setAutomatonController(this);
			localSynthetic = new float[1][dataInputController.getFeatureNames().length];
			

			contrastiveController.setAutomatonController(this);
			
		    
			for(int i = 0; i < dataInputController.getFeatureNames().length; i++) {
	    		
	    		float[] uv = dataInputController.getBinarizer().getFeatureValues(i);	 
	    		int synthetic_val = rng.nextInt(uv.length);
	    		
	    		localSynthetic[0][i] = uv[synthetic_val];
	    		
	    		
			}
			
			regressionColors = new Color[dataInputController.getTargetResolution()];
			Color x = Color.CORNFLOWERBLUE;
		    Color y = Color.CRIMSON;
			for(int i = 0; i < dataInputController.getTargetResolution(); i++) {
				
				float inverse_blending = (float)i/dataInputController.getTargetResolution();
	    		float blending = (1f - inverse_blending);
	    	    double red =   x.getRed()   * blending   +   y.getRed()   * inverse_blending;
	    		double green = x.getGreen() * blending   +   y.getGreen() * inverse_blending;
	    		double blue =  x.getBlue()  * blending   +   y.getBlue()  * inverse_blending;

	    		regressionColors[i] = new Color (red, green, blue, 1.0);								
			}
			
			syntheticController.setAutomaton(this);
			syntheticController.updateSyntheticCanvas();

			
	    	training_sample_list = new ArrayList<Integer>();
			for(int i = 0; i < n_samples; i++) training_sample_list.add(i);
			Collections.shuffle(training_sample_list);
			
			
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
			
			if(regression) {
				myAutomaton.update(myAutomaton.getTransformedData()[rand_samp], myAutomaton.getRegTargets()[rand_samp]);
			}
			else {
				myAutomaton.update(myAutomaton.getTransformedData()[rand_samp], myAutomaton.getTargets()[rand_samp]);
			}
			

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
			
			if(regression) {
				localExpController.setPrediction(myAutomaton.decode(pred_class));
			}
			else if(encodeContinuous) localExpController.setPrediction((int)dataInputController.decodeClass(pred_class)); 
			else localExpController.setPrediction(pred_class);
			
			if(regression || encodeContinuous) localExpController.setTarget(myAutomaton.getRegTargets()[rand_samp]);
			else localExpController.setTarget(myAutomaton.getTargets()[rand_samp]);
			
			localExpController.sketchCanvas();
			
			if(encodeContinuous) {
				printRegressionSample(myAutomaton.getOriginalData().getSample(rand_samp), dataInputController.decodeClass(pred_class), myAutomaton.getRegTargets()[rand_samp]);
			}
			else if(regression) {
				printRegressionSample(myAutomaton.getOriginalData().getSample(rand_samp), myAutomaton.decode(pred_class), myAutomaton.getRegTargets()[rand_samp]); 
			}
			else {
				printSample(myAutomaton.getOriginalData().getSample(rand_samp), pred_class, myAutomaton.getTargets()[rand_samp]);
			}
			
			if(hiddenStateWindow.isShowing()) {
				addObservationTsne(rand_samp);
			}
			
		}
    	
    }

    
    public void computeSample(int rand_samp) throws ParseException {
    	
    	if(rand_samp < 0) {
    		diagnosticTextFlow.getChildren().clear();
    		localExpController.sketchNothing();		
    	}
    	else {
    		
    		int pred_class = myAutomaton.computeLocalFeatureImportance(myAutomaton.getTransformedData()[rand_samp]);
    		
    		localExpController.setPositive_features(myAutomaton.getLocPositiveFeatures());
    		localExpController.setNegative_features(myAutomaton.getLocNegativeFeatures());
    		
    		if(regression) {
    			localExpController.setPrediction(myAutomaton.decode(pred_class));
    		}
    		if(encodeContinuous) localExpController.setPrediction((int)dataInputController.decodeClass(pred_class)); 
    		else localExpController.setPrediction(pred_class);
    		
    		if(regression || encodeContinuous) localExpController.setTarget((int)myAutomaton.getRegTargets()[rand_samp]);
    		else localExpController.setTarget(myAutomaton.getTargets()[rand_samp]);
    		
    		localExpController.sketchCanvas();
    		
    		float[] synth = myAutomaton.getOriginalData().getSample(rand_samp);
    		for(int i = 0; i < synth.length; i++) {
    			localSynthetic[0][i] = synth[i];
    		}
    		syntheticController.updateSyntheticCanvas();
    		
    		
    		if(encodeContinuous) {
    			printRegressionSample(myAutomaton.getOriginalData().getSample(rand_samp), dataInputController.decodeClass(pred_class), myAutomaton.getRegTargets()[rand_samp]);
    		}
    		else if(regression) {
				printRegressionSample(myAutomaton.getOriginalData().getSample(rand_samp), myAutomaton.decode(pred_class), myAutomaton.getRegTargets()[rand_samp]);
			}
    		else {
    			printSample(myAutomaton.getOriginalData().getSample(rand_samp), pred_class, myAutomaton.getTargets()[rand_samp]);
    		}
    	}
    }
    
    public void computeSyntheticSample(int[] Xi) throws ParseException {
    	
    	int pred_class = myAutomaton.computeLocalFeatureImportance(Xi);
		
		localExpController.setPositive_features(myAutomaton.getLocPositiveFeatures());
		localExpController.setNegative_features(myAutomaton.getLocNegativeFeatures());
		
		if(encodeContinuous) localExpController.setPrediction((int)dataInputController.decodeClass(pred_class)); 
		else localExpController.setPrediction(pred_class);
				
		localExpController.sketchCanvas();
		
		if(encodeContinuous) {
			printRegressionSample(localSynthetic[0], dataInputController.decodeClass(pred_class), -1);
		}
		else if(regression) {
			printRegressionSample(localSynthetic[0], myAutomaton.decode(pred_class), -1);
		}
		else {
			printSample(localSynthetic[0], pred_class, -1);
		}
    	
    }
    
    
    
    public void switchClass() {
    	
    	if(globalExpController.getClassChoice() >= 0) {
    		myAutomaton.computeGlobalFeatureImportance(globalExpController.getClassChoice());
    	}
		
		
		globalExpController.setPositive_features(myAutomaton.getPositiveFeatures());
		globalExpController.setNegative_features(myAutomaton.getNegativeFeatures());
    	
    	
    }
    
    
    
    private void printSample(float[] original, int pred, int targ) {
		
    	diagnosticTextFlow.getChildren().clear();
		
		//diagnosticTabArea.setText(sb.toString());
		Text label = new Text("\n\n");
		diagnosticTextFlow.getChildren().add(label);

		String[] names = globalExpController.getFeature_names();
		
		for(int i = 0; i < names.length; i++) {
			
			label = new Text(names[i] + ": ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			
			if(names[i].contains("_")) {
				
				label = new Text(dataInputController.getCategoricalValue(i, (int)original[i]) + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier", 20));
				diagnosticTextFlow.getChildren().add(label);
				
			}
			else {
				
				label = new Text(original[i] + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier", 20));
				diagnosticTextFlow.getChildren().add(label);
			}
			

			
		}
		
		label = new Text("Prediction: ");
		label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
		label.setFont(Font.font ("Courier", 20));
		diagnosticTextFlow.getChildren().add(label);
		label = new Text(pred+ "\n");
		
		if(pred != targ) {
			label.setFill(Paint.valueOf(Color.RED.toString()));
		}
		else {
			label.setFill(Paint.valueOf(Color.LIGHTGREEN.toString()));
		}
		
		if(targ == -1) {
			label.setFill(Paint.valueOf(Color.LIGHTGREEN.toString()));
		}
		label.setFont(Font.font ("Courier", 20));
		diagnosticTextFlow.getChildren().add(label);
		
		
	}
    
    
    private void printRegressionSample(float[] original, float pred, float targ) {
		
    	diagnosticTextFlow.getChildren().clear();
		
		//diagnosticTabArea.setText(sb.toString());
		Text label = new Text("\n\n");
		diagnosticTextFlow.getChildren().add(label);

		double latitude = 0;
		double longitude = 0;
		
		String[] names = globalExpController.getFeature_names();
		String locationName = null;
		
		
		for(int i = 0; i < names.length; i++) {
			
			label = new Text(names[i] + ": ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			
			if(names[i].contains("_")) {
				
				label = new Text(dataInputController.getCategoricalValue(i, (int)original[i]) + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier", 20));
				diagnosticTextFlow.getChildren().add(label);
				
			}
			else {
				
				label = new Text(original[i] + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier", 20));
				diagnosticTextFlow.getChildren().add(label);
			}
			
			if(names[i].contains("City") || names[i].contains("Location")) {
				locationName = dataInputController.getCategoricalValue(i, (int)original[i]);
			}
			
			if(names[i].contains("Latitude")) {
				latitude = original[i];
			}
			if(names[i].contains("Longitude")) {
				longitude = original[i];
			}
		}
		
		label = new Text("Prediction: ");
		label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
		label.setFont(Font.font ("Courier", 20));
		label.setEffect(new Glow(1.0));
		diagnosticTextFlow.getChildren().add(label);
		label = new Text(pred+ "\n");
		label.setFill(Paint.valueOf(Color.LIGHTGREEN.toString()));
		label.setFont(Font.font ("Courier", 20));
		diagnosticTextFlow.getChildren().add(label);
		
		label = new Text("Target: ");
		label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
		label.setFont(Font.font ("Courier", 20));
		label.setEffect(new Glow(1.0));
		diagnosticTextFlow.getChildren().add(label);
		label = new Text(targ+ "\n");
		label.setFill(Paint.valueOf(Color.LIGHTGREEN.toString()));
		label.setFont(Font.font ("Courier", 20));
		diagnosticTextFlow.getChildren().add(label);
		
		//Identified lat/long in data 
		if(longitude != 0 && latitude != 0) {
			
			StringBuilder sb = new StringBuilder();
			
			glass.getChildren().clear();
	        for (Node node : diagnosticTextFlow.getChildren()) {
	            if (node instanceof Text) {
			
	            	String mynext = ((Text) node).getText();
	            	sb.append(" " + mynext);
	            	//if(!mynext.contains(":")) sb.append("\n");

	            }
	        }
			
	        
	        final Label mylabel = new Label(sb.toString());
        	mylabel.setStyle("-fx-text-fill: cyan; -fx-font: 14 \"courier\"; -fx-padding: 0 0 20 0; -fx-text-alignment: left");
        	mylabel.setEffect(new Glow(.7));
        	StackPane.setAlignment(mylabel, Pos.TOP_LEFT);
            glass.getChildren().addAll(mylabel);             	        
	        
            
            String fullText =  names[rng.nextInt(names.length)];        
	        while(fullText.contains("City")) {
	        	fullText = names[rng.nextInt(names.length)];
	        }
	        
	        float indicator = (targ - dataInputController.getData().getTarget_min())/
	        		(dataInputController.getData().getTarget_max() - dataInputController.getData().getTarget_min());
	        		
	        int whichColor = (int)(indicator*((float)(regressionColors.length-1)));
	        Color myColor = regressionColors[whichColor];
							
			addGeographicalObservation(latitude, longitude, myColor, sb.toString());
		}
		
		
		
		//Identified location in categorical data
		if(locationName != null) {
			
			double lat = 47.3769;
			double longi = 8.5417;
			StringBuilder sb = new StringBuilder();
			
			glass.getChildren().clear();
	        for (Node node : diagnosticTextFlow.getChildren()) {
	            if (node instanceof Text) {
			
	            	String mynext = ((Text) node).getText();
	            	sb.append(" " + mynext);
	            	//if(!mynext.contains(":")) sb.append("\n");

	            }
	        }
			
	        
	        final Label mylabel = new Label(sb.toString());
        	mylabel.setStyle("-fx-text-fill: cyan; -fx-font: 14 \"courier\"; -fx-padding: 0 0 20 0; -fx-text-alignment: left");
        	mylabel.setEffect(new Glow(.7));
        	StackPane.setAlignment(mylabel, Pos.TOP_LEFT);
            glass.getChildren().addAll(mylabel);             	        
	        
            
            String fullText =  names[rng.nextInt(names.length)];        
	        while(fullText.contains("City")) {
	        	fullText = names[rng.nextInt(names.length)];
	        }
	        
	        float indicator = (targ - dataInputController.getData().getTarget_min())/
	        		(dataInputController.getData().getTarget_max() - dataInputController.getData().getTarget_min());
	        		
	        int whichColor = (int)(indicator*((float)(regressionColors.length-1)));
	        Color myColor = regressionColors[whichColor];
			
			if(locationName.contains("Zurich")) {
				
				lat = 47.3769 + rng.nextGaussian()*.1; 
				longi = 8.5417 + rng.nextGaussian()*.1;
			}
			else if(locationName.contains("Bern")) {
				lat = 46.9480 + rng.nextGaussian()*.1; 
				longi = 7.4474 + rng.nextGaussian()*.1;
			}
			else if(locationName.contains("Geneva")) {
				lat = 46.2044 + rng.nextGaussian()*.1; 
				longi = 6.1432 + rng.nextGaussian()*.1;
			}
				
			addGeographicalObservation(lat, longi, myColor, sb.toString());
		}			
		repaintMap();
	}
    


	@FXML
    void handleNumClausesChange() {

    	number_clause_multiplier = (int)numClausesSlider.getValue();
    	numClausesText.setText("" + number_clause_multiplier);
    	
    	if(myAutomaton != null) myAutomaton.setNumClause(number_clause_multiplier);   	    	
    }

    @FXML
    void handleNumStatesChange() {

    	number_states_multiplier = numStatesSlider.getValue();
    	numStatesText.setText(decimalFormat.format(number_states_multiplier));
    	
    	  	
    	
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
    	
    	threshold_multiplier = (int)clauseThresholdSlider.getValue();
    	clauseThresholdText.setText("" + threshold_multiplier);
    	
    	if(myAutomaton != null) myAutomaton.setThresholdMultiplier(threshold_multiplier); 
    }

    @FXML
    void handleTrainSetSplit() {

    	split_ratio = traindingSplitSlider.getValue()/100f;;
    	traindingRoundsText1.setText(decimalFormat.format(split_ratio));
    	
    	if(myAutomaton != null) myAutomaton.setTrainSplitRatio((float)split_ratio);
    }

    @FXML
    void handleTraining(ActionEvent event) throws Exception {
		
		training_samples = (int)(split_ratio*n_samples);		
		test_samples = n_samples - training_samples;

		metrics = new ReferenceMetrics();
		
		globalExpController.setNClauses(myAutomaton.getNClauses());
		//isTraining = true;

		learnTask = new Task<Void>() {
	    	

			@Override
			protected Void call() throws Exception {
				
		    int count = 0;
		    
			for(int n = 0; n < n_training_rounds; n++) {			
				for(int i = 0; i < training_samples; i++) {
					
					int rand_samp = training_sample_list.get(i);	
					
					if(regression) {
						myAutomaton.update(myAutomaton.getTransformedData()[rand_samp], myAutomaton.getRegTargets()[rand_samp]);
					}
					else {
						myAutomaton.update(myAutomaton.getTransformedData()[rand_samp], myAutomaton.getTargets()[rand_samp]);
					}
					
//					myAutomaton.computeGlobalFeatureImportance(globalExpController.getClassChoice());
//					
//					globalExpController.setPositive_features(myAutomaton.getPositiveFeatures());
//					globalExpController.setNegative_features(myAutomaton.getNegativeFeatures());
				
					updateProgress(count, n_training_rounds*training_samples); 
					count++;

					if(learnTask.isCancelled()) break;
				}
				
				Thread.sleep(10);
				myAutomaton.computeGlobalFeatureImportance(globalExpController.getClassChoice());
				globalExpController.setPositive_features(myAutomaton.getPositiveFeatures());
				globalExpController.setNegative_features(myAutomaton.getNegativeFeatures());
				
				Platform.runLater(new Runnable() {
				    @Override
				    public void run() {
				    	generateTestReport();	
				    	try {
							globalExpController.sketchCanvas();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

				    }
				});
				
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
			isTraining = false;
			
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
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getTypeI_error() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);

			label = new Text("TypeIIError: ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getTypeII_error() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Sensitivity: ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getSensitivity() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Specificity: ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getSpecificity() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("Accuracy: ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getAccuracy() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("ErrorRate: ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getError_rate() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("F1: ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getF1_score() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("F2: ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getF2_score() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			diagnosticTextFlow.getChildren().add(label);
			
			label = new Text("MCC: ");
			label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
			label.setFont(Font.font ("Courier New", 20));
			label.setEffect(new Glow(1.0));
			diagnosticTextFlow.getChildren().add(label);
			label = new Text(metrics.getMCC() + "\n");
			label.setFill(Paint.valueOf(Color.YELLOW.toString()));
			label.setFont(Font.font ("Courier New", 20));
			
			diagnosticTextFlow.getChildren().add(label);
            
    	}
    	
    	
    }
    

    public void generateTestReport() {
    	
    	
    	if(training_sample_list != null && myAutomaton != null) {
        	
    		
    		if(regression || encodeContinuous) {
    			computeRegressionError();
    		}
    		
    		else {
        	
	    		training_samples = (int)(split_ratio*n_samples);	
	    		test_samples = n_samples - training_samples;
	    		int target = 0;
	    		int rand_samp;
		    	int count_false = 0;
				int count_true = 0;
				int count_true_positive = 0;
				int count_true_negative = 0;
				int count_false_negative = 0;
				int count_false_positive = 0;
				int nsamps = test_samples;
				
				if(isTraining) {
					nsamps = training_samples;
				}
				
				for(int i = 0; i < nsamps; i++) {
					
					    if(isTraining) rand_samp = training_sample_list.get(i);
					    else rand_samp = training_sample_list.get(i + training_samples);
						
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
				
				
				metrics.addMetrics(X, Y, Q, Z);
				metrics.computeMeans();
				
				diagnosticTextFlow.getChildren().clear();
				
				//diagnosticTabArea.setText(sb.toString());
				Text label = new Text("\n\n");
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("TypeIError: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getTypeI_error() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
	
				label = new Text("TypeIIError: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getTypeII_error() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("Sensitivity: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getSensitivity() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("Specificity: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getSpecificity() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("Accuracy: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getAccuracy() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("ErrorRate: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getError_rate() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("F1: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getF1_score() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("F2: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getF2_score() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("MCC: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getMCC() + "\n\n\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
	            
				label = new Text("Mean Accuracy: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getMeanAccuracy() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("Mean TypeIError: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getMeanTypeI() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("Mean TypeIIError: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getMeanTypeII() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				label = new Text("Mean F1 Score: ");
				label.setFill(Paint.valueOf(Color.LIGHTSKYBLUE.toString()));
				label.setFont(Font.font ("Courier New", 20));
				label.setEffect(new Glow(1.0));
				diagnosticTextFlow.getChildren().add(label);
				label = new Text(metrics.getMeanFI() + "\n");
				label.setFill(Paint.valueOf(Color.YELLOW.toString()));
				label.setFont(Font.font ("Courier New", 20));
				diagnosticTextFlow.getChildren().add(label);
				
				
				double[] perf = new double[] { 
						                      
						                      metrics.getSpecificity(),
						                      metrics.getAccuracy(),
						                      
						                      metrics.getF1_score(),
						                      };
				
				performanceRecord.add(perf);
						
				historicalPane.getChildren().set(0, TimeSeriesCanvas.createBarChart(performanceRecord, diagnosticName));
    		}		                      
			
    	} 	
    }
    
    
    public void computeRegressionError() {
    	

    	if(training_sample_list != null && myAutomaton != null) {
        	
    		training_samples = (int)(split_ratio*n_samples);	
    		test_samples = n_samples - training_samples;
    		float l2error = 0;
    		float target = 0f;
			int nsamps = test_samples;
			float prediction = 0f;
			
			
			for(int i = 0; i < nsamps; i++) {
				
				   int rand_samp = training_sample_list.get(training_samples + i);

				   if(regression) {
					   prediction = myAutomaton.predict_regression(myAutomaton.getTransformedData()[rand_samp]);
					   target = myAutomaton.getRegTargets()[rand_samp];
				   }
				   else {
					   prediction = dataInputController.decodeClass(myAutomaton.predict(myAutomaton.getTransformedData()[rand_samp]));
					   target = dataInputController.decodeClass(myAutomaton.getTargets()[rand_samp]);					   
				   }


						   
				   l2error += Math.abs(prediction - target)*Math.abs(prediction - target);

		    }
			
			l2error = (float) (Math.sqrt(l2error)/nsamps);		
			System.out.println("l2 Error: " + l2error);   
			
			double[] perf = new double[] {l2error};
			
			performanceRecord.add(perf);
			diagnosticName = new String[] {"L2 Error"};
			historicalPane.getChildren().set(0, TimeSeriesCanvas.createBarChart(performanceRecord, diagnosticName));
			
			
		}

    }
    

    public void initiateCanvas() throws ParseException {		
		historicalPane.getChildren().add(TimeSeriesCanvas.createBarChart(performanceRecord, diagnosticName));
	}
    

    public void initializeGeografica() {
    	
    	swingNode = new SwingNode();  
    	jxMap = new JXMapViewerTab();
    	
    	jxMap.initializeMap(swingNode, mapnames[0]);  	
    	//mapPane.getChildren().add(swingNode);
    	
    	glass = new StackPane();
        //StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
        //glass.getChildren().addAll(label);
        glass.setStyle("-fx-background-color: rgba(0, 100, 100, 0.3); -fx-background-radius: 10;");
        glass.setMaxWidth(250);
        glass.setMaxHeight(300);
        StackPane.setAlignment(glass, Pos.TOP_LEFT);
//        mapPane.getChildren().add(swingNode);
//        mapPane.getChildren().add(glass);
    	
    
    	repaintMap();
    }
    
    
    @FXML
    void handleChangeMapTheme(ActionEvent event) {

    	mapindex = 0;
    	String myevent = event.toString();

    	if(myevent.contains("darthColor"))  mapindex = 0;
    	else if(myevent.contains("tonerColor"))  mapindex = 1;
    	else if(myevent.contains("tonerLiteColor"))  mapindex = 2;
    	else if(myevent.contains("waterColor"))  mapindex = 3;
    	else if(myevent.contains("burningColor"))  mapindex = 4;
    	else if(myevent.contains("regularColor"))  mapindex = 5;
    	else if(myevent.contains("modernColor"))  mapindex = 6;
    	else if(myevent.contains("blueColor"))  mapindex = 7;
    	
    	jxMap.initializeMap(swingNode, mapnames[mapindex]);   	
//    	mapPane.getChildren().clear();
//    	mapPane.getChildren().addAll(swingNode,glass);
    	
    	repaintMap();
    	
    }
    
    void addGeographicalObservation(double lat, double longi, Color col, String text) {
    	
    	jxMap.addGeoPosition(lat, longi, new java.awt.Color((float)col.getRed(), (float)col.getGreen(), (float)col.getBlue()), text);  	
    }
    
    void repaintMap() {
    	
    	mapPane.getChildren().clear();
    	mapPane.getChildren().add(swingNode);
    	mapPane.getChildren().add(glass);
    	jxMap.createSwingContent(swingNode);
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
    
    
    public void setTsneController() throws IOException, ParseException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("tsnePanel.fxml"));
		Parent root = (Parent)loader.load();
		tsneController = loader.getController();

		
		tsneScene = new Scene(root);
		tsneScene.getStylesheets().add("css/WhiteOnBlack.css");
		tsneWindow = new Stage();
		tsneWindow.setScene(tsneScene);
		tsneWindow.setX(primaryStage.getX() + 250);
		tsneWindow.setY(primaryStage.getY() + 100);
		
		tsneWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	tsneCheckbox.setSelected(false);
            }
        });
		
		tsneController.initializeTSNE();
		tsneController.setStage(primaryStage);
		
	}
    
    public void setContrastiveController() throws IOException, ParseException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("contrastivePanel.fxml"));
		Parent root = (Parent)loader.load();
		contrastiveController = loader.getController();

		
		contrastiveScene = new Scene(root);
		contrastiveScene.getStylesheets().add("css/WhiteOnBlack.css");
		contrastiveWindow = new Stage();
		contrastiveWindow.setScene(contrastiveScene);
		contrastiveWindow.setX(primaryStage.getX() + 250);
		contrastiveWindow.setY(primaryStage.getY() + 100);
		
		contrastiveWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	contrastiveCheckbox.setSelected(false);
            }
        });
		
		contrastiveController.initializeContrastive();
		contrastiveController.setStage(primaryStage);
		
	}
    
    public void setSyntheticController() throws IOException, ParseException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("syntheticPanel.fxml"));
		Parent root = (Parent)loader.load();
		syntheticController = loader.getController();

		
		syntheticScene = new Scene(root);
		syntheticScene.getStylesheets().add("css/WhiteOnBlack.css");
		syntheticWindow = new Stage();
		syntheticWindow.setScene(syntheticScene);
		syntheticWindow.setX(primaryStage.getX() + 250);
		syntheticWindow.setY(primaryStage.getY() + 100);
		
		syntheticWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	syntheticCheckbox.setSelected(false);
            }
        });
		
	
		syntheticController.initiateSyntheticCanvas();
		syntheticController.setStage(primaryStage);
		
	}
    
	public void setHiddenStateController() throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("hiddenState3DPanel.fxml"));
		Parent root = (Parent)loader.load();
		hiddenStateController = loader.getController();
		
		hiddenStateScene = new Scene(root);
		hiddenStateScene.getStylesheets().add("css/WhiteOnBlack.css");
		
		hiddenStateWindow = new Stage();
		hiddenStateWindow.setScene(hiddenStateScene);
		hiddenStateWindow.setX(primaryStage.getX() + 250);
		hiddenStateWindow.setY(primaryStage.getY() + 100);
		hiddenStateWindow.setTitle("3D cPCA and tsne");
			
		hiddenStateController.createHiddenState(hiddenStateScene);	
	}
	
	public void show3DTsne() {
		
		hiddenStateWindow.show();
	
	}
    
    
    
    @FXML
    void handleSyntheticCheckbox(ActionEvent event) {

		if(syntheticCheckbox.isSelected()) {
			syntheticWindow.show();
		}
		else {
			syntheticWindow.close();
		}	
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
    
    @FXML
    void handleTsneCheckbox(ActionEvent event) {

    	if(tsneCheckbox.isSelected()) {
    		tsneWindow.show();
		}
		else {
			tsneWindow.close();
		}	
    	
    }
    
    @FXML
    void handleContrastiveCheckbox(ActionEvent event) {

    	if(contrastiveCheckbox.isSelected()) {
    		contrastiveWindow.show();
		}
		else {
			contrastiveWindow.close();
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

	
	public void create3DTsneChart(double[][] data, double[] mins, double[] maxs, Color[] colors) {
		
		hiddenStateController.updateGrid(data, mins, maxs, colors);	
	}


	

    public ScatterChart createScatterChart(double[][] data) throws ParseException {
		
		final NumberAxis xAxis = new NumberAxis();
	    final NumberAxis yAxis = new NumberAxis();
		
	    sc = new ScatterChart<Number,Number>(xAxis,yAxis);
	    
	    Color myColor;
	    tsne_max = Double.MIN_VALUE;
	    tsne_min = Double.MAX_VALUE;
	    	    
	    sc.setTitle("t-Distributed Stochastic Neighbor Embedding");
	    sc.setAnimated(false);
	    sc.setLegendVisible(false);
	    
	    
	    int rand_samp;
	    tsne_data = data.clone();
	    int n_samples = tsne_data.length;
	    int training_samples = (int)(split_ratio*n_samples);	
		int test_samples = n_samples - training_samples;
	    
	    train_tsne = new double[training_samples][3];
	    test_tsne = new double[test_samples][3];
	    	    
	    for(int i = 0; i < data.length; i++) {
	    	
	    	if(i < training_samples) {
	    		rand_samp = i;	
	    		train_tsne[i] = data[rand_samp];
	    	}
	    	else {
	    		rand_samp = i;	
	    		test_tsne[i - training_samples] = data[rand_samp];
	    	}
	    }
	    
	    
	    if(data != null) {
	    	
	    	for(int i = 0; i < data.length; i++) {
	    		if(data[i][2] > tsne_max) tsne_max = data[i][2];
	    		else if(data[i][2] < tsne_min) tsne_min = data[i][2];
	    	}
	    	
	
	        pin = new XYChart.Series();
  
	        tsneColors = new Color[train_tsne.length];
	        
	        for(int i = 0; i < train_tsne.length; i++) {
	        	
	        	final XYChart.Data<Number, Number> dataXY = new XYChart.Data(train_tsne[i][0], train_tsne[i][1]);
	        	
	        	float trans = (float) (.9f*(train_tsne[i][2] - tsne_min)/(tsne_max - tsne_min) + .1f);
	        	
	        	if(dataInputController.isContinuousEncoder()) {
	        		myColor = regressionColors[dataInputController.getData().getLabels()[training_sample_list.get(i)]%(regressionColors.length-1)];
	        	}
	        	else if(dataInputController.isRegression()) {
	        		
	        		float indicator = (dataInputController.getData().getRegLabels()[training_sample_list.get(i)] - dataInputController.getData().getTarget_min())/
	        		(dataInputController.getData().getTarget_max() - dataInputController.getData().getTarget_min());
	        		
	        		int whichColor = (int)(indicator*((float)(regressionColors.length-1)));
	        		myColor = regressionColors[whichColor];
	        	}
	        	else {
	        		myColor = defaultColors[dataInputController.getData().getLabels()[training_sample_list.get(i)]%(defaultColors.length-1)];	        		
	        	}
	        	
	        	tsneColors[i] = myColor;
	        	
	        	
	        	String rgb = String.format("%d, %d, %d",
	        	        (int) (myColor.getRed() * 255),
	        	        (int) (myColor.getGreen() * 255),
	        	        (int) (myColor.getBlue() * 255));
	        	      	
	        	dataXY.setNode(new HoveredThresholdNode(training_sample_list.get(i), rgb, trans ));

	        	pin.getData().add(dataXY);
	        }      
	        sc.getData().addAll(pin);
	        
	    }
		
	    
	    create3DTsneChart(train_tsne, tsneController.getMins(), tsneController.getMaxs(), tsneColors);
	    show3DTsne();
	    
		return sc;
    }
    
    

    public ScatterChart createContrastiveScatterChart(double[][] data) throws ParseException {
		
		final NumberAxis xAxis = new NumberAxis();
	    final NumberAxis yAxis = new NumberAxis();
		
	    sc = new ScatterChart<Number,Number>(xAxis,yAxis);
	    
	    Color myColor;
	    contrastive_max = -Double.MAX_VALUE;
	    contrastive_min = Double.MAX_VALUE;
	    	    
	    sc.setTitle("Contrastive PCA");
	    sc.setAnimated(false);
	    sc.setLegendVisible(false);
	    
	    
	    int rand_samp;
	    contrastive_data = data.clone();
	    int n_samples = contrastive_data.length;
	    int training_samples = (int)(split_ratio*n_samples);	
		int test_samples = n_samples - training_samples;
	    
	    train_contrastive = new double[training_samples][3];
	    test_contrastive = new double[test_samples][3];
	    tsneColors = new Color[train_contrastive.length];	    
	    
	    for(int i = 0; i < data.length; i++) {
	    	
	    	if(i < training_samples) {
	    		rand_samp = training_sample_list.get(i);
	    		train_contrastive[i] = data[rand_samp];
	    	}
	    	else {
	    		rand_samp = training_sample_list.get(training_samples + rng.nextInt(test_samples));		
	    		test_contrastive[i - training_samples] = data[rand_samp];
	    	}
	    }
	    
	    
	    if(data != null) {
	    	
	    	for(int i = 0; i < data.length; i++) {
	    		if(data[i][2] > contrastive_max) contrastive_max = data[i][2];
	    		else if(data[i][2] < contrastive_min) contrastive_min = data[i][2];
	    	}
	    	
	
	        pin = new XYChart.Series();
  
	        tsneColors = new Color[train_contrastive.length];
	        
	        for(int i = 0; i < train_contrastive.length; i++) {
	        	
	        	final XYChart.Data<Number, Number> dataXY = new XYChart.Data(train_contrastive[i][0], train_contrastive[i][1]);
	        	
	        	float trans = (float) (.9f*(train_contrastive[i][2] - contrastive_min)/(contrastive_max - contrastive_min) + .1f);
	        	
	        	if(dataInputController.isContinuousEncoder()) {
	        		myColor = regressionColors[dataInputController.getData().getLabels()[training_sample_list.get(i)]%(regressionColors.length-1)];
	        	}
	        	else if(dataInputController.isRegression()) {
	        		
	        		float indicator = (dataInputController.getData().getRegLabels()[training_sample_list.get(i)] - dataInputController.getData().getTarget_min())/
	        		(dataInputController.getData().getTarget_max() - dataInputController.getData().getTarget_min());
	        		
	        		int whichColor = (int)(indicator*((float)(regressionColors.length-1)));
	        		myColor = regressionColors[whichColor];
	        	}
	        	else {
	        		myColor = defaultColors[dataInputController.getData().getLabels()[training_sample_list.get(i)]%(defaultColors.length-1)];	        		
	        	}
	        	
	        	tsneColors[i] = myColor;
	        	
	        	
	        	String rgb = String.format("%d, %d, %d",
	        	        (int) (myColor.getRed() * 255),
	        	        (int) (myColor.getGreen() * 255),
	        	        (int) (myColor.getBlue() * 255));
	        	      	
	        	dataXY.setNode(new HoveredThresholdNode(training_sample_list.get(i), rgb, trans ));

	        	pin.getData().add(dataXY);
	        }      
	        sc.getData().addAll(pin);
	        
	    }
		
	    
	    create3DTsneChart(train_contrastive, contrastiveController.getMins(), contrastiveController.getMaxs(), tsneColors);
	    show3DTsne();
	    
		return sc;
    }
    
    
    
    public void addObservationTsne(int samp) {
    	

    	Color myColor;
    	double[] tsne_new = tsne_data[samp];
    	
    	final XYChart.Data<Number, Number> dataXY = new XYChart.Data(tsne_data[samp][0], tsne_data[samp][1]);
    	
    	float trans = (float) (.9f*(tsne_data[samp][2] - tsne_min)/(tsne_max - tsne_min) + .1f);
    	
    	if(dataInputController.isContinuousEncoder()) {
    		myColor = regressionColors[dataInputController.getData().getLabels()[samp]%(regressionColors.length-1)];
    	}
    	else if(dataInputController.isRegression()) {
    		
    		float indicator = (dataInputController.getData().getRegLabels()[samp] - dataInputController.getData().getTarget_min())/
    		(dataInputController.getData().getTarget_max() - dataInputController.getData().getTarget_min());
    		
    		int whichColor = (int)(indicator*((float)(regressionColors.length-1)));
    		myColor = regressionColors[whichColor];
    	}
    	else {
    		myColor = defaultColors[dataInputController.getData().getLabels()[samp]%(defaultColors.length-1)];	        		
    	}
    	

    	String rgb = String.format("%d, %d, %d",
    	        (int) (myColor.getRed() * 255),
    	        (int) (myColor.getGreen() * 255),
    	        (int) (myColor.getBlue() * 255));
    	      	
    	dataXY.setNode(new HoveredThresholdNode(samp, rgb, trans ));

    	pin.getData().add(dataXY);
         
        hiddenStateController.updateGrid(tsne_new, myColor);
                	
    }
    
    

    class HoveredThresholdNode extends StackPane {
    	
    	private int sample_index = -1;
    	
    	public HoveredThresholdNode(int value, String rgb, float trans) {
    	    	
    	      setPrefSize(12, 12);
    	      setStyle("-fx-background-color: rgba(" + rgb + ", " + decimalFormat.format(trans) + ");");
    	
    	      setOnMouseEntered(new EventHandler<MouseEvent>() {
    	        @Override public void handle(MouseEvent mouseEvent) {
    	        	sample_index = value;
    	        	try {
						computeSample(sample_index);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	        	setEffect();
    	        }
    	      });
    	      setOnMouseExited(new EventHandler<MouseEvent>() {
    	        @Override public void handle(MouseEvent mouseEvent) {
    	          sample_index = -1;
    	          setEffectOff();
    	          try {
					computeSample(sample_index);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	        }
    	      });
    	}
    	
    	public void setEffect() {
    		this.setEffect(new Glow(1.0));
    		
    	}
    	
    	public void setEffectOff() {
    		this.setEffect(null);
    	}
    	
    	public int getSampleIndex() {
    		return sample_index;
    	}

    }
    
    

    public static ScatterChart createScatterChart() throws ParseException {
		
		final NumberAxis xAxis = new NumberAxis();
	    final NumberAxis yAxis = new NumberAxis();
		
	    final ScatterChart<Number,Number> sc = new
	            ScatterChart<Number,Number>(xAxis,yAxis);
	    
		return sc;
    }
    
    public static ScatterChart createSyntheticChart() throws ParseException {
		
		final CategoryAxis xAxis = new CategoryAxis();
	    final NumberAxis yAxis = new NumberAxis();
		
	    final ScatterChart<String,Number> sc = new
	            ScatterChart<String,Number>(xAxis,yAxis);

	    
	    
		return sc;
    }
    

    public ScatterChart createSyntheticDataChart() throws ParseException {
		
		final CategoryAxis xAxis = new CategoryAxis();
	    final NumberAxis yAxis = new NumberAxis(0,1.0,.10);
		
	    final ScatterChart<String,Number> sc = new
	            ScatterChart<String,Number>(xAxis,yAxis);

	    
	    if(myAutomaton != null) {
	    	
	    	sc.setTitle("Synthetic Data Generator");
		    sc.setAnimated(false);
		    sc.setLegendVisible(false);
		    
		    String[] names = globalExpController.getFeature_names();
		    
		    XYChart.Series synth = new XYChart.Series();
		    
		    
	    	for(int i = 0; i < names.length; i++) {
	    		
	    		float[] uv = dataInputController.getBinarizer().getFeatureValues(i);	 
	    		int synthetic_val = rng.nextInt(uv.length);
	    		
	    		localSynthetic[0][i] = uv[synthetic_val];
	    		float val = (float)synthetic_val/(float)uv.length;	    		
	    		final XYChart.Data<String, Number> dataXY = new XYChart.Data(names[i], val);
	    		
	    		dataXY.setNode(new AdjustableNode(i, uv));
	    		
	    		synth.getData().add(dataXY);
	    		
	    	}
	    	sc.getData().addAll(synth);
	    }
	    
		return sc;
    }
    
    

    public ScatterChart updateSyntheticDataChart() throws ParseException {
		
		final CategoryAxis xAxis = new CategoryAxis();
	    final NumberAxis yAxis = new NumberAxis(0,1.0,.10);
		
	    final ScatterChart<String,Number> sc = new
	            ScatterChart<String,Number>(xAxis,yAxis);

	    
	    if(myAutomaton != null) {
	    	
	    	sc.setTitle("Synthetic Data Generator");
		    sc.setAnimated(false);
		    sc.setLegendVisible(false);
		    
		    String[] names = globalExpController.getFeature_names();
		    
		    XYChart.Series synth = new XYChart.Series();
		    
		    
	    	for(int i = 0; i < names.length; i++) {
	    		
	    		float[] uv = dataInputController.getBinarizer().getFeatureValues(i);	 
	    		
	    		int j = 0;
	    		while(j < uv.length) {
	    			if(localSynthetic[0][i] != uv[j]) j++;
	    			else break;
	    		}

	    		float val = (float)j/(float)uv.length;	    		
	    		final XYChart.Data<String, Number> dataXY = new XYChart.Data(names[i], val);
	    		
	    		dataXY.setNode(new AdjustableNode(i, uv));
	    		
	    		synth.getData().add(dataXY);
	    		
	    	}
	    	sc.getData().addAll(synth);
	    }	    
		return sc;
    }
    
    
    class AdjustableNode extends StackPane {
    	
    	double orgSceneX, orgSceneY, val;
        double orgTranslateX, orgTranslateY;
    	
        double maxSceneY = 460.0;
        double minSceneY = 40.0;
        private float feature_val;
        private int which;
        
    	public AdjustableNode(int sel, float[] uv) {
	    	
  	      setPrefSize(30, 30);
  	      setStyle("-fx-background-color: #4682B4;");
  	
  	      setOnMousePressed(new EventHandler<MouseEvent>() {
  	        @Override public void handle(MouseEvent mouseEvent) {
  	        	setEffect();
  	        	orgSceneY = mouseEvent.getSceneY();
  	            orgTranslateY = ((AdjustableNode)(mouseEvent.getSource())).getTranslateY();
  	        }
  	      });
  	      
  	      setOnMouseDragged(new EventHandler<MouseEvent>() {
  	        

			

			@Override public void handle(MouseEvent mouseEvent) {
  	        	
  	            double offsetY = mouseEvent.getSceneY() - orgSceneY;
  	            double newTranslateY = orgTranslateY + offsetY;
  	            
  	            if(mouseEvent.getSceneY() < maxSceneY && mouseEvent.getSceneY() > minSceneY) {
  	            	
  	            	((AdjustableNode)(mouseEvent.getSource())).setTranslateY(newTranslateY);
  	            	
  	            	val = Math.min(1.0, 1.0 - (mouseEvent.getSceneY() - minSceneY)/(maxSceneY - minSceneY));
  	            	val = Math.max(0, val);
  	            	
  	            	which = (int) (val*(uv.length-1));     	
  	            	localSynthetic[0][sel] = uv[which];
  	            	
  	            	try {
  	            		computeSyntheticSample(myAutomaton.translateLocalData(localSynthetic));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
  	            }
  	            
  	        	
  	            //System.out.println(" SceneY: " + mouseEvent.getSceneY() + " FeatSelection: " + sel + " Which: " + which + " value: " + localSynthetic[0][sel]);
  	        	
  	        	setEffect();  	        	
  	        }
  	      });
  	      
  	      setOnMouseEntered(new EventHandler<MouseEvent>() {
  	        @Override public void handle(MouseEvent mouseEvent) {
  	        	setEffect();
  	        }
  	      });
  	      
  	      setOnMouseExited(new EventHandler<MouseEvent>() {
  	        @Override public void handle(MouseEvent mouseEvent) {      
  	          setEffectOff();
  	      }
  	   });
  	}
  	
  	public void setEffect() {
  		this.setEffect(new Glow(1.0));
  		
  	}
  	
  	public void setEffectOff() {
  		this.setEffect(null);
  	}
    	
   }
    
}




