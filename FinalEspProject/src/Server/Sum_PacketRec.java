package Server;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sum_PacketRec {

    //la prima volta che si rileva un mac da Tot schedine si inizializza RSSI
    //quando si rileva in seguito dinuovo uno stesso mac si aggiunge all'entry iniziale
    //dei valori di rssi nella lista RSSIs
    private Map<String, Integer> RSSI;
    private ArrayList<Map<String, Integer>> RSSIs = new ArrayList<Map<String, Integer>>();
    private String MacSource;
    private String digest;
    private String TimeStamp;
    private String SSID;
    private Integer SequenceNumber;

    public Sum_PacketRec(Map<String, Integer> RSSI, String macSource, String digest, String timeStamp, String ssid) {
        this.RSSI = RSSI;
        Map<String, Integer> copyRssi = new HashMap<String,Integer>(RSSI);
        this.RSSIs.add(copyRssi);
        MacSource = macSource;
        this.digest = digest;
        TimeStamp = timeStamp;
        SSID = ssid;
    }

    public Sum_PacketRec(Map<String, Integer> RSSI, String macSource, String digest, String timeStamp) {
        this.RSSI = RSSI;
        Map<String, Integer> copyRssi = new HashMap<String,Integer>(RSSI);
        this.RSSIs.add(copyRssi);
        MacSource = macSource;
        this.digest = digest;
        TimeStamp = timeStamp;
        SSID = null;
    }

    public Sum_PacketRec(Map<String, Integer> RSSI, String macSource, String digest, String timeStamp, String SSID, Integer sequenceNumber) {
        this.RSSI = RSSI;
        this.RSSIs = RSSIs;
        MacSource = macSource;
        this.digest = digest;
        TimeStamp = timeStamp;
        this.SSID = SSID;
        SequenceNumber = sequenceNumber;
    }

    public void setRSSIs(ArrayList<Map<String, Integer>> RSSIs) {
        this.RSSIs = RSSIs;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Integer getSequenceNumber() {
        return SequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        SequenceNumber = sequenceNumber;
    }

    public void addRSSI(Map<String, Integer> rssi){
        RSSIs.add(rssi);
    }

    public ArrayList<Map<String, Integer>> getRSSIs(){
        return this.RSSIs;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
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
        return Objects.equals(RSSI, that.RSSI) &&
                Objects.equals(RSSIs, that.RSSIs) &&
                Objects.equals(MacSource, that.MacSource) &&
                Objects.equals(digest, that.digest) &&
                Objects.equals(TimeStamp, that.TimeStamp) &&
                Objects.equals(SSID, that.SSID) &&
                Objects.equals(SequenceNumber, that.SequenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(RSSI, RSSIs, MacSource, digest, TimeStamp, SSID, SequenceNumber);
    }

    @Override
    public String toString() {
        return "Sum_PacketRec{" +
                "RSSI=" + RSSI +
                ", RSSIs=" + RSSIs +
                ", MacSource='" + MacSource + '\'' +
                ", digest='" + digest + '\'' +
                ", TimeStamp='" + TimeStamp + '\'' +
                ", SSID='" + SSID + '\'' +
                ", SequenceNumber=" + SequenceNumber +
                '}';
    }
}
