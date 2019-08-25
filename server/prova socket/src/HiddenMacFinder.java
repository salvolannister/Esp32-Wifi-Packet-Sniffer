import java.util.*;

public class HiddenMacFinder {
    private static Float DIST_WEIGHT = 0.4f;
    private static Float SSID_WEIGHT = 0.3f;
    private static Float CAT_WEIGHT = 0.3f;
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
                        Float Prob = DistP + SSIDP + CategoryP;

                        //System.out.println("couple analized: " + DBpkt1.getValue().getMacSource() + " and " + DBpkt2.getValue().getMacSource());
                        //System.out.println("Distance prob: " + DistP + " SSID prob: " + SSIDP + " Category prob: " + CategoryP + ", TOTAL = " + Prob);

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
        P = (float) (DIST_WEIGHT/(distance+1));
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

    /***
     *
     * @param mac
     * @return true/false
     *
     * test su mac locale/globale
     */
    public static Boolean isLocal(String mac){
        //System.out.println("check mac -> "+ mac);
        String[] octets = mac.split(":");
        char test=octets[0].charAt(1);

        String tests = Character.toString(test);
        int decimal = Integer.parseInt(tests, 16);
        String lower = Integer.toBinaryString(decimal);

        //System.out.println("Mac is: " + mac + " Test is: " + tests + " decimal version is: " + decimal + " lower is:"+lower);
        if(lower.charAt(2)=='1'){
            //System.out.println("local MAC foud!");
            return true;
        }
        else{
            //System.out.println("global");
            return false;
        }
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

        //primi 3 vengono uniti
        List<String> macs = Arrays.asList("da:a1:19:00:00:00", "da:a1:19:00:00:01", "da:a1:19:00:00:02", "da:a1:19:00:00:03", "0a:00:00:00:00:00");
        pkts.add(new DBPacket(digests.get(0), 1566377514000L, 1, 3.5f, 4.6f, macs.get(0), "StessoSSID"));
        pkts.add(new DBPacket(digests.get(1), 1566377518000L, 1, 3.0f, 4.1f, macs.get(1), "StessoSSID"));
        pkts.add(new DBPacket(digests.get(2), 1566377519000L, 1, 3.3f, 4.3f, macs.get(2), "StessoSSID"));
        pkts.add(new DBPacket(digests.get(3), 1566377538000L, 1, 5.0f, 1.1f, macs.get(3), "StessoSSID")); //non unito -> P=0.6
        pkts.add(new DBPacket(digests.get(4), 1566377558000L, 1, 3.0f, 4.5f, macs.get(4), "DiversoSSID")); //non unito -> P=0.4

        int i = 0;
        synchronized (EchoServer.final_tab) {
            for (DBPacket pkt : pkts) {
                EchoServer.final_tab.put(macs.get(i), pkt);
                i++;
            }
            System.out.println(EchoServer.final_tab);
        }
    }
}
