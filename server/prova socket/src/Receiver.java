import DB.DBUtil;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;

public class Receiver extends Thread {

    Socket csocket;
    private  static Integer n_ESP;
    private Integer id;
    private DBUtil db;
    private int max_silece_time = 120*1000;

    public Receiver(Socket csocket, Integer n, DBUtil db) {
        this.csocket = csocket;
        this.n_ESP=n;
        this.db=db;
    }

    @Override
    public void run() {

        int waitSec = 15;
        String MacESPDavide = "24:0a:c4:9b:4f:ac";
        String MacESPMar= "3c:71:bf:0c:b5:38";
        String MacESPUmb= "24:0a:c4:9a:9f:3c";
        String MacESPAnt= "24:0a:c4:a2:b3:40";

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


                    Long StgartLong = EchoServer.resinchronize();
                    String StartTime = Long.toString(StgartLong);

                        /*synchronized (EchoServer.start_time){
                            TimeLong = EchoServer.start_time;
                        }*/

                        /*
                        // create a calendar
                        Calendar cal = Calendar.getInstance();
                        // get time in millis from Epoch
                        Long TimeLong = cal.getTimeInMillis();
                        // add to current time waitSec -> ESP start sniffing at now+waitSec
                        TimeLong = TimeLong + waitSec * 1000;
                        // convert long to string in order to truncate at 10 number
                        String StartTime = Long.toString(TimeLong);*/

                    System.out.print("Time Sent: ");
                    for (int i = 0; i < 10; i++) {
                        System.out.print(StartTime.charAt(i));
                        dOut.write(StartTime.charAt(i));
                    }
                    dOut.flush(); // Send off the data
                    dOut.close();
                    System.out.println();

                    //System.out.println("Time send to ESP: "+ StartTime.substring(0, Math.min(StartTime.length(), 10)));

                    //Get MAC of ESP written in the HELLO message
                    String[] mac = inputLine.split("My Mac is: ");

                    Calendar cal = Calendar.getInstance();
                    Long TimeLong = cal.getTimeInMillis();
                    TimeLong = TimeLong + waitSec * 1000;

                    //setting posizioni ESP, Controllo di ESP non più collegate e conseguente aggiornamento numero dispositivi
                    synchronized (EchoServer.conf){
                        if(EchoServer.conf.getMac_tab().containsKey(mac[1])==false){
                            if (mac[1].compareTo(MacESPDavide) == 0) {
                                //define position of this ESP
                                Polo p = new Polo(0.0, 0.0);
                                Payload pack = new Payload(TimeLong, p);
                                EchoServer.conf.getMac_tab().put(mac[1], pack);
                            }else if(mac[1].compareTo(MacESPUmb) == 0) {
                                Polo p = new Polo(0.0, 3.3);
                                Payload pack = new Payload(TimeLong, p);
                                EchoServer.conf.getMac_tab().put(mac[1], pack);
                            }else if(mac[1].compareTo(MacESPMar) == 0) {
                                Polo p = new Polo(2.4, 3.3);
                                Payload pack = new Payload(TimeLong, p);
                                EchoServer.conf.getMac_tab().put(mac[1], pack);
                            }else if(mac[1].compareTo(MacESPAnt) == 0) {
                                Polo p = new Polo(0.0, 4.0);
                                Payload pack = new Payload(TimeLong, p);
                                EchoServer.conf.getMac_tab().put(mac[1], pack);
                            }
                        }
                        //Aggiornamento dell'ultimo istante di tempo al quale la schedina ha dato segni di vita al server
                        EchoServer.conf.getMac_tab().get(mac[1]).setLastTime(TimeLong);

                        //si verifica se per il dato mac sono passati più di 60000 ms (ovvero 1min) facendo la diferenza tra il tempo del prossimo sniffing e l'ultimo registrato.
                        // Se vero si setta LastTime a MIN_VALUE
                        for(String x: EchoServer.conf.getMac_tab().keySet()) {
                            if (EchoServer.conf.getMac_tab().get(x).getLastTime() != Long.MIN_VALUE) {
                                if ((TimeLong - EchoServer.conf.getMac_tab().get(x).getLastTime()) > this.max_silece_time) {//5 min=300000
                                    System.out.println("ERRRRRRRRRRRRRRRRRRRRRR -------- some ESP " + EchoServer.conf.getMac_tab().get(x) + "not available!!!!!!!!!!!!!!!!!!!!!!!!!!!!! NOW = "+ TimeLong +
                                            " Last = "+ EchoServer.conf.getMac_tab().get(x).getLastTime() + " DIFF = "+ (TimeLong - EchoServer.conf.getMac_tab().get(x).getLastTime()));
                                    EchoServer.conf.getMac_tab().get(x).setLastTime(Long.MIN_VALUE);
                                }
                            }
                        }
                        //Si verifica a questo punto se ci sono schedine che non si fanno sentire da più di 60000 (ovvero quelle per cui al passo precedente
                        //si è impostato LeastTime a MIN_VALUE. Si aggiorna di conseguenza il numero di schedine correnti.
                        Long l=EchoServer.conf.getMac_tab().values().stream().filter(y->Long.compareUnsigned(y.getLastTime(),Long.MIN_VALUE)!=0).count();
                        EchoServer.conf.setNumEsp(l.intValue());
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  n_esp = " + EchoServer.conf.getNumEsp());

                        // ???????????????????????????????????????????????????????????????????????????
                        // ???????????????????????????????????????????????????????????????????????????
                        //CONTROLLARE!!!!!!!!!!!!!!!!!!! update anche dell'attributo della classe!!! this.nESP

                        n_ESP = EchoServer.conf.getNumEsp(); //?????????????????????????????????????????????

                        // ???????????????????????????????????????????????????????????????????????????
                        // ???????????????????????????????????????????????????????????????????????????
                        // ???????????????????????????????????????????????????????????????????????????


                        writeFileConf(EchoServer.conf, "Conf.txt");
                    }//chiusura accesso sincronizzato

                    //si elaborano i dati precedentemente ricevuti (alias: appena finito di catturare).
                    //Si sfrutta in questo modo il tempo di sniffing della schedina per l'elaborazione lato Server.
                    //Dunque si calcola la distanza
                    //e si aggiungono le info sul DB
                    synchronized (EchoServer.sum_tab){
                        if(EchoServer.getNEsp()>2){
                            System.out.println("MINUMUM # OF ESP OK!!!");
                            //todo debug
                            computeAvarage();
                            synchronized (EchoServer.final_tab) {
                                for(Sum_PacketRec p:EchoServer.sum_tab) {
                                    List<Distance> dist = new ArrayList<>();
                                    for (String s : p.getRSSI().keySet()) {
                                        synchronized (EchoServer.conf) {
                                            //creo ed aggiungo alla lista, un oggetto Distance costituito dalla posizione di una schedina e il valore di RSSI
                                            dist.add(new Distance(EchoServer.conf.getMac_tab().get(s).getPosizione(), p.getRSSI().get(s)));
                                            // DEBUG System.out.println("Distance value before calling computeDistance: " + EchoServer.conf.getMac_tab().get(s).getPosizione().toString());
                                        }
                                    }
                                    Polo pos = computePosition(dist);

                                    EchoServer.final_tab.put(p.getMacSource(), new DBPacket(p.getdigest(), Long.parseLong(p.getTimeStamp()) * 1000, 1, (float) pos.getX(), (float) pos.getY(), p.getMacSource(), p.getSSID()));
                                    //double average=p.getRSSI().values().stream().mapToInt(i->i).average().getAsDouble();

                                    /*
                                    if (isLocal(p.getMacSource()) == true) {
                                        Long duplicate = EchoServer.final_tab.values().stream()
                                                .filter(x -> x.getPosX() == pos.getX()).filter(y -> y.getPosY() == pos.getY())
                                                .count();
                                        if (duplicate == 0) {
                                            System.out.println("Local MAC, ma nessun elemento nella stessa posizione trovato -> carico nel DB un nuovo dispositivo!");
                                            EchoServer.final_tab.put(p.getMacSource(), new DBPacket(p.getdigest(), Long.parseLong(p.getTimeStamp()) * 1000, 1, (float) pos.getX(), (float) pos.getY(), p.getMacSource()));
                                            try {
                                                QueryFake q = new QueryFake(db.getConn());

                                                if (!q.aggiungiTupla(p.getdigest(), p.getMacSource(), Long.parseLong(p.getTimeStamp()) * 1000, 1, (float) pos.getX(), (float) pos.getY())) {
                                                    System.err.println("Errore nell'inserimento");
                                                    System.exit(-1);
                                                }
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else{
                                            // TODO: 14/08/2019 inserire calcolo che aggiorna una qualche misura di errore
                                            System.out.println("Torvato local MAC con posizione identica ad altro dispositivo -> si suppone sia lo stesso");
                                        }
                                    }
                                    else { //MAC NON LOCALE
                                        EchoServer.final_tab.put(p.getMacSource(), new DBPacket(p.getdigest(), Long.parseLong(p.getTimeStamp()) * 1000, 1, (float) pos.getX(), (float) pos.getY(), p.getMacSource()));
                                        try {
                                            QueryFake q = new QueryFake(db.getConn());

                                            if (!q.aggiungiTupla(p.getdigest(), p.getMacSource(), Long.parseLong(p.getTimeStamp()) * 1000, 1, (float) pos.getX(), (float) pos.getY())) {
                                                System.err.println("Errore nell'inserimento");
                                                System.exit(-1);
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }*/

                                    dist.clear();
                                }//fine for di sumpacket
                                //HiddenMacFinder.addLocalFake(); //// TODO: 23/08/2019 DELETE!!!
                                HiddenMacFinder.FindHiddenDevices();
                                DBInsert();
                                //todo inviare final_tab alla gui
                                writeFileFinalTab(EchoServer.final_tab, "Final.txt");
                                EchoServer.final_tab.clear();
                            }//fine synchronized finaltab
                        }
                        else
                            System.out.println("Not enaugh data to process Location!!");
                        if(EchoServer.sum_tab.isEmpty()==false)
                            writeFileSumTab(EchoServer.sum_tab, "sumTab.txt");
                        EchoServer.sum_tab.clear();
                    }
                    synchronized (EchoServer.tab){
                        if(EchoServer.tab.isEmpty()==false) {
                            writeFileTab(EchoServer.tab, "Tab.txt");
                            //EchoServer.tab.clear();
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
                }//Chiusura messaggio HELLO
                else { //se si entra qui è perchè è stato ricevuto uno dei pacchetti sniffati dalla schedina oppure un messaggio di fine.
                    synchronized (EchoServer.tab) { //accesso concorrente
                        //se è stato ricevuto un pacchetto si crea l'oggetto Packet che esegue il parsing dei dati.
                        //quindi si inserisce il nuovo pacchetto nella struttura tab. Il metodo checkInsert verificherà se esista già tale entry.
                        if (inputLine.compareTo("STOP") != 0) {
                            Packet p = new Packet(inputLine);
                            if (checkInsert(p, EchoServer.tab) == false)
                                System.out.println("pacchetto già ricevuto");
                        } else {
                            EchoServer.updateTime2(); //metodo in mutua esclusione per aggiornare (se non è stato già fatto per questa sessione) lo start_time
                            System.out.println("Stop message received: " + inputLine);
                        }
                    }//chiusura accesso concorrente
                }
            }//chiusura while
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
     * in particolare controlla il campo RSSI della mappa tab in modo da fare un inserimento solo se non si è ancora
     * ricevuta una posizione da una certa scheda
     * e in caso di primo inserimento crea l'oggetto packet rec e lo inserisce.
     *
     * La funzione è chiamata con accesso concorrente a tab
     */
    private static boolean checkInsert (Packet p, Map < String, PacketRec > tab){

        boolean esito =false;
        //inserisco nella mappa principale

        if (tab.containsKey(p.getDigest()) == true) {
            if (tab.get(p.getDigest()).getRSSI().containsKey(p.getIdMac()) == false) {
                //si entra in questo if se in tab esiste già un pacchetto con lo stesso digest.
                //siccome è necessario gestire situazioni in cui un dispositivo invii "contemporaneamente" due richieste identiche,
                //si controlla se tale pacchetto (individuato dal digest) è presente perchè catturato da un'altra schedina (corretto -> si entra in questo if)
                //o se è la stessa che ha inviato per la seconda (o più) volta lo stesso (scorretto -> non si fa nulla).
                tab.get(p.getDigest()).newSignal(p.getIdMac(), p.getRSSI());
                esito=true;
            }
        } else { //il pacchetto non è ancora stato registrato -> è la prima schedina ad averlo inviato.
            tab.put(p.getDigest(), new PacketRec(p));
            esito=true;
        }
        if(esito==true) {
            synchronized (EchoServer.sum_tab) {
                boolean trovato = false;
                if (tab.get(p.getDigest()).getN_ESP() == EchoServer.getNEsp()) { //una volta inserito controllo se il pacchetto è stato inviato da tutte le schedine

                    //si verifica se è stato già rilevato quel determinato mac tramite altri pacchetti ricevuti da tutte le schedine.
                    //ovvero, se esiste già un'entry in sum_tab che contiene quel determinato mac.
                    for(Sum_PacketRec pkt:EchoServer.sum_tab){
                        if(tab.get(p.getDigest()).getMacSource().compareTo(pkt.getMacSource())==0){
                            //si aggiunge al primo pacchetto sniffato e insierito in sum_tab un nuovo elemento nella
                            //lista RSSIs in modo da collezionare tutti i pacchetti relativi ad uno stesso mac address
                            //e ricevuto da tutte le schedine
                            pkt.addRSSI(tab.get(p.getDigest()).getRSSI());
                            trovato = true;
                            System.out.println("MAC già catturato nell'arco del minuto -> aggiungo info a packet esistente");
                            break;
                        }
                    }

                    if(!trovato)
                    {
                        System.out.println("Nuovo MAC catturato");
                        Sum_PacketRec s = new Sum_PacketRec(tab.get(p.getDigest()).getRSSI(),
                                tab.get(p.getDigest()).getMacSource(),
                                tab.get(p.getDigest()).getDigest(),
                                tab.get(p.getDigest()).getTimeStamp(),
                                tab.get(p.getDigest()).getSSID());

                        EchoServer.sum_tab.add(s);
                        //EchoServer.tab.remove(p.getDigest());
                    }
                }
            }
        }
        return esito;
    }

    private void DBInsert() {
        for (Map.Entry<String, DBPacket> DBpkt1 : EchoServer.final_tab.entrySet()) {
            if(DBpkt1.getValue()!=null){
                try {
                    QueryFake q = new QueryFake(db.getConn());
                    DBPacket pkt = DBpkt1.getValue();

                    if (!q.aggiungiTupla(pkt.getDigest(), pkt.getMacSource(), pkt.getTimeStamp(), pkt.getRoom(), pkt.getPosX(), pkt.getPosY(), pkt.getErr(), pkt.getLocalMacMargedNumber())) {
                        System.err.println("Errore nell'inserimento");
                        System.exit(-1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void computeAvarage(){
        for(Sum_PacketRec pkt: EchoServer.sum_tab) {
            if (pkt.getRSSIs().size() > 1) //se invece il mac è stato catturato una sola volta non è possibile calcolare la media
            {
                Iterator it = pkt.getRSSI().entrySet().iterator();
                //iteriamo su tutte le entry nella map rssi di sumPacketRec. La usiamo per accedere alle chiavi (ovvero MAC della schedina).
                while (it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    String key = (String) pair.getKey(); //ESP mac address
                    pair.setValue(0);
                    for (Map<String, Integer> rssi: pkt.getRSSIs()){
                        Integer rssiSum = rssi.get(key);
                        rssiSum += (Integer) pair.getValue();
                        pair.setValue(rssiSum);
                    }
                    //System.out.println("key = " + key + " Somma calcolata = " + pair.getValue());
                    //System.out.println("media calocata per key = " + key + " Come: " + (Integer)pair.getValue() + " / " + pkt.getRSSIs().size());
                    pair.setValue((Integer)pair.getValue()/pkt.getRSSIs().size());
                }
                //System.out.println("lista di Map = " + pkt.getRSSIs());
                //System.out.println("Map con media calcolata = " + pkt.getRSSI());
            }
        }

    }

    private Polo computePosition(List<Distance> d) {

        synchronized (EchoServer.conf) {
            int numESP = EchoServer.conf.getNumEsp();

            double[][] positions = new double[numESP][2];
            double[] distances = new double[numESP];

            for (int i = 0; i < numESP; i++) {

                //posizione
                positions[i][0] = d.get(i).getPosizione().getX();
                positions[i][1] = d.get(i).getPosizione().getY();
                //distanza
                distances[i] = d.get(i).getDistance(EchoServer.conf);
            }

            TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);
            NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());
            Optimum nonLinearOptimum = nlSolver.solve();
            RealVector computedPOS = nonLinearOptimum.getPoint();
            Polo pos = new Polo(computedPOS.getEntry(0), computedPOS.getEntry(1));
            return pos;
        }
    }
    /***
     *
     * @param value
     * @param length
     * @return
     *
     * funzione che estrae il messaggio dai pacchetti di controllo "hello" e "stop"
     */
    private static String trunc(String value, int length) {
        String val = "";
        if (value != null && value.length() > length)
            val = value.substring(0, length);
        return val;
    }

    public static void writeFileFinalTab(Map<String, DBPacket> tab, String path){
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

    public static void writeFileTab(Map<String, PacketRec> tab, String path){
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

    public static void writeFileSumTab(List<Sum_PacketRec> sumTab, String path){
        File f=new File(".");
        f.getAbsolutePath();
        String url =f.getAbsolutePath()+"//"+path;
        try {
            File file = new File(url);
            FileWriter fw = new FileWriter(file);
            fw.write(sumTab.toString());
            fw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFileConf(Configuration conf, String path) {

        File f=new File(".");
        f.getAbsolutePath();
        String url =f.getAbsolutePath()+"//"+path;
        try {
            File file = new File(url);
            FileWriter fw = new FileWriter(file);
            fw.write(conf.toString());
            fw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /***
     *
     * @param mac
     * @return true/false
     *
     * test su mac locale/globale
     */


    /*public static Boolean isLocal(String mac){
        String[] octets = mac.split(":");
        char test=octets[0].charAt(1);
        String lower=Integer.toBinaryString(test);
        //System.out.println(lower);
        if(lower.charAt(4)=='1'){
            System.out.println("local MAC foud!");
            return true;
        }
        else{
            //System.out.println("global");
            return false;
        }
    }*/


}