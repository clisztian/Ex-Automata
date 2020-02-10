package application;
import java.text.DecimalFormat;
import java.text.ParseException;

import cpca.CPCA;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ContrastiveController {

	
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
    private ComboBox<String> featureCombo;

    DecimalFormat df = new DecimalFormat();
	private Window primaryStage;
	private AutomatonMachineController myAutomaton;
	private double[] mins;
	private double[] maxs;

	private double alpha = 0;
	private double[][] myForeground;
	private double[][] myBackground;
	private double[][] reducedA;

	
	public void setAutomatonController(AutomatonMachineController cont) {
		this.myAutomaton = cont;
	}
	
	
    public void initializeContrastive() throws ParseException {
    	
    	df.setMaximumFractionDigits(2);
    	contrastiveCanvas.getChildren().add(AutomatonMachineController.createScatterChart());
    }
    
    public void setForegroundData(double[][] foreground) {
    	this.myForeground = foreground; 	
    }
    
    public void setBackgroundData(double[][] background) {
    	this.myBackground = background; 	
    }
    
    
    @FXML
    void handleCompute(ActionEvent event) throws ParseException {
    	computeContrastivePCA();
    }

    @FXML
    void handleAlphaChange() throws ParseException {

    	alpha = alphaSlider.getValue()*.05f;
    	dimsText.setText("" + df.format(alpha));
    	
    	if(realTimeCheckBox.isSelected()) {
    		computeContrastivePCA();
    	}
    	
    }
	
    @FXML
    void handleFeatureFinder(ActionEvent event) {
    	
    }
    
    public void computeContrastivePCA() throws ParseException {
    	
    	
    	CPCA contrast = new CPCA((float)alpha, scaleCheckBox.isSelected(), 3);  	
    	contrast.computeForeground(myForeground);
		contrast.computeBackground(myBackground);
		double[] eigen = contrast.computeCPCA();
				
		reducedA = contrast.getReducedDoubleDataset(myForeground, myBackground);
		
		findExtremes();
		sketchCanvas();
    }
    
    
    private void sketchCanvas() throws ParseException {		
		contrastiveCanvas.getChildren().set(0,myAutomaton.createContrastiveScatterChart(reducedA));
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
		
		for(int i = 0; i < reducedA.length; i++) {
			
			for(int j = 0; j < 3; j++) {
				
				if(reducedA[i][j] > maxs[j]) {
					maxs[j] = reducedA[i][j];
				}
				else if(reducedA[i][j] < mins[j]) {
					mins[j] = reducedA[i][j];
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
