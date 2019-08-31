package Server;
import java.util.*;

public class HiddenMacFinder {
    private static Float DIST_WEIGHT = 0.3f;
    private static Float SSID_WEIGHT = 0.2f;
    private static Float CAT_WEIGHT = 0.2f;
    private static Float SEQN_WEIGHT = 0.3f;
    private static Float THRESHOLD = 0.7f;
    private static List<String> SSIDblackList = Arrays.asList("Eduroam", "Polito");
    private static Float CategorydistanceThreshold = 1.5f; //se due dispositivi sono distanti più di CategorydistanceThreshold, anche se sono di tipo Android -> p = 0;

    public static void FindHiddenDevices(){

        //todo debug
        Integer total = 0;
        Integer couple = 0;

        for (Map.Entry<String, DBPacket> DBpkt1 : EchoServer.final_tab.entrySet()) {
            total++;
            if(DBpkt1.getValue()!=null && isLocal(DBpkt1.getValue().getMacSource())){
                for(Map.Entry<String, DBPacket> DBpkt2 : EchoServer.final_tab.entrySet())
                    if(DBpkt1!=DBpkt2 && DBpkt2.getValue()!=null && isLocal(DBpkt2.getValue().getMacSource())){
                        //si entra in questo IF se sia DBpkt1 che DBpkt2 sono Local (e non sono la stessa entry)
                        // TODO: 21/08/2019 debug
                        couple++;

                        Float DistP = HiddenMacFinder.ComputeDistanceProb(DBpkt1.getValue(), DBpkt2.getValue());
                        Float SSIDP = HiddenMacFinder.ComputeSSIDProb(DBpkt1.getValue(), DBpkt2.getValue());
                        Float CategoryP = HiddenMacFinder.ComputeCategoryProb(DBpkt1.getValue(), DBpkt2.getValue());
                        Float SeqNP = HiddenMacFinder.ComputeSeqNPorob(DBpkt1.getValue(), DBpkt2.getValue());
                        Float Prob = DistP + SSIDP + CategoryP + SeqNP;

                        System.out.println("couple analized: " + DBpkt1.getValue().getMacSource() + " and " + DBpkt2.getValue().getMacSource());
                        System.out.println("Distance prob: " + DistP + " SSID prob: " + SSIDP + " Category prob: " + CategoryP + " SeqN prob: " + SeqNP + ", TOTAL = " + Prob);

                        if(Prob >= HiddenMacFinder.THRESHOLD){
                            //EchoServer.final_tab.put(DBpkt2.getKey(), null);
                            DBpkt2.setValue(null);
                            Float err = 1-Prob; //distanza da 1 (certezza che si tratta dello stesso dispositivo)
                            System.out.println("candidato TROVATO: due MAC local con alta probabilità di appartenere allo stesso dispositivo!! ERRORE = " + err);
                            if(DBpkt1.getValue().getErr() < err) //POLITICA WORST CASE
                                DBpkt1.getValue().setErr(err);
                            DBpkt1.getValue().setLocalMacMargedNumber(DBpkt1.getValue().getLocalMacMargedNumber()+1);
                        }
                        else{
                            //trovati due mac local che non sono stati intesi come unico dispositivo.
                            if(Prob<0)
                                Prob = 0.03f; //siamo praticamente certi che non sia lo stesso dispositivo. Per essere negativo infatti vuol dire che un mac è di tipo android mentre l'altro no. Approssimiamo comunque ad un errore minimale
                            Float err = Prob; //distanza dallo zero (certezza che non sono lo stesso dispositivo)
                            System.out.println("candidato SCARTATO: si assume dispositivi diversi!! ERRORE = " + err);
                            if(DBpkt1.getValue().getErr() < err) //POLITICA WORST CASE
                                DBpkt1.getValue().setErr(err);
                        }
                    }
            }
        }//End Cycle
        //System.out.println("Number of elements: " + total + " and couple analized: " + couple);
    }

    private static Float ComputeDistanceProb(DBPacket pk1, DBPacket pk2){
        float P;
        float x1 = pk1.getPosX(), x2 = pk2.getPosX(), y1 = pk1.getPosY(), y2 = pk2.getPosY();
        double distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
        double factor = distance/2;
        P = (float) (DIST_WEIGHT/(factor+1));
        return P;
    }

    private static Float ComputeSSIDProb(DBPacket pk1, DBPacket pk2){
        Float P = 0f;
        if(pk1.getSSID()==null || pk2.getSSID()==null)
            return P;
        if(pk1.getSSID().compareTo(pk2.getSSID()) == 0 && !SSIDblackList.contains(pk1))
            P = SSID_WEIGHT;
        return P;
    }

    private static Float ComputeCategoryProb(DBPacket pk1, DBPacket pk2){
        Float P = 0f;

        List<String> AndroidOUI = Arrays.asList("da:a1:19", "92:68:c3");

        String OUI1 = pk1.getMacSource().substring(0,8);
        String OUI2 = pk2.getMacSource().substring(0,8);

        //System.out.println("OUI1: " + OUI1 + " OUI2: " + OUI2);

        for (String target: AndroidOUI){
            if(OUI1.compareTo(target)== 0 && OUI2.compareTo(target)==0) //Entrambi i mac hanno oui uguale tra loro ed è di tipo android local
            {
                float x1 = pk1.getPosX(), x2 = pk2.getPosX(), y1 = pk1.getPosY(), y2 = pk2.getPosY();
                double distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
                if(distance <= CategorydistanceThreshold)
                    P = CAT_WEIGHT;
            }
            else if((OUI1.compareTo(target)== 0 | OUI2.compareTo(target)==0) & OUI1.compareTo(OUI2)!=0 ) //se sono diversi, ma uno è di tipo android -> altamente probabile che siano due dispositivi diversi
                P = -CAT_WEIGHT;
        }

        return P;
    }

    private static Float ComputeSeqNPorob(DBPacket pk1, DBPacket pk2){
        if(pk1.getSequenceNumber()==null || pk2.getSequenceNumber()==null)
            return 0f;

        Integer seqDistance = Math.abs(pk1.getSequenceNumber() - pk2.getSequenceNumber());
        if(seqDistance<=100)
            return SEQN_WEIGHT; //MAX VALUE
        else if(seqDistance>100 && seqDistance<=200)
            return SEQN_WEIGHT/2;
        else if(seqDistance>200 && seqDistance<=300)
            return SEQN_WEIGHT/3;
        //If distance > 300 we consider no weight for SeqNumber criteria
        return 0f;
    }
    /***
     *
     * @param mac
     * @return true/false
     *
     * test su mac locale/globale
     */
    public static Boolean isLocal(String mac){

        boolean islocal = false;
        //System.out.println("check mac -> "+ mac);
        String[] octets = mac.split(":");
        char test=octets[0].charAt(1); //cifra esadecimale (4bit) che mi interessa -> devo analizzarne il terzo bit

        String tests = Character.toString(test);
        int decimal = Integer.parseInt(tests, 16);
        String lower = Integer.toBinaryString(decimal);

        //System.out.println("Mac is: " + mac + " Test is: " + tests + " decimal version is: " + decimal + " lower is:"+lower);

        //risulta utile ragionare, invece che come terzo bit, come PENULTIMO bit in modo da essere indipendente dal numero di bit necessari per rappresentare il numero.
        if(lower.length()<2){ //if the number is in 1 bit: the third MSB of 4bit is always zero.System.out.println("SOME ERROR IN ISLOCAL FUNTION - LOWER VALUE!!!!!!!!!! Mac is: " + mac + " Test is: " + tests + " decimal version is: " + decimal + " lower is:"+lower);
            islocal = false;
        }
        else{
          if(lower.charAt(lower.length()-2)=='1')
              islocal = true;
          else
              islocal = false;
        }
        return islocal;
        /*
        if(lower.charAt(2)=='1'){
            //System.out.println("local MAC foud!");
            return true;
        }
        else{
            //System.out.println("global");
            return false;
        }*/
    }

    public static void addLocalFake(){


        ArrayList<String> digests = new ArrayList<String>();

        for (int i = 0; i<5; i++){
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 32; j++) {
                Random rand = new Random();
                builder.append(rand.nextInt(9));
            }
            digests.add(builder.toString());
        }

        ArrayList<DBPacket> pkts = new ArrayList<DBPacket>();

        List<String> macs = Arrays.asList("da:a1:19:00:00:00", "da:a1:19:00:00:01", "da:a1:19:00:00:02", "da:a1:19:00:00:03", "0a:00:00:00:00:00");

        //TEST 1: TUTTI I CRITERI SODDISFATTI -> MERGED
        //PDist circa 0.3, Pssid = 0.2, PCat = 0.3, PseqN = dist < 100 -> 0.3. P=circa 1, err=circa 0
/*

        pkts.add(new DBPacket(digests.get(0), 1566377514000L, "1", 3.5f, 4.6f, macs.get(0), 0f, 0, "StessoSSID", 1550));
        pkts.add(new DBPacket(digests.get(1), 1566377518000L, "1", 3.0f, 4.1f, macs.get(1), 0f, 0, "StessoSSID", 1570));

*/

        //TEST 2: CRITERI NON SODDISFATTI O P BASSA -> NOT MERGED
        //PDist motlo minore di 0.3, Pssid = 0, PCat = -0.2, PseqN = dist > 300 -> 0. P=circa -0.2 -> 0.03 , err=circa 0
/*

        pkts.add(new DBPacket(digests.get(0), 1566377514000L, "1", 3.5f, 4.6f, macs.get(0), 0f, 0, "StessoSSID", 1550));
        pkts.add(new DBPacket(digests.get(1), 1566377518000L, "1", 5.0f, 1.1f, macs.get(4), 0f, 0, "DiversoSSID", 2570));

*/



        //primi 3 vengono uniti
        pkts.add(new DBPacket(digests.get(0), 1566377514000L, "1", 3.5f, 4.6f, macs.get(0), 0f, 0, "StessoSSID", 1550));
        pkts.add(new DBPacket(digests.get(1), 1566377518000L, "1", 3.0f, 4.1f, macs.get(1),  0f, 0, "StessoSSID", 1590));
        pkts.add(new DBPacket(digests.get(2), 1566377519000L, "1", 3.3f, 4.3f, macs.get(2),  0f, 0, "StessoSSID", 3000));
        pkts.add(new DBPacket(digests.get(3), 1566377538000L, "1", 5.0f, 1.1f, macs.get(3),  0f, 0, "StessoSSID", 250)); //non unito -> P=0.6
        pkts.add(new DBPacket(digests.get(4), 1566377558000L, "1", 3.0f, 4.5f, macs.get(4),  0f, 0, "DiversoSSID", 370)); //non unito -> P=0.4

        int i = 0;
        synchronized (EchoServer.final_tab) {
            for (DBPacket pkt : pkts) {
                EchoServer.final_tab.put(macs.get(i), pkt);
                i++;
            }
            //System.out.println(EchoServer.final_tab);
        }
    }
}
