package application;
import java.beans.FeatureDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import data.DataInterface;
import data.TrainingData;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
    public int bit_size = 10;
    private DataInterface dataInterface;
	final FileChooser fileChooser = new FileChooser();
	
	public void initializeController() {
		
			/**
			 * To do: 
			 * Multiclass classification
			 * Continuous data regression (or classification)
			 * 
			 */
		String[] formats = new String[] {"Classification", "MultivarRegression", "EncodeDecode"};
		
		problemTypeChoiceBox.getItems().addAll(formats);
		problemTypeChoiceBox.getSelectionModel().selectFirst();
		
		
		
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

	    @FXML
	    private ListView<String> listHeaders;

	    @FXML
	    private Button dataPipelineButton;
	    
	    @FXML
	    private ComboBox<String> problemTypeChoiceBox;
	    
	    @FXML
	    private CheckBox semicolonCheckBox;
	    
	    @FXML
	    private CheckBox categoryCheckBox;
	    
	    private Window primaryStage;
	    
		private int resolution = 70;
		private String[] class_names;
	   

	   	    
	    @FXML
	    void handleBinarizerDimension(MouseEvent event) {

	    	bit_size = (int)binarizerSlider.getValue();
	    	binarizerText.setText(""+bit_size);
	    }

	   
	    
	    
	    
	    @FXML
	    void handleBinaryTransform(ActionEvent event) throws Exception {

	    	if(dataInterface != null) {
	    		
	    		if(problemTypeChoiceBox.getSelectionModel().getSelectedIndex() == 1) { //regression
	    			
	    			feature_names = dataInterface.setFeatureNames();
	    			data = dataInterface.uploadRegressionData(feature_names);
	    			
	    			class_names = new String[] {"Regression"};
	    		}
	    		else if(problemTypeChoiceBox.getSelectionModel().getSelectedIndex() == 2) { //encodeDecode
	    			
	    			feature_names = dataInterface.setFeatureNames();
	    				    			
	    			data = dataInterface.getContinuousMixedData(feature_names, resolution);
	    			
	    			float min = dataInterface.getMin();
	    			float delta = (dataInterface.getMax() - dataInterface.getMin())/(float)resolution;
	    			float val = min;
	    			class_names = new String[resolution];
	    			
	    			for(int i = 0; i < resolution; i++) {
	    				class_names[i] = "[" + val + ", " + (val + delta) + "]";
	    				val += delta;
	    			}
	    		}
	    		else {
	    			
	    			feature_names = dataInterface.setFeatureNames();
		    		data = dataInterface.getTwoClassData(feature_names, ""); 	
		    		
		    		List<Integer> listclass = dataInterface.getMy_classes();
		    		class_names = new String[listclass.size()];
		    		for(int i = 0; i < class_names.length; i++) {
		    			class_names[i] = "Class " + listclass.get(i);
		    		}
		    		
	    		}
	    		
	    		bin = new Binarizer(bit_size);		
	    		bin.fit(data.getData());	    	
	    		
	    		dataPipelineButton.setDisable(false);
	    	}
	    }

	    @FXML
	    void handleFileOpen(ActionEvent event) throws FileNotFoundException {

	    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("csv files (*.csv)", "*.csv");
			fileChooser.getExtensionFilters().add(extFilter);
			fileChooser.setTitle("Open csv File");
			List<File> filelist = fileChooser.showOpenMultipleDialog(primaryStage);		
			
			if(semicolonCheckBox.isSelected()) {
				dataInterface = new DataInterface(filelist.get(0).getAbsolutePath(), ';');
			}
			else {
				dataInterface = new DataInterface(filelist.get(0).getAbsolutePath());
			}
			
			dataInterface.instantiateDataFeed();
			fileNameText.setText(filelist.get(0).getAbsolutePath());
			
			binaryTransformButton.setDisable(false);
			getHeadersButton.setDisable(false);
			
	    }

	    @FXML
	    void handleGetHeaders(ActionEvent event) throws IOException {

	    	if(data != null) {    		
	    		
	    		feature_names = dataInterface.setFeatureNames();
	    		
	    		
	    		ObservableList<String> items = FXCollections.observableArrayList(feature_names);
	    		
	    		listHeaders.setItems(items);
	    		listHeaders.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);



	    		listHeaders.getSelectionModel().selectedItemProperty()
	    		
	    			    .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
	    			     ObservableList<String> selectedItems = listHeaders.getSelectionModel().getSelectedItems();

	    			     StringBuilder builder = new StringBuilder("");

	    			     for (String name : selectedItems) {
	    			      builder.append(name + "\n");
	    			     }

	    			     headersTextArea.setText(builder.toString());

	    			    });	
	    	}
	    	
	    }
	    
	    
	    @FXML
	    void handleBuildDataPipeline(ActionEvent event) throws Exception {
	    	
	    	String[] selected = headersTextArea.getText().split("[\n]+");
	    	
	    	if(selected.length > 0) {
	    	
	    		feature_names = selected;
	    		
		    	for(int i = 0; i < selected.length; i++) {
		    		System.out.println(i + " " + feature_names[i]);
		    	}
		    	
		    	dataInterface.instantiateDataFeed();
		    	
		    	String[] headers = dataInterface.setFeatureNames();
		    	
		    	if(problemTypeChoiceBox.getSelectionModel().getSelectedIndex() == 1) {
		    		data = dataInterface.uploadRegressionData(feature_names);
		    	}
		    	else if(problemTypeChoiceBox.getSelectionModel().getSelectedIndex() == 2) { //encodeDecode
	    			data = dataInterface.getContinuousMixedData(feature_names, resolution);
		    	}
		    	else {
		    		data = dataInterface.getTwoClassData(feature_names, ""); 	
		    	}
		    	
	    		bin = new Binarizer(bit_size);		
	    		bin.fit(data.getData());	
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
			return dataInterface.getTwoClassData(feature_names,"");
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

		public boolean isRegression() {
			return problemTypeChoiceBox.getSelectionModel().getSelectedIndex() == 1;
		}


		public float decodeClass(int num) {
			return dataInterface.decodeClass(num);
		}



		public String[] getClassNames() {
			return class_names;
		}



		public int getTargetResolution() {
			return dataInterface.getTargetResolution();
		}


		public boolean isContinuousEncoder() {
			return problemTypeChoiceBox.getSelectionModel().getSelectedIndex() == 2;
		}


}

	
	

