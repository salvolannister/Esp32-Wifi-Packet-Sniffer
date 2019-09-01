package application;

import DB.DBUtil;
import DB.QueryRoom;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NewRoomController implements Initializable {
 @FXML TextField x;
 @FXML TextField y;
 @FXML TextField name;

     public void createEvent(MouseEvent mouseEvent ) throws SQLException {
         float X = 0;
         float Y = 0;

        if( !( checkFieldXY(x)) || !(checkFieldXY(y))) {

            return;
        }
         if(name.getText().trim().isEmpty()){
             Alert fail= new Alert(Alert.AlertType.INFORMATION);
             fail.setHeaderText("failure");
             fail.setContentText("You didn't insert a name");
             fail.showAndWait();
             return;
         }else {
             DBUtil db = new DBUtil();
             if (!db.openConnection("database.db")) {
                 System.err.println("Errore di Connessione al DB. Impossibile Continuare");
                 System.exit(-1);
             }
             QueryRoom qR = new QueryRoom(db.getConn());
             String n = name.getText();

             if (qR.checkRoomExistence(n)) {
                 ConfigurationController.showAlert("Room name is already used", true);
                 db.closeConnection();
                 return;
             }
             try {
                 X = Float.parseFloat(x.getText());
                 Y = Float.parseFloat(y.getText());
                 qR.addRoom(n, X, Y);
                 Parent settingPage;
                 db.closeConnection();

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
             catch (NumberFormatException e){
                 ConfigurationController.showAlert("Insert numeric value", true);
                 db.closeConnection();
                 return;
             }
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

    private static boolean checkFieldXY( TextField o) {

        if(o.getText() == null || o.getText().trim().isEmpty()) {
            ConfigurationController.showAlert("You must fullfill the X and Y fields", true);
            return false;
        }else {


        }
        return true;

    }
}
