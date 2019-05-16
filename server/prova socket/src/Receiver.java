import DB.DBUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class Receiver extends Thread {

    Socket csocket;
    private  static Integer n_ESP;
    private Integer id;
    private DBUtil db;

    public Receiver(Socket csocket, Integer n, DBUtil db) {
        this.csocket = csocket;
        this.n_ESP=n;
        this.db=db;
    }

    @Override
    public void run() {

        int waitSec = 10;
        String MacESPDavide = "24:0a:c4:9b:4f:ac";



            try (

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(csocket.getInputStream()));

                    DataOutputStream dOut = new DataOutputStream(csocket.getOutputStream());

            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String str = trunc(inputLine, 5);
                    if (str.compareTo("Hello") == 0) {

                        System.out.println(inputLine);

                        // create a calendar
                        Calendar cal = Calendar.getInstance();
                        // get time in millis from Epoch
                        Long TimeLong = cal.getTimeInMillis();
                        // add to current time waitSec -> ESP start sniffing at now+waitSec
                        TimeLong = TimeLong + waitSec * 1000;
                        // convert long to string in order to truncate at 10 number
                        String StartTime = Long.toString(TimeLong);

                        //VECCHIO
                        //out.println(StartTime.substring(0, Math.min(StartTime.length(), 10)));
                        //out.close();
                        //

                        //PROVA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        for (int i = 0; i < 10; i++) {
                            System.out.print(StartTime.charAt(i));
                            dOut.write(StartTime.charAt(i));
                        }
                        dOut.flush(); // Send off the data
                        dOut.close();
                        System.out.println();

                        //System.out.println("Time send to ESP: "+ StartTime.substring(0, Math.min(StartTime.length(), 10)));

                        //check if MAC has changed!!!!!
                        String[] mac = inputLine.split("My Mac is: ");
                        if (mac[1].compareTo(MacESPDavide) != 0)
                            System.out.println("---------- MAC HAS CHANGED!!!!!!!!!");

                        /*
                            trilaterazione+db

                         */
                        synchronized (EchoServer.sum_tab){
                            for(Sum_PacketRec p:EchoServer.sum_tab){

                                //giusto per inserire un valore
                                double average=p.getRSSI().values().stream().mapToInt(i->i).average().getAsDouble();

                                try {
                                    QueryFake q = new QueryFake(db.getConn());

                                    if (!q.aggiungiTupla(p.getdigest(),p.getMacSource(), Long.parseLong(p.getTimeStamp()), 1, (float) average,(float) average)) {
                                        System.err.println("Errore nell'inserimento");
                                        System.exit(-1);
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        /*synchronized (EchoServer.tab){
                            if(EchoServer.tab.isEmpty()==true){
                                System.out.println("inizio");
                            }
                            else{
                                System.out.println("in corso");
                                EchoServer.tab.clear();
                            }

                        }*/

                        break;
                    } else {
                        synchronized (EchoServer.tab) {
                            if (inputLine.compareTo("STOP") != 0) {
                                Packet p = new Packet(inputLine);
                                if (checkInsert(p, EchoServer.tab) == false)
                                    System.out.println("pacchetto già ricevuto");
                            } else {
                                System.out.println("Stop message received: " + inputLine);
                            }

                        }
                    }
                }

                }catch(IOException e){
                    System.out.println("Exception caught when trying to listen on port 8080 "
                            + " or listening for a connection");
                    System.out.println(e.getMessage());
                }


        }

    /***
     *
     * @param p
     * @param tab
     * @return
     *
     * funzione che controlla l'inserimento in mappa.
     *  in particolare controlla il campo RSSI della mappa tab in modo da fare un inserimento solo se non si è ancora
     *  ricevuta una posizione da una certa scheda
     *  e in caso di primo inserimento crea l'oggetto packet rec e lo inserisce
     */
        private static boolean checkInsert (Packet p, Map < String, PacketRec > tab){

            boolean esito;
            //inserisco nella mappa principale

            if (tab.containsKey(p.getDigest()) == true) {
                if (tab.get(p.getDigest()).getRSSI().containsKey(p.getIdMac()) == false) {

                    tab.get(p.getDigest()).newSignal(p.getIdMac(), p.getRSSI());
                    esito=true;
                }
            } else {
                tab.put(p.getDigest(), new PacketRec(p));
                //System.out.println(tab.toString());
                esito=true;
            }
            esito=false;
            synchronized (EchoServer.sum_tab) {
                if (tab.get(p.getDigest()).getN_ESP() == n_ESP) {
                    Sum_PacketRec s = new Sum_PacketRec(tab.get(p.getDigest()).getRSSI(),
                            tab.get(p.getDigest()).getMacSource(),
                            tab.get(p.getDigest()).getDigest(),
                            tab.get(p.getDigest()).getTimeStamp());
                    EchoServer.sum_tab.add(s);
                    EchoServer.tab.remove(p.getDigest());
                }

            }
            return esito;
        }


    /***
     *
     * @param value
     * @param length
     * @return
     *
     * funzione che estrae il messaggio dai pacchetti di controllo "hello" e "stop"
     */
    private static String trunc(String value, int length)
    {
        String val = "";
        if (value != null && value.length() > length)
            val = value.substring(0, length);
        return val;
    }



}