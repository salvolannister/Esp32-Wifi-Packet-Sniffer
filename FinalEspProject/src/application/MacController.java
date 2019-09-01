package application;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import DB.DBUtil;
import DB.QueryPosition;
import DB.QueryRoom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class MacController implements Initializable{
    //	it will tell that the controller is connected to FXML file
    @FXML private Button SearchButton;
    @FXML private TextArea AreaInfo;
    @FXML private LocalDateTimeTextField DataF;
    @FXML private LocalDateTimeTextField DataI;
    @FXML private ListView Lista;
    @FXML private ComboBox<String> ComboboxRoom;


    private ObservableList<Button> MacList = FXCollections.observableArrayList();
    private Map<String, Long> risultato= new HashMap<>();

    private ObservableList<String> Roomlist = FXCollections.observableArrayList();
    private List<String> ReadList = new ArrayList<>();


    /*Legge la stanza selezionata, le date e restituisce la frequenza di MAC per ogni stanza */
    public void search(MouseEvent mouseEvent) {

        try {
            AreaInfo.setText("");
            Lista.getItems().remove(0, Lista.getItems().size());

            Timestamp inizio = Timestamp.valueOf(DataI.getLocalDateTime());
            Timestamp fine = Timestamp.valueOf(DataF.getLocalDateTime());

            if(inizio.compareTo(fine)>0){
                AreaInfo.appendText("La data d'inizio deve precedere quella di fine\n");
                ConfigurationController.showAlert("The starting date  must precede the ending one",true);
                return;
            }

            /*Ricavo la stanza selezionata*/
            String roomselected = ComboboxRoom.getValue();

            if(roomselected != null) {
                DBUtil db = new DBUtil();
                if (!db.openConnection("database.db")) {
                    System.err.println("Errore di Connessione al DB. Impossibile Continuare");
                    System.exit(-1);
                }

                QueryPosition p = new QueryPosition(db.getConn());

                try {
                    risultato = p.showMacPerRoom(String.valueOf(inizio.getTime()), String.valueOf(fine.getTime()), roomselected);
                    if (risultato != null) {
                        //System.out.println("tutto ok");
                        for (String s : risultato.keySet()) {
                            addMacButton(s, DataI.getText(), DataF.getText());
                        }
                    } else {
                        AreaInfo.appendText("Nessun MAC rilevato per la stanza " + roomselected + "\n" + "Nell'intervallo di tempo seguente:\n" + "TS Inizio: " + inizio + "\n" + "TS Fine: " + fine);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                db.closeConnection();

                DataI.setText("");
                DataI.setLocalDateTime(null);
                DataF.setText("");
                DataF.setLocalDateTime(null);
                //queste non funzionano perchè il combobox l ho messo non editabile
                //ComboboxRoom.setPromptText("");
                //ComboboxRoom.setPromptText(ComboboxRoom.getPromptText());
                return;
            }else{
                AreaInfo.appendText("Selezionare la stanza\n");
                ConfigurationController.showAlert("Select a room",true);
                return;
            }
        }catch (NullPointerException n){
            AreaInfo.appendText("Inserire data e ora di inizio e fine");
            ConfigurationController.showAlert("Insert a starting/ending date and time",true);
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

    /*Inizializza i valori di selezione per la stanza*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            DBUtil db = new DBUtil();
            if (!db.openConnection("database.db")) {
                System.err.println("Errore di Connessione al DB. Impossibile Continuare");
                System.exit(-1);
            }

            QueryRoom p = new QueryRoom(db.getConn());

            try {
                ReadList = p.getRoomName();

                if (ReadList != null) {
                    for (String room : ReadList) {
                        Roomlist.add(room);
                        //System.out.println(room);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            db.closeConnection();
            ComboboxRoom.setItems(Roomlist);
        }catch(NullPointerException n){
            return;
        }
    }




}
