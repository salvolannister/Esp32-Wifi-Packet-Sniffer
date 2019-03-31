import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
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


    /*
        aggiornamento della lista di rssi
     */
    public void newSignal(int rssi) {
        setN_ESP(getN_ESP()+1);
        getRSSI().add(rssi);
    }
}