package application;

import java.io.Console;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {
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
			Runtime runTime = Runtime.getRuntime();
			try {
				Process process = runTime.exec("java -classpath /home/umb/Scrivania/PROGRAMMAZIONEDISISTEMA/progetto/MUDS/server/prova socket/out/production/Serverino:/home/umb/Scrivania/PROGRAMMAZIONEDISISTEMA/progetto/MUDS/server/prova socket/sqlite-jdbc-3.7.2.jar EchoServer");
			//				Process process = Runtime.getRuntime().exec("java -jar /home/umb/Scrivania/PROGRAMMAZIONEDISISTEMA/progetto/MUDS/JavaFxGui/src/Serverino.jar");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
}
