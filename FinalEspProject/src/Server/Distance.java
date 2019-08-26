package Server;
import java.util.Objects;

public class Distance {
    private Polo posizione;
    private Integer rssi;

    /**
     * Distance rappresenta, dato un pacchetto, la posizione della schedina che lo ha catturato e
     * il valore di RSSI rivelato dalla stessa per quel pacchetto specifico.
     * @param posizione
     * @param rssi
     */
    public Distance(Polo posizione, Integer rssi) {
        this.posizione = posizione;
        this.rssi = rssi;
    }

    public Polo getPosizione() {
        return posizione;
    }

    public void setPosizione(Polo posizione) {
        this.posizione = posizione;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return posizione.equals(distance.posizione) &&
                rssi.equals(distance.rssi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(posizione, rssi);
    }

    @Override
    public String toString() {
        return "Distance{" +
                "posizione=" + posizione +
                ", rssi=" + rssi +
                '}';
    }

    /***
     * Converte l'RSSI in distanza
     * @param conf
     * @return
     */

    public double getDistance(Configuration conf){

        double x = (conf.getPx() - rssi)/(10 * conf.getN());
        double distance = Math.pow(10,x);
        // DEBUG System.out.println("RSSI value: " + rssi + " while computed distance is: " + distance);
        return distance;

    }
}
