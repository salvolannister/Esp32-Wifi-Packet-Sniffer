import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HiddenMacFinder {
    private static Float DIST_WEIGHT = 0.4f;
    private static Float SSID_WEIGHT = 0.3f;
    private static Float CAT_WEIGHT = 0.3f;
    private static Float THRESHOLD = 0.7f;
    private static List<String> SSIDblackList = Arrays.asList("Eduroam", "Polito");
    private static Float CategorydistanceThreshold = 1.5f; //se due dispositivi sono distanti più di CategorydistanceThreshold, anche se sono di tipo Android -> p = 0;

    public static void FindHiddenDevices( Map<String, DBPacket> final_map){

        //todo debug
        Integer total = 0;
        Integer couple = 0;

        for (Map.Entry<String, DBPacket> DBpkt1 : final_map.entrySet()) {
            total++;
            if(DBpkt1!=null && isLocal(DBpkt1.getValue().getMacSource())){
                for(Map.Entry<String, DBPacket> DBpkt2 : final_map.entrySet())
                    if(DBpkt1!=DBpkt2 && DBpkt2!=null && isLocal(DBpkt2.getValue().getMacSource())){
                        //si entra in questo IF se sia DBpkt1 che DBpkt2 sono Local (e non sono la stessa entry)
                        // TODO: 21/08/2019 debug
                        couple++;

                        Float DistP = HiddenMacFinder.ComputeDistanceProb(DBpkt1.getValue(), DBpkt2.getValue());
                        Float SSIDP = HiddenMacFinder.ComputeSSIDProb(DBpkt1.getValue(), DBpkt2.getValue());
                        Float CategoryP = HiddenMacFinder.ComputeCategoryProb(DBpkt1.getValue(), DBpkt2.getValue());
                        Float Prob = DistP + SSIDP + CategoryP;

                        System.out.println("Distance prob: " + DistP + " SSID prob: " + SSIDP + " Category prob: " + CategoryP + ", TOTAL = " + Prob);

                        if(Prob >= HiddenMacFinder.THRESHOLD){
                            System.out.println("candidato trovato: due MAC local con alta probabilità di appartenere allo stesso dispositivo!!");
                            DBpkt2 = null;
                            Float err = 1-Prob; //distanza da 1 (certezza che si tratta dello stesso dispositivo)
                            if(DBpkt1.getValue().getErr() < err) //POLITICA WORST CASE
                                DBpkt1.getValue().setErr(err);
                            DBpkt1.getValue().setLocalMacMargedNumber(DBpkt1.getValue().getLocalMacMargedNumber()+1);
                        }
                        else{
                            //trovati due mac local che non sono stati intesi come unico dispositivo.
                            if(Prob<0)
                                Prob = 0.03f; //siamo praticamente certi che non sia lo stesso dispositivo. Per essere negativo infatti vuol dire che un mac è di tipo android mentre l'altro no. Approssimiamo comunque ad un errore minimale
                            Float err = Prob; //distanza dallo zero (certezza che non sono lo stesso dispositivo)
                            if(DBpkt1.getValue().getErr() < err) //POLITICA WORST CASE
                                DBpkt1.getValue().setErr(err);
                        }
                    }
            }
        }//End Cycle
        System.out.println("Number of elements: " + total + " and couple analized: " + couple);
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

        System.out.println("OUI1: " + OUI1 + " OUI2: " + OUI2);

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
    }
}
