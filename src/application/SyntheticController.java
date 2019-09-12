package application;

import java.text.ParseException;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SyntheticController {


	private AutomatonMachineController myAutomaton;
	
    @FXML
    private StackPane syntheticPane;
	
	private Window primaryStage;

	private double syntheticPaneHeight;

	private double syntheticPaneWidth;

	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void setAutomaton(AutomatonMachineController myAutomaton) {
		this.myAutomaton = myAutomaton;
	}
	
    public void initiateSyntheticCanvas() throws ParseException {
		
		syntheticPane.getChildren().add(AutomatonMachineController.createScatterChart());
	}
    
    public void updateSyntheticCanvas() throws ParseException {
    	
    	syntheticPane.getChildren().set(0,myAutomaton.updateSyntheticDataChart());
    		
    	syntheticPaneHeight = syntheticPane.getHeight();
    	syntheticPaneWidth = syntheticPane.getWidth();
    }
    
    
	
}
