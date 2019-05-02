import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//w w  w.j  ava 2s  .  c  o m
public class Hello_World extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    

   
	Parent root;
	
	root = FXMLLoader.load(getClass().getResource("Main.fxml"));
	  	
    Scene scene = new Scene(root);

    primaryStage.setTitle("Esp32 Tracker");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}