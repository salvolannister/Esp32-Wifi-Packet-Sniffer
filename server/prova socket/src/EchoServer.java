import DB.DBUtil;

import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class EchoServer {
    //public static  Long TOT_ESP= Long.valueOf(1); //da togliere

    /**
     * @tab: struttura utilizzata per immagazzinare tutti i pacchetti ricevuti dall'ESP. String = DIGEST
     * @sum_tab: lista che raccoglie tutti i pacchetti che sono stati ricevuti da tutte le schedine
     * @final_tab: una struttura che ricalca la struttura del db e avente la duplice funzione di agevolare
     *              la visualizzazione in tempo reale e la ricerca di mac duplicati. String = Mac
     * @start_time: l'ultimo valora inviato alle schedine per definire il tempo di inizio di sniffing
     * @delta_time: quando si calcola start_time si aggiunge un delta per evitare che i tempi di latenza diano problemi
     * @sniffing_time: il tempo di sniffing della schedina allo scadere del quale ci sarà un messaggio di STOP -> aggiornamento start_time successivo
     * @delta_update: per evitare che tutte le schedine allo STOP aggiornino start_time all'interno della stessa finestra, si verifica se
     *                la differenza tra il tempo attuale e start_time è maggiore di un certo delta
     */

    public  static Map<String, PacketRec> tab= new HashMap<String, PacketRec>();
    public  static List<Sum_PacketRec> sum_tab= new ArrayList<Sum_PacketRec>();
    public  static Map<String, DBPacket> final_tab= new HashMap<String, DBPacket>();
    public  static Configuration conf= new Configuration(2.5);
    public  static Long start_time = Long.valueOf(0);
    public static int delta_time = 25;
    public static int sniffing_time = 40;
    private static int delta_update = sniffing_time - 10;


    public static void main(String[] args) throws IOException, SQLException {

    	conf.setNumEsp(3);

        DBUtil db=new DBUtil();
        if(!db.openConnection("fake_db.db")){
            System.err.println("Errore di Connessione al DB. Impossibile Continuare");
            System.exit(-1);
        }

        QueryFake q=new QueryFake(db.getConn());
        //q.dropDB();
        q.createDB();

        /*
        updateTime();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() { // Function runs every "period".
                updateTime();
            }
        }, 0, 1000 * sniffing_time); // funzione chiamata ogni sniffing time
        */

        while(true) {
            try (
                    ServerSocket serverSocket =
                            new ServerSocket(8080);
            ) {
                while (true) {
                    //count++;
                    new Receiver(serverSocket.accept(), conf.getNumEsp(),db).start();
                    System.out.println("io");
                    /*synchronized (tab){
                        writeFile(tab, "prova.txt");
                    }*/
                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port 8080 "
                        + " or listening for a connection");
                System.out.println(e.getMessage());
                return;
            }finally {
                db.closeConnection();
            }
        }
    }

    /**
     * funzione che aggiorna la variabile StartTime al tempo attuale + un tempo di attesa waitSec.
     * Viene chiamata ogni minuto.
     */
    /*
    private static void updateTime() {
        System.out.println("updating next sniffing time");
        // create a calendar
        Calendar cal = Calendar.getInstance();
        // get time in millis from Epoch
        Long TimeLong = cal.getTimeInMillis();
        // add to current time waitSec -> ESP start sniffing at now+waitSec
        TimeLong = TimeLong + delta_time * 1000;
        synchronized (start_time){
                // convert long to string in order to truncate at 10 number
            start_time = TimeLong;
        }
    }*/

    public static synchronized void updateTime2(){
        System.out.println("updating next sniffing time");
        // create a calendar
        Calendar cal = Calendar.getInstance();
        // get time in millis from Epoch
        Long TimeLong = cal.getTimeInMillis();
        synchronized (start_time){
            // convert long to string in order to truncate at 10 number
            if(TimeLong - start_time >= delta_update)
                start_time = TimeLong + (delta_time * 1000);
        }
    }

    public static synchronized Long resinchronize(){
        // create a calendar
        Calendar cal = Calendar.getInstance();
        // get time in millis from Epoch
        Long TimeLong = cal.getTimeInMillis();
        synchronized (start_time){
            //System.out.println("now is: "+ TimeLong + " start_time is: " + start_time + " DIFF = "+ (TimeLong - start_time));
            if(start_time == 0){ //prima volta che una schedina si connette al server
                System.out.println("Risincronizzazione.....");
                start_time = TimeLong + (delta_time * 1000);
            }
            //else if(TimeLong - start_time >= 2*delta_update) //schedina si riconnette dopo un certo tempo
            return start_time;
        }
    }

    public static synchronized int getNEsp(){
        return conf.getNumEsp();
    }

    public static synchronized void computeAvarage(){

    }
    /***
     *
     * @param tab
     *
     * funzione di debug, scrive il contenuto della mappa tab su di un file
     */
    public static void writeFile(Map<String, PacketRec> tab, String path) {
        File f=new File(".");
        f.getAbsolutePath();
        String url =f.getAbsolutePath()+"//"+path;
        try {
            File file = new File(url);
            FileWriter fw = new FileWriter(file);
            fw.write(tab.toString());
            fw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile2(List<Sum_PacketRec> tab, String path) {
        File f=new File(".");
        f.getAbsolutePath();
        String url =f.getAbsolutePath()+"//"+path;
        try {
            File file = new File(url);
            FileWriter fw = new FileWriter(file);
            fw.write(tab.toString());
            fw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}