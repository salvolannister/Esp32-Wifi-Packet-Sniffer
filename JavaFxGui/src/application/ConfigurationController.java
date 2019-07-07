package application;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;


public class ConfigurationController implements Initializable{
	
	
	@FXML private Button OKButton;
	@FXML private Spinner<Integer> SpinnerBox; 
	@FXML private HBox hbox;
	@FXML private GridPane gp;
	
	ObservableList<TextField> macTextField = FXCollections.observableArrayList();
    ObservableList<TextField> xTextField = FXCollections.observableArrayList();
	ObservableList<TextField> yTextField = FXCollections.observableArrayList();
	private int nEsp = 3;
//	private List<TextField> espTextField;
	GridPane espButton ;
	
	
	
	private void addEspButton(int x, int y) throws IOException {
//		GridPane eb = (GridPane) FXMLLoader.load(getClass().getResource("Esp_button.fxml"));
		Label macL = new Label("MAC:");
		Label xL = new Label("X:");
		Label yL = new Label("Y: ");
		
		TextField mac = new TextField();
		TextField X = new TextField();
		TextField Y = new TextField();
		
		macTextField.add(mac);
		xTextField.add(X);
		yTextField.add(Y);
		System.out.println("-----------");
		System.out.println("In addEsp button size of Xarray is: " + xTextField.size());

		
//		ChangeListener<Object> listener = (obs, oldValue, newValue) -> 
//	    printField(mac.getText(),X.getText(), Y.getText());
//	    mac.textProperty().addListener(listener);
//	    X.textProperty().addListener(listener);
//	    Y.textProperty().addListener(listener);
		
		
		
		if(x != 0 && y != 0)
			gp.addRow(1);
		
		 	gp.add(macL, x, y);
			gp.add(mac, x+1, y);
			gp.add(xL, x+2, y);
			gp.add(X, x+3,y );
			gp.add(yL, x+4, y);
			gp.add(Y, x+5, y);
			
			
	}

private static void printField(String mac, String X, String Y) {
//		System.out.println("You inserted this: "+"Mac "+ text+ "X "+ text2 +" y "+text3);
	System.out.println("You inserted this: "+"Mac "+ mac +" X:"+ X +" Y: "+Y);
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

	static void deleteRow(GridPane grid, final int row) {
	    Set<Node> deleteNodes = new HashSet<>();
	    for (Node child : grid.getChildren()) {
	        // get index from child
	        Integer rowIndex = GridPane.getRowIndex(child);

	        // handle null values for index=0
	        int r = rowIndex == null ? 0 : rowIndex;

	        if (r > row) {
	            // decrement rows for rows after the deleted row
	            GridPane.setRowIndex(child, r-1);
	        } else if (r == row) {
	            // collect matching rows for deletion
	            deleteNodes.add(child);
	        }
	    }

	    // remove nodes from row
	    
	   
	    grid.getChildren().removeAll(deleteNodes);
	    
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
					
//					deleteRow(gp,2);
	
			
		
//			SpinnerBox.setValueFactory(valueFactory);
		
	}
	
	public void getSpinnerValue(Event event) throws IOException {
	int oldValue = nEsp;
	int diff = 0, i = 0;
	 nEsp = SpinnerBox.getValue();
	 diff = oldValue - nEsp;
	 
	 if(diff > 0) {
		 for(i=oldValue; i>nEsp ;i--) {
			System.out.println("Size of Xarray is: " + xTextField.size());
			 deleteRow(gp,i-1);
			 System.out.println(" After deleting row: " + xTextField.size());
			 xTextField.remove(i-1);
			 System.out.println(" After deleting a node " + xTextField.size());
			 printList(xTextField);
		 }
	 }else {
		 diff*=-1;
		 System.out.println("diff is "+diff+" old value is "+ oldValue);
		 for(i=oldValue; i<nEsp ;i++) {
			addEspButton(0,i);
			
		 }
	 }
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
	
	private void printList(ObservableList<TextField> o) {
		 for(TextField e : o) {
			 System.out.println("this is my text X= "+ e.getText());
		 }
	}

	public void OKButtontEvent (Event event) {
		Parent HomePage;
		boolean OK = false;
		 for(TextField e : xTextField) {
			 System.out.println("this is my text X= "+ e.getText());
			 if(e.getText() == null || e.getText().trim().isEmpty()) {
				 Alert fail= new Alert(AlertType.INFORMATION);
			        fail.setHeaderText("failure");
			        fail.setContentText("You must fullfill the X fields");
			        fail.showAndWait();
				 OK = false;
				 return;
			 }
		 }
			OK = true;
			System.out.println("WOWWWWW");
	     if(OK==true) {
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
     
	}

    public void back(MouseEvent mouseEvent) {

		Parent configurationPage;
		try {
			configurationPage = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene configurationPageScene = new Scene(configurationPage);
			Stage appStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
			appStage.hide();
			appStage.setScene(configurationPageScene);
			appStage.show();

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

