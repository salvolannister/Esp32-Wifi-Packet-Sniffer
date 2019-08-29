package application;

import DB.DBUtil;
import DB.QueryPosition;
import DB.QueryRoom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalDateTimeTextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;


public class TimeController implements Initializable {

    @FXML private Button SearchButton;
    @FXML private Pane graph_container;
    @FXML private LocalDateTimeTextField DataI;
    @FXML private ComboBox<String> ComboRoom;
    @FXML private TextArea AreaInfo;



    private LineChart<Number, Number> grafico;
    private Map<String, Long> risultato= new HashMap<>();
    private Map<String, Long> secres= new HashMap<>();
    private Map<String, Long> thirdres= new HashMap<>();
    private Map<String, Long> fourcres= new HashMap<>();

    private ObservableList<String> Roomlist = FXCollections.observableArrayList();
    private List<String> ReadList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /*inizializza il grafico*/
        final NumberAxis xAxis = new NumberAxis(0, 20, 5);
        //final ValueAxis<String> xAxis =new ValueAxis<String>();
        final NumberAxis yAxis = new NumberAxis(0, 50, 1);
        xAxis.setLabel("Time (min)");
        yAxis.setLabel("People Number");
        grafico=new LineChart<Number, Number>(xAxis, yAxis);
        grafico.setTitle("People Number Per Time");
        //grafico.setCreateSymbols(false); per togliere i cerchietti su ogni valore y
        grafico.setLegendVisible(false);
        grafico.setAnimated(false);
        graph_container.getChildren().add(grafico); //aggiungo un grafico vuoto

        /*Inizializza i valori di selezione per la stanza*/
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

            ComboRoom.setItems(Roomlist);
        }catch(NullPointerException n){
            return;
        }
    }


    public void search(MouseEvent mouseEvent) {

        try {
            grafico.getData().removeAll(grafico.getData()); //rimuove il vecchio risultato
            AreaInfo.setText("");

            Timestamp inizio = Timestamp.valueOf(DataI.getLocalDateTime());
            Timestamp afterfive = new Timestamp(inizio.getTime() + 5*60000);
            Timestamp afterten = new Timestamp(afterfive.getTime() + 5*60000);
            Timestamp afterfifteen = new Timestamp(afterten.getTime() + 5*60000);
            Timestamp aftertwenty = new Timestamp(afterfifteen.getTime() + 5*60000);

            /*ricavo la stanza selezionata*/
            String roomselected = ComboRoom.getValue();

            if(roomselected != null) {


                DBUtil db = new DBUtil();
                if (!db.openConnection("database.db")) {
                    System.err.println("Errore di Connessione al DB. Impossibile Continuare");
                    System.exit(-1);
                }
                QueryPosition p = new QueryPosition(db.getConn());

                try {
                    risultato = p.showNumberMacPerRoom(String.valueOf(inizio.getTime()), String.valueOf(afterfive.getTime()), roomselected);

                    if (risultato != null) {
                        //System.out.println("tutto ok");
                        XYChart.Series series = new XYChart.Series();
                        series.getData().add(new XYChart.Data(0, 0));

                        int numbermac = 0;
                        //ogni HahMap contiene: K=MAC, V=frequenza del MAC
                        /*ricavo il valore per ogni chiave dell'hashmap e incremento una variabile solo se
                         * se è = 5*/
                        for (Map.Entry<String, Long> s : risultato.entrySet()) {
                            long val = s.getValue();
                            System.out.println(val);
                            if (val == 5)
                                numbermac++;
                        }
                        System.out.println("numero MAC presenti in TUTTI i primi 5 min:" + numbermac);

                        series.getData().add(new XYChart.Data(5, numbermac));

                        secres = p.showMacPerRoom(String.valueOf(afterfive.getTime()), String.valueOf(afterten.getTime()), roomselected);
                        if (secres != null) {
                            numbermac = 0;
                            for (Map.Entry<String, Long> s : secres.entrySet()) {
                                long val = s.getValue();
                                System.out.println(val);
                                System.out.println(secres);
                                if (val == 5)
                                    numbermac++;
                            }
                            System.out.println("numero MAC presenti in TUTTI i primi 10 min:" + numbermac);
                            series.getData().add(new XYChart.Data(10, numbermac));


                            thirdres = p.showMacPerRoom(String.valueOf(afterten.getTime()), String.valueOf(afterfifteen.getTime()), roomselected);
                            if (thirdres != null) {
                                numbermac = 0;
                                for (Map.Entry<String, Long> s : thirdres.entrySet()) {
                                    long val = s.getValue();
                                    System.out.println(val);
                                    System.out.println(thirdres);
                                    if (val == 5)
                                        numbermac++;
                                }
                                System.out.println("numero MAC presenti in TUTTI i primi 15 min:" + numbermac);
                                series.getData().add(new XYChart.Data(15, numbermac));


                                fourcres = p.showMacPerRoom(String.valueOf(afterfifteen.getTime()), String.valueOf(aftertwenty.getTime()), roomselected);
                                if (fourcres != null) {
                                    numbermac = 0;
                                    for (Map.Entry<String, Long> s : fourcres.entrySet()) {
                                        long val = s.getValue();
                                        System.out.println(val);
                                        System.out.println(fourcres);
                                        if (val == 5)
                                            numbermac++;
                                    }
                                    System.out.println("numero MAC presenti in TUTTI i primi 20 min:" + numbermac);
                                    series.getData().add(new XYChart.Data(20, numbermac));
                                }
                            }
                        }
                        graph_container.getChildren().remove(grafico); //rimuovo il grafico vuoto
                        grafico.getData().add(series);
                        graph_container.getChildren().add(grafico);
                    }else{
                        AreaInfo.appendText("Nessun MAC rilevato per la stanza " + roomselected + "\n" + "A partire dalla data e ora seguenti:\n" + "TS Inizio: " + inizio);
                        XYChart.Series serie = new XYChart.Series();
                        serie.getData().add(new XYChart.Data(0, 0));
                        serie.getData().add(new XYChart.Data(5, 0));
                        serie.getData().add(new XYChart.Data(10, 0));
                        serie.getData().add(new XYChart.Data(15, 0));
                        serie.getData().add(new XYChart.Data(20, 0));
                        graph_container.getChildren().remove(grafico); //rimuovo il grafico vuoto
                        grafico.getData().add(serie);
                        graph_container.getChildren().add(grafico);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                db.closeConnection();
                DataI.setText("");
                DataI.setLocalDateTime(null);
                return;
            }else{
                AreaInfo.appendText("Selezionare la stanza\n");
                ConfigurationController.showAlert("Select a Room!");
                return;
            }
        }catch (NullPointerException n){
            AreaInfo.appendText("Inserire data e ora di inizio\n");
            ConfigurationController.showAlert("Select a date and time!");
            return;
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

}
