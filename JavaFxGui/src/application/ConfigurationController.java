package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ConfigurationController implements Initializable{
	
	@FXML private TextField HeightBox;
	@FXML private TextField WidthBox;
	@FXML private Button OKButton;
	@FXML private Spinner<Integer> SpinnerBox; 
	
	private float Height;
	private float Width ;
	private int nEsp;
	@FXML
	private void setHeight() {
		
		try{
			String h = new String(HeightBox.getText());
			System.out.println("altezza:"+ h);
			Height = Float.parseFloat(h);
		}catch (Exception e) {
			
		}
		
	}
	
	
	public void setWidth() {
		String w = new String(WidthBox.getText());
		System.out.println("larghezza:"+ w);
		Height = Float.parseFloat(w);
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		HeightBox.setText("0");
//		SpinnerBox.setValueFactory(value);
		HeightBox.setEditable(true);
		SpinnerValueFactory<Integer> valueFactory = 
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(2,10,2);
		SpinnerBox.setValueFactory(valueFactory);
		
	}
	
	public void getSpinnerValue() {
	 nEsp = SpinnerBox.getValue();
	 System.out.println("nEsp "+nEsp);
	}
	
	}


