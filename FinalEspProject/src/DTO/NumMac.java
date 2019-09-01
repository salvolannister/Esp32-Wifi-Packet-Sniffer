package DTO;

import java.util.Objects;

public class NumMac {
    private String MAC;
    private Long freq;

    public NumMac(String MAC, Long freq) {
        this.MAC = MAC;
        this.freq = freq;
    }

    public String getMAC() {
        return MAC;
    }

    @Override
    public String toString() {
        return "NumMac{" +
                "MAC='" + MAC + '\'' +
                ", freq=" + freq +
                '}';
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public Long getFreq() {
        return freq;
    }

    public void setFreq(Long freq) {
        this.freq = freq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumMac numMac = (NumMac) o;
        return MAC.equals(numMac.MAC) &&
                freq.equals(numMac.freq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(MAC, freq);
    }
}
