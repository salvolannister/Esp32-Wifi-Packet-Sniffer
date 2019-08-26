package Server;
import java.util.Objects;

public class Payload {
    private Long lastTime;
    private Polo posizione;

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public Polo getPosizione() {
        return posizione;
    }

    public void setPosizione(Polo posizione) {
        this.posizione = posizione;
    }

    public Payload(Long lastTime, Polo posizione) {
        this.lastTime = lastTime;
        this.posizione = posizione;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payload payload = (Payload) o;
        return lastTime.equals(payload.lastTime) &&
                posizione.equals(payload.posizione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastTime, posizione);
    }

    @Override
    public String toString() {
        return "Payload{" +
                "lastTime=" + lastTime +
                ", posizione=" + posizione +
                '}';
    }
}
