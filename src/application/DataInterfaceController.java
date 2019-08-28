package application;
import java.beans.FeatureDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import data.DataInterface;
import data.TrainingData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import tools.Binarizer;

public class DataInterfaceController {

	
	private Binarizer bin;
	private TrainingData data;
	private String[] feature_names;

	final FileChooser fileChooser = new FileChooser();
	
	public void initializeController() {
		
			
	}
	
	    @FXML
	    private Button getFileButton;

	    @FXML
	    private TextField fileNameText;

	    @FXML
	    private Slider binarizerSlider;

	    @FXML
	    private TextField binarizerText;

	    @FXML
	    private Button binaryTransformButton;

	    @FXML
	    private Button getHeadersButton;

	    @FXML
	    private TextArea headersTextArea;

	    public int bit_size = 10;
	    private DataInterface dataInterface;
	    private Window primaryStage;
	    
	    @FXML
	    void handleBinarizerDimension(MouseEvent event) {

	    	bit_size = (int)binarizerSlider.getValue();
	    	binarizerText.setText(""+bit_size);
	    }

	    @FXML
	    void handleBinaryTransform(ActionEvent event) throws Exception {

	    	if(dataInterface != null) {
	    		
	    		feature_names = dataInterface.setFeatureNames();
	    		data = dataInterface.getTwoClassData(); 	

	    		bin = new Binarizer(bit_size);		
	    		bin.fit(data.getData());	    		
	    	}
	    }

	    @FXML
	    void handleFileOpen(ActionEvent event) throws FileNotFoundException {

	    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("csv files (*.csv)", "*.csv");
			fileChooser.getExtensionFilters().add(extFilter);
			fileChooser.setTitle("Open csv File");
			List<File> filelist = fileChooser.showOpenMultipleDialog(primaryStage);		
			
			dataInterface = new DataInterface(filelist.get(0).getAbsolutePath(), ',');
			dataInterface.instantiateDataFeed();
			fileNameText.setText(filelist.get(0).getAbsolutePath());
			
	    }

	    @FXML
	    void handleGetHeaders(ActionEvent event) throws IOException {

	    	if(data != null) {    		
	    		
	    		feature_names = dataInterface.setFeatureNames();
	    		
	    		StringBuilder sb = new StringBuilder();
	    		for(int i = 0; i < feature_names.length; i++) {
	    			sb.append(feature_names[i] + "\n");
	    		}
	    		
	    		headersTextArea.setText(sb.toString());
	    		
	    	}
	    	
	    }
	    
	    public String[] getFeatureNames() {
	    	return feature_names;
	    }

		public void setStage(Stage primaryStage2) {
			this.primaryStage = primaryStage2;
			
		}

		public int getBitSize() {
			return bit_size;
		}

		public TrainingData getTrainingData() throws Exception {
			return dataInterface.getTwoClassData();
		}
		
		public DataInterface getDataInterface() {
			return dataInterface;
			
		}

		public Binarizer getBinarizer() {
			return bin;
		}

		public TrainingData getData() {
			return data;
		}


}

	
	

