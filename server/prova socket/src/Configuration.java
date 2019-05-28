import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Configuration {

    private Map<String, Payload> mac_tab;

    public Configuration() {
        this.mac_tab = new HashMap<String, Payload>();
    }

    public Map<String, Payload> getMac_tab() {
        return mac_tab;
    }

    public void setMac_tab(Map<String, Payload> mac_tab) {
        this.mac_tab = mac_tab;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(mac_tab, that.mac_tab);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mac_tab);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "mac_tab=" + mac_tab +
                '}';
    }
}
