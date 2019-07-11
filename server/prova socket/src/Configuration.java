import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Configuration {

    private Map<String, Payload> mac_tab;
    private Double n; //parametro di configurazione che tiene conto dell'ambiente in cui avviene lo sniffing
    /* Potenza del segnale ricevuto alla distanza di riferimento di un metro */
    /* Settato a questo valore dopo varie misurazioni fatte */
    private final Integer Px = -62;
    private int numEsp;


    public Configuration(Double n) {
        this.n = n;
        this.mac_tab = new HashMap<String, Payload>();
    }

    public Map<String, Payload> getMac_tab() {
        return mac_tab;
    }

    public void setMac_tab(Map<String, Payload> mac_tab) {
        this.mac_tab = mac_tab;
    }

    public Integer getPx() {
        return Px;
    }

    public Double getN() {
        return n;
    }

    public void setN(Double n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Double.compare(that.Px, Px) == 0 &&
                mac_tab.equals(that.mac_tab) &&
                n.equals(that.n);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mac_tab, n, Px);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "mac_tab=" + mac_tab +
                ", n=" + n +
                ", Px=" + Px +
                '}';
    }

	public int getNumEsp() {
		return numEsp;
	}

	public void setNumEsp(int numEsp) {
		this.numEsp = numEsp;
	}
}
