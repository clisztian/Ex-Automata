package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class ExAutomaton extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("tsetlinMachineInterface.fxml"));
			Parent root = (Parent)loader.load();
			
			AutomatonMachineController myController = loader.getController();
			myController.setStage(primaryStage);		
	        primaryStage.setTitle("Ex Automata");
	        
	        Scene primaryScene = new Scene(root);
	        primaryScene.getStylesheets().add("css/WhiteOnBlack.css");
	        primaryStage.setScene(primaryScene);
	        
	        myController.setDataInputController();
	        myController.setGlobalExpController();
	        myController.setLocalExpController();
	        myController.setTsneController();
	        myController.setContrastiveController();
	        myController.setHiddenStateController();
	        myController.setSyntheticController();
	        myController.initiateRadioButtons();
	        myController.initiateCanvas();
	        myController.initializeGeografica();
	        myController.setInterpretableRangeController();
 
	        primaryStage.show();
	        
	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
