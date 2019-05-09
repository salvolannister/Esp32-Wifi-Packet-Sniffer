package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class ConfigurationController {
	
	@FXML
	private TextField HeightBox;
	
	@FXML
	private TextField WidthBox;
	
	@FXML
	private Button OKButton;
	
	@FXML
	private Spinner SpinnerBox; 
	
	private float Height;
	private float Width ;
	
	public void setHeight() {
		String h = new String(HeightBox.getText());
		System.out.println("altezza:"+ h);
		Height = Float.parseFloat(h);
	}
	
	public void setWidth() {
		String w = new String(HeightBox.getText());
		System.out.println("larghezza:"+ w);
		Height = Float.parseFloat(w);
	}
	


}
