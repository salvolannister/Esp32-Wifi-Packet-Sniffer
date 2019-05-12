package application;

import java.io.IOException;
import java.net.URL;
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
import javafx.stage.Stage;


public class ConfigurationController implements Initializable{
	
	@FXML private TextField HeightBox;
	@FXML private TextField WidthBox;
	@FXML private Button OKButton;
	@FXML private Spinner<Integer> SpinnerBox; 
	
	private float Height = 0;
	private float Width = 0 ;
	private int nEsp = 0;
	@FXML
	private void setHeight(Event event) {
		
		try{
			String h = new String(HeightBox.getText());
			System.out.println("altezza:"+ h);
			Height = Float.parseFloat(h);
		}catch (Exception e) {
			
		}
		
	}
	
	
	public void setWidth(Event event) {
		String w = new String(WidthBox.getText());
		System.out.println("larghezza:"+ w);
		Height = Float.parseFloat(w);
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		HeightBox.setEditable(true);
		SpinnerValueFactory<Integer> valueFactory = 
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(2,10,2);
		
		if(Height != 0 && Width != 0 && nEsp!= 0) {
			String h = new String( Float.toString(Height));
			String w = new String(Float.toString(Width));
			HeightBox.setText(h);
			WidthBox.setText(w);
			valueFactory.setValue(nEsp);
			SpinnerBox.setValueFactory(valueFactory);
		}else {
			SpinnerBox.setValueFactory(valueFactory);
		}
	}
	
	public void getSpinnerValue(Event event) {
	 nEsp = SpinnerBox.getValue();
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

