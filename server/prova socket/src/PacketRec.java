import lombok.Data;

import java.util.*;


public class PacketRec {

    /***
     * RSSI: string -> MAC della schedina che ha sniffato il pacchetto. Integer -> valore dell'RSSI
     * n_ESP: numero di ESP che hanno catturato lo stesso pacchetto.
     */

    private Map<String, Integer> RSSI;
    private Integer n_ESP;
    private String MacSource;
    private String SSID;
    private String Digest;
    private String TimeStamp;


    /***
     *
     * @param packet
     *
     * costruttore di un oggetto di tipo PacketRec, a partire da un oggetto di tipo Packet
     */

    public PacketRec(Packet packet) {
        setDigest(packet.getDigest());
        setMacSource(packet.getMacSource());
        setSSID(packet.getSSID());
        setN_ESP(0);
        setRSSI(new HashMap<String,Integer>());
        setTimeStamp(packet.getTimeStamp());
        newSignal(packet.getIdMac(),packet.getRSSI());
    }

    @Override
    public String toString() {
        return "\nPacketRec{" +
                "\nRSSI=" + RSSI +
                ", \nn_ESP=" + n_ESP +
                ", \nMacSource='" + MacSource + '\'' +
                ", \nSSID='" + SSID + '\'' +
                ", \nDigest='" + Digest + '\'' +
                ", \nTimeStamp='"+ TimeStamp +'\''+"\n"+
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacketRec packetRec = (PacketRec) o;
        return RSSI.equals(packetRec.RSSI) &&
                n_ESP.equals(packetRec.n_ESP) &&
                MacSource.equals(packetRec.MacSource) &&
                SSID.equals(packetRec.SSID) &&
                Digest.equals(packetRec.Digest) &&
                TimeStamp.equals(packetRec.TimeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(RSSI, n_ESP, MacSource, SSID, Digest, TimeStamp);
    }

    public Map<String, Integer> getRSSI() {
        return RSSI;
    }

    public void setRSSI(Map<String, Integer> RSSI) {
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

    public String getTimeStamp() { return TimeStamp; }

    public void setTimeStamp(String timeStamp) { TimeStamp = timeStamp; }

    /***
     *
     * @param id
     * @param rssi
     *
     * Aggiornamento della Mappa contenente MAC, RSSI. Ovvero aggiunta di un'entry nella mappa RSSI e aggiornamento numero di ESP
     * che hanno ricevuto quel determinato pacchetto.
     */
    public void newSignal(String id,int rssi) {
        setN_ESP(getN_ESP()+1);
        getRSSI().put(id, rssi);
    }
}