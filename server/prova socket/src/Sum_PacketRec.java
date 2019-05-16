import java.util.Map;
import java.util.Objects;

public class Sum_PacketRec {

    private Map<String, Integer> RSSI;
    private String MacSource;
    private String digest;
    private String TimeStamp;


    public Sum_PacketRec(Map<String, Integer> RSSI, String macSource, String digest, String timeStamp) {
        this.RSSI = RSSI;
        MacSource = macSource;
        this.digest = digest;
        TimeStamp = timeStamp;
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
        return "Sum_PacketRec{" +
                "RSSI=" + RSSI +
                ", MacSource='" + MacSource + '\'' +
                ", digest='" + digest + '\'' +
                ", TimeStamp='" + TimeStamp + '\'' +
                '}';
    }
}
