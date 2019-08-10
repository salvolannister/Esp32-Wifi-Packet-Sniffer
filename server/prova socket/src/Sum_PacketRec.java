import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sum_PacketRec {

    //la prima volta che si rileva un mac da Tot schedine si inizializza RSSI
    //quando si rileva in seguito dinuovo uno stesso mac si aggiunge all'entry iniziale
    //dei valori di rssi nella lista RSSIs
    private Map<String, Integer> RSSI;
    //todo debug
    private ArrayList<Map<String, Integer>> RSSIs = new ArrayList<Map<String, Integer>>();
    private String MacSource;
    private String digest;
    private String TimeStamp;

    public Sum_PacketRec(Map<String, Integer> RSSI, String macSource, String digest, String timeStamp) {
        //todo cancellare se funziona
        this.RSSI = RSSI;
        //todo debug
        Map<String, Integer> copyRssi = new HashMap<String,Integer>(RSSI);
        this.RSSIs.add(copyRssi);
        MacSource = macSource;
        this.digest = digest;
        TimeStamp = timeStamp;
    }
    //todo debug
    public void addRSSI(Map<String, Integer> rssi){
        RSSIs.add(rssi);
    }
    //todo debug
    public ArrayList<Map<String, Integer>> getRSSIs(){
        return this.RSSIs;
    }

    public Map<String, Integer> getRSSI() {
        return RSSI;
    }

    public void setRSSI(Map<String, Integer> RSSI) {
        this.RSSI = RSSI;
    }

    public String getMacSource() {
        return MacSource;
    }

    public void setMacSource(String macSource) {
        MacSource = macSource;
    }

    public String getdigest() {
        return digest;
    }

    public void setdigest(String digest) {
        this.digest = digest;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sum_PacketRec that = (Sum_PacketRec) o;
        return RSSI.equals(that.RSSI) &&
                MacSource.equals(that.MacSource) &&
                digest.equals(that.digest) &&
                TimeStamp.equals(that.TimeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(RSSI, MacSource, digest, TimeStamp);
    }

    @Override
    public String toString() {
        return "\nSum_PacketRec{\n" +
                "RSSI=" + RSSI +
                ", \nMacSource='" + MacSource + '\'' +
                ", \ndigest='" + digest + '\'' +
                ", \nTimeStamp='" + TimeStamp + '\'' + "\n"+
                '}';
    }
}
