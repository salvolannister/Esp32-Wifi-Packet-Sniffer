package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewSettingController implements Initializable {

    @FXML private Button back;
    @FXML private Button RoomButton;
    @FXML private Button EspButton;

    public void newRoom(MouseEvent mouseEvent){
        Parent roomPage;
        try {
            roomPage = FXMLLoader.load(getClass().getResource("NewRoom.fxml"));
            Scene newRoomPageScene = new Scene(roomPage);
            Stage appStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            appStage.hide();
            appStage.setScene(newRoomPageScene);
            appStage.show();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public void newConfiguration(MouseEvent mouseEvent){

        Parent configurationPage;
        try {
            configurationPage = FXMLLoader.load(getClass().getResource("Configuration.fxml"));
            Scene configurationPageScene = new Scene(configurationPage);
            Stage appStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            appStage.hide();
            appStage.setScene(configurationPageScene);
            appStage.show();

        } catch (IOException e) {

            e.printStackTrace();

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
