package application;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
	

	private int nEsp = 0;
	private List<TextField> espTextField;
	GridPane espButton ;
	
	
	
	private void addEspButton(int x, int y) throws IOException {
		GridPane eb = (GridPane) FXMLLoader.load(getClass().getResource("Esp_button.fxml"));
		if(x == 0 && y == 0)
			gp.add(eb, x, y);//x is column index and 0 is row index
		else {
			gp.addRow(1);
			gp.add(eb, x, y);
		}
		
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
		
	 System.out.println("nEsp "+nEsp);
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
}

