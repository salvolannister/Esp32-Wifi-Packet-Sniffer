package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class ButtonController implements Initializable{

	@FXML private TextField mac;
	@FXML private GridPane gp;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

	public TextField getTextField() {
		return mac;
	}
	public GridPane getGridPane() {
		return gp;
	}
}
