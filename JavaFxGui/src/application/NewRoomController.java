package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewRoomController implements Initializable {
 @FXML TextField x;
 @FXML TextField y;
 @FXML TextField name;

     public void createEvent(MouseEvent mouseEvent ){
         if(x.getText() == null || y.getText() == null || name.getText() == null){
             /*oppure un controllo sui dati*/
             // error
         }

     }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void back(MouseEvent mouseEvent) {


        Parent settingPage;
        try {
            settingPage = FXMLLoader.load(getClass().getResource("NewSetting.fxml"));
            Scene configurationPageScene = new Scene(settingPage);
            Stage appStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            appStage.hide();
            appStage.setScene(configurationPageScene);
            appStage.show();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}
