package DTO;
import java.util.Objects;

public class Posizione {
    private Double x;
    private Double y;
    private String MAC;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getMAC(){ return MAC;}

    public Posizione(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Posizione(double x, double y, String MAC) {
        this.x = x;
        this.y = y;
        this.MAC = MAC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posizione posizione = (Posizione) o;
        return Double.compare(posizione.x, x) == 0 &&
                Double.compare(posizione.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Polo{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
