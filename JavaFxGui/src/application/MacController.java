package application;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import DB.QueryFake;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import DB.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalDateTimeTextField;

public class MacController {
    //	it will tell that the controller is connected to FXML file
    @FXML private Button SearchButton;
    @FXML private TextArea AreaInfo;
    @FXML private LocalDateTimeTextField DataF;
    @FXML private LocalDateTimeTextField DataI;
    @FXML private ListView Lista;

    private ObservableList<Button> MacList = FXCollections.observableArrayList();
    private Map<String, Long> risultato= new HashMap<>();



    public void search(MouseEvent mouseEvent) {

        try {


            AreaInfo.setText("");
            Lista.getItems().remove(0, Lista.getItems().size());

            Timestamp inizio = Timestamp.valueOf(DataI.getLocalDateTime());
            Timestamp fine = Timestamp.valueOf(DataF.getLocalDateTime());

            DBUtil db=new DBUtil();
            if(!db.openConnection("fake_db.db")){
                System.err.println("Errore di Connessione al DB. Impossibile Continuare");
                System.exit(-1);
            }
            QueryFake p=new QueryFake(db.getConn());

            try {
                risultato=p.showMac(String.valueOf(inizio.getTime()), String.valueOf(fine.getTime()));
                if(risultato!=null){
                    //System.out.println("tutto ok");
                    for (String s: risultato.keySet()){
                        addMacButton(s, DataI.getText(), DataF.getText());
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            db.closeConnection();
            DataI.setText("");
            DataI.setLocalDateTime(null);
            DataF.setText("");
            DataF.setLocalDateTime(null);
            return;
        }catch (NullPointerException n){
            AreaInfo.setText("inserire data e ora di inizio e fine");
            //DataF.text
            return;
        }


    }




    private void addMacButton(String s, String dataI,  String dataF) {

        Button mac=new Button(s);
        mac.setPrefWidth(260);
        mac.setOnMouseClicked(new InfoEvent(risultato.get(mac.getText()), mac.getText(),AreaInfo, dataI, dataF));
        MacList.add(mac);
        System.out.println("-----------");
        System.out.println("ho aggiunto un bottone " + MacList.size());

        Lista.setItems(MacList);
    }

    @FXML
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
}
