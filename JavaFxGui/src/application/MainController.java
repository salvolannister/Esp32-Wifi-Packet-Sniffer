package application;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

	@FXML
	public void openMacFreq(ActionEvent event) {
		Parent FreqPage;
		try {
			FreqPage = FXMLLoader.load(getClass().getResource("MACfreq.fxml"));
			Scene FreqPageScene = new Scene(FreqPage);
			Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			appStage.hide();
			appStage.setScene(FreqPageScene);
			appStage.show();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
//	it will tell that the controller is connected to FXML file
	@FXML
		
		public void openConfiguration(ActionEvent event) {
		Parent configurationPage;
		try {
			configurationPage = FXMLLoader.load(getClass().getResource("Configuration.fxml"));
			Scene configurationPageScene = new Scene (configurationPage);
			Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
			appStage.hide();
			appStage.setScene(configurationPageScene);
			appStage.show();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	
		}

		public void startServer(ActionEvent event){
			System.out.println("cisono");
			try {
				File f=new File(".");
				f.getAbsolutePath();
				String jar =f.getAbsolutePath()+"//Serverino.jar";
				Process process = Runtime.getRuntime().exec("java -jar"+ jar);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
}
