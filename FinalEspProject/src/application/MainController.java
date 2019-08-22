package application;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
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
		
		public void openSetting(ActionEvent event) {
		Parent settingPage;
		try {
			settingPage = FXMLLoader.load(getClass().getResource("NewSetting.fxml"));
			Scene settingPageScene = new Scene (settingPage);
			Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
			appStage.hide();
			appStage.setScene(settingPageScene);
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

    public void openRoom(ActionEvent mouseEvent) {
		Parent RoomPage;
		try {
			RoomPage = FXMLLoader.load(getClass().getResource("RoomMAC.fxml"));
			Scene RoomPageScene = new Scene (RoomPage);
			Stage appStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
			appStage.hide();
			appStage.setScene(RoomPageScene);
			appStage.show();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public void openTimeDiag(ActionEvent mouseEvent) {
		Parent TimePage;
		try {
			TimePage = FXMLLoader.load(getClass().getResource("TimeMAC.fxml"));

			Scene TimePageScene = new Scene (TimePage);
			Stage appStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
			appStage.hide();
			appStage.setScene(TimePageScene);
			appStage.show();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
