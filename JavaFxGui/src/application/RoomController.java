package application;

import DB.DBUtil;
import DB.QueryConfiguration;
import DB.QueryFake;
import DB.QueryRoom;
import DTO.Polo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalDateTimeTextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class RoomController implements Initializable {

    @FXML private Button ahead;
    @FXML private Button behind;
    @FXML private Button SearchButton;
    @FXML private Pane graph_container;
    @FXML private LocalDateTimeTextField DataI;
    @FXML private Slider nav;
    @FXML private ComboBox<String> roomCB;
    @FXML private ComboBox<String> configCB;
    @FXML private Button start;
    @FXML private Button stop;
    private ScatterChart<Number, Number> grafico;


    private ObservableList<String> roomList = FXCollections.observableArrayList();
    private List<String> readList = new ArrayList<>();
    private ObservableList<String> configList = FXCollections.observableArrayList();
    private  Timestamp inizio;
    private boolean stopCliked = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final NumberAxis xAxis = new NumberAxis(0, 10, 0.01);
        final NumberAxis yAxis = new NumberAxis(0, 10, 0.01);
        xAxis.setLabel("posX");
        yAxis.setLabel("posY");
        grafico=new ScatterChart<Number, Number>(xAxis, yAxis);
        grafico.setTitle("Posizione");
        graph_container.getChildren().add(grafico); //aggiungo un grafico vuoto

        /* setting content in Room and Configuration button*/
        try {
            DBUtil db = new DBUtil();
            if (!db.openConnection("database.db")) {
                System.err.println("Errore di Connessione al DB. Impossibile Continuare");
                System.exit(-1);
            }

            QueryRoom p = new QueryRoom(db.getConn());
            QueryConfiguration qC = new QueryConfiguration(db.getConn());

            try {
             readList = p.getRoomName();

                if (readList != null) {
                    for (String room : readList) {
                        roomList.add(room);
                        System.out.println(room);
                    }
                }
                List<String> readListConf = qC.getConfNames();
                if (readListConf != null) {
                    for (String config : readListConf) {
                        configList.add(config);
                       // System.out.println(config);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            db.closeConnection();
            configCB.setItems(configList);
            roomCB.setItems(roomList);
        }catch(NullPointerException n){
            return;
        }
    }

    public void search(MouseEvent mouseEvent) {
        String roomName = roomCB.getValue();
        String confName = configCB.getValue();
        Map<String, Polo> risultato= new HashMap<>();
        nav.setValue(0);

        if(roomName == null){
            errorShower("You must select a Room first");
            return;
        }else if(confName == null){
            errorShower("You must select a Configuration first");
            return;
        }else if(DataI.getLocalDateTime() == null){
            errorShower("You must insert a date first");
            return;
        }

        try {
            /* Timestamp e' una classe per gestire il temo in millisecondi
             */
            LocalDateTime dataI = DataI.getLocalDateTime();
            inizio = Timestamp.valueOf(dataI);
            //Timestamp fine = Timestamp.valueOf(DataF.getLocalDateTime());

            DBUtil db=new DBUtil();

            if(!db.openConnection("prova.db")){
                System.err.println("Errore di Connessione al DB. Impossibile Continuare");
                System.exit(-1);
            }
            QueryFake p=new QueryFake(db.getConn());
            QueryRoom qR = new QueryRoom(db.getConn());
            QueryConfiguration qC = new QueryConfiguration(db.getConn());

            try {
                /*slider con il tempo*/
                ahead.setDisable(false);
                behind.setDisable(false);
                nav.setDisable(false);
                risultato=p.showPosition(String.valueOf(inizio.getTime()),roomName,confName);
                ArrayList<Float> roomDim = qR.getRoomDim(roomName);
                /* aggiusto gli assi in base
                alla dimensione della stanza */
                final NumberAxis xAxis = new NumberAxis(0, roomDim.get(0), 0.01);
                final NumberAxis yAxis = new NumberAxis(0, roomDim.get(1), 0.01);
                xAxis.setLabel("posX");
                yAxis.setLabel("posY");
                graph_container.getChildren().remove(grafico); //rimuovo il grafico vuoto
                grafico=new ScatterChart<Number, Number>(xAxis, yAxis);
                grafico.setTitle("Posizione");

                /*inserisco la
                configurazione */

                XYChart.Series series2 = new XYChart.Series();
                series2.setName("Esp");
                ArrayList<EspInfo> espInfos = qC.readConfiguration(confName);
                if(espInfos !=  null) {

                    for (EspInfo eI: espInfos) {
                        /*aggiunge i dati delle schedine al grafico
                        .getX() è un metodo di Polo
                         */
                        series2.getData().add(new XYChart.Data(eI.getX(), eI.getY()));

                    }

                    grafico.getData().add(series2);

                }else{
                    //TODO
                    return;
                }



                if(risultato!=null) {
                    //System.out.println("tutto ok");

                    graphAdd(risultato);


                }
                /*in ogni caso mostra
                 la posizione dei dispositivi */
                graph_container.getChildren().add(grafico);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            db.closeConnection();
           // DataI.setText("");
           // DataI.setLocalDateTime(null);
            return;
        }catch (NullPointerException n){
            //AreaInfo.setText("inserire data e ora di inizio e fine");
            //DataF.text
            return;
        }


    }

    public void timeManager(MouseEvent mouseEvent) throws SQLException {
        Button clicked= (Button) mouseEvent.getSource();
        String name = clicked.getText();
        if(name.equals(">>")){
            System.out.println("You clicked >>");
            nav.setValue(0);
            long minutInMillisec = 60000*10;
            inizio = new Timestamp(inizio.getTime() + minutInMillisec);
            onSliderClick(mouseEvent);
            DataI.setLocalDateTime(inizio.toLocalDateTime());

            //TODO put the time ahead in DAtaI
        }else if(name.equals("<<")){
            //TODO put the time behind in
            System.out.println("You clicked <<");
            nav.setValue(0);
            long minutInMillisec = 60000*10;
            inizio = new Timestamp(inizio.getTime() - minutInMillisec);
            onSliderClick(mouseEvent);
            DataI.setLocalDateTime(inizio.toLocalDateTime());
        }
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

    public void onSliderClick(MouseEvent mouseEvent) throws SQLException {
        double value = nav.getValue();

        long lValue = (long) value;
        long minutInMillisec = 60000*lValue;
        System.out.println("valore nav: "+lValue+ " valore in Millisec :"+minutInMillisec);
        String minutField = String.valueOf(minutInMillisec);
        Timestamp later = new Timestamp(inizio.getTime() + minutInMillisec);
        DataI.setLocalDateTime(later.toLocalDateTime());
        /*   leggere i dati dal DB
            rimuovere i dati vecchi
            mettere i nuovi
         */
        DBUtil db=new DBUtil();

        if(!db.openConnection("prova.db")){
            System.err.println("Errore di Connessione al DB. Impossibile Continuare");
            System.exit(-1);
        }
        QueryFake p=new QueryFake(db.getConn());
        String roomName = roomCB.getValue();
        String confName = configCB.getValue();
        Map<String, Polo> risultato = p.showPosition(String.valueOf(later.getTime()), roomName, confName);
        System.out.println("size: "+grafico.getData().size());
        if(grafico.getData().size() == 2){
            grafico.getData().remove(1);

        };

        if(risultato!=null) {
            //System.out.println("tutto ok");
            graphAdd(risultato);

        }

    }

    private void graphAdd(Map<String, Polo> risultato){
                /* add a collection of points
                    with a determinated color*/
        XYChart.Series series1 =new XYChart.Series<>();
        series1.setName("Device");
        /*aggiunge la posizione delle schedine*/
        for (String s : risultato.keySet()) {
                        /*aggiunge i dati delle schedine al grafico
                        .getX() è un metodo di Polo
                         */
            series1.getData().add(new XYChart.Data(risultato.get(s).getX(), risultato.get(s).getY()));

        }

        grafico.getData().add(series1);

    }

    public void onStartClick(MouseEvent mouseEvent){
        ahead.setDisable(true);
        behind.setDisable(true);
        start.setDisable(true);
        roomCB.setDisable(true);
        configCB.setDisable(true);
        stop.setDisable(false);
        DataI.setDisable(true);
        SearchButton.setDisable(true);
        // TODO put the start function you mentioned end add a new graph

        startService.reset();
        startService.start();
    }

    /* crea un thread in modo tale da non
    fermare l'interfaccia
     */
    Service<Void>  startService = new Service<Void>() {

        @Override
        protected Task<Void> createTask() {

            return new Task<Void>(){

                @Override
                protected Void call() throws Exception {

                    while(!isCancelled()) {
                        int randomInt = (int )(Math.random() * 37 + 1);
                        System.out.println(randomInt);
                        /* qui mettere il codice che dovrebbe fare start*/
                    }
                    return null;
                }
            };
        }
    };

    public void onStopCLick(MouseEvent mouseEvent){
        start.setDisable(false);
        roomCB.setDisable(false);
        configCB.setDisable(false);
        stop.setDisable(true);
        DataI.setDisable(false);
        SearchButton.setDisable(false);
        stopCliked = true;
        startService.cancel();
    }

    public void printValue(MouseEvent mouseEvent) {
        System.out.println(nav.getValue());

    }

    public void errorShower(String text){

            Alert fail= new Alert(Alert.AlertType.INFORMATION);
            fail.setHeaderText("failure");
            fail.setContentText(text);
            fail.showAndWait();

    }

    private boolean checkDataUpper(Timestamp newData){
        LocalDateTime actual = DataI.getLocalDateTime();
       return true;
    }
}
