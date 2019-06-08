package application;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class ConfigurationController implements Initializable{
	
	
	@FXML private Button OKButton;
	@FXML private Spinner<Integer> SpinnerBox; 
	@FXML private HBox hbox;
	@FXML private GridPane gp;
	
	ObservableList<TextField> macTextField = FXCollections.observableArrayList();
	ObservableList<TextField> xTextField = FXCollections.observableArrayList();
	ObservableList<TextField> yTextField = FXCollections.observableArrayList();
	private int nEsp = 0;
//	private List<TextField> espTextField;
	GridPane espButton ;
	
	
	
	private void addEspButton(int x, int y) throws IOException {
		GridPane eb = (GridPane) FXMLLoader.load(getClass().getResource("Esp_button.fxml"));
		if(x == 0 && y == 0)
			gp.add(eb, x, y);//x is column index and 0 is row index
		else {
			gp.addRow(1);
			gp.add(eb, x, y);
		}
		/* children begins with index 1 not 0)*/
		Node result = getNodeFromGridPane(eb,5,0);
		
		TextField mac = (TextField) getNodeFromGridPane(eb,1,0);
		TextField X = (TextField) getNodeFromGridPane(eb,3,0);
		TextField Y = (TextField) getNodeFromGridPane(eb,5,0);
//	    macTextField.add(nuovo);
//		
		
		ChangeListener<Object> listener = (MAC, Xi, Yi) -> 
        printField(mac.getText(), X.getText(), Y.getText()); 
        
       if(X== null) System.out.println("X is null");
       X.setText("argo");
	}

private static void printField(String text, String text2, String text3) {
		System.out.println("You inserted this: "+"Mac "+ text+ "X "+ text2 +" y "+text3);
		
	}

	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
	    for (Node node : gridPane.getChildren()) {
	    	
	    	Integer columnIndex = GridPane.getColumnIndex(node);
	    	Integer rowIndex =  GridPane.getRowIndex(node);
	    	if(columnIndex != null && rowIndex != null) {
	        if (columnIndex == col && rowIndex == row) {
	        	
	            return node;
	        }
	    	}
	    }
	    return null;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		SpinnerValueFactory<Integer> valueFactory = 
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(3,10,3);
		
	
			valueFactory.setValue(nEsp);
			SpinnerBox.setValueFactory(valueFactory);
			
				
				try {
					
					
					for(int i = 0;i<3;i++) {
					addEspButton(0,i);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	
			
		
//			SpinnerBox.setValueFactory(valueFactory);
		
	}
	
	public void getSpinnerValue(Event event) {
	 nEsp = SpinnerBox.getValue();
//	 TextField newField = new TextField();
//	 HBox hb =(HBox) hbox.getChildren().get(1);
//	 double X =hb.getChildren().get(1).getLayoutX();
//	 newField.setLayoutX(X);
//	 hb.getChildren().add(newField);
//	 GridPane temp;
//	try {
//		temp = (GridPane) FXMLLoader.load(getClass().getResource("Esp_button.fxml"));
//		gp.addRow(1+ nEsp);
//		gp.add(temp, 0, 0);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
		
	 System.out.println("nEsp: "+nEsp);
	}
	
	public void OKButtontEvent (Event event) {
		Parent HomePage;
		try {
			HomePage = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene HomePageScene = new Scene (HomePage);
			Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
			appStage.hide();
			appStage.setScene(HomePageScene);
			appStage.show();
			/*code to send information to the database*/
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
	
//	public void poundIncrementButtonPressed(ActionEvent event) {
//	    System.out.println("You tryed to write");
//	    TextField tf = (TextField) event.getSource();
//	    // identifies which plate is to be incremented
//	    int plate = poundIncrementButtons.indexOf(button);
//	    incrementDecrementPlate(0, 0, plate);
//	}
}

