import lombok.Data;

import java.util.ArrayList;
import java.util.List;


public class PacketRec {

    private List<Integer> RSSI;
    private Integer n_ESP;
    private String MacSource;
    private String SSID;
    private String Digest;


    /*
        costruttore di un record di tipo PacketRec, a partire da un oggetto di tipo Packet
     */

    public PacketRec(Packet packet) {
        setDigest(packet.getDigest());
        setMacSource(packet.getMacSource());
        setSSID(packet.getSSID());
        setN_ESP(0);
        setRSSI(new ArrayList<Integer>());
        newSignal(packet.getRSSI());
    }

    @Override
    public String toString() {
        return "PacketRec{" +
                "RSSI=" + RSSI +
                ", n_ESP=" + n_ESP +
                ", MacSource='" + MacSource + '\'' +
                ", SSID='" + SSID + '\'' +
                ", Digest='" + Digest + '\'' +
                '}';
    }

    public List<Integer> getRSSI() {
        return RSSI;
    }

    public void setRSSI(List<Integer> RSSI) {
        this.RSSI = RSSI;
    }

    public Integer getN_ESP() {
        return n_ESP;
    }

    public void setN_ESP(Integer n_ESP) {
        this.n_ESP = n_ESP;
    }

    public String getMacSource() {
        return MacSource;
    }

    public void setMacSource(String macSource) {
        MacSource = macSource;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getDigest() {
        return Digest;
    }

    public void setDigest(String digest) {
        Digest = digest;
    }

    /*
            aggiornamento della lista di rssi
         */
    public void newSignal(int rssi) {
        setN_ESP(getN_ESP()+1);
        getRSSI().add(rssi);
    }
}