import java.util.Objects;

public class Packet {

	private int RSSI;
	private String MacSource;
	private String SSID;
	private String Digest;
	private String TimeStamp;
	
	/**
	 * 
	 */
	public Packet(String packetChar) {
		extractMac(packetChar);
		extractRssi(packetChar);
		extractSSID(packetChar);
		extractDigest(packetChar);
		extractTimeStamp(packetChar);
		System.out.println(this.toString());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Packet packet = (Packet) o;
		return RSSI == packet.RSSI &&
				MacSource.equals(packet.MacSource) &&
				SSID.equals(packet.SSID) &&
				Digest.equals(packet.Digest) &&
				TimeStamp.equals(packet.TimeStamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(RSSI, MacSource, SSID, Digest, TimeStamp);
	}

	@Override
	public String toString() {
		return "Packet{" +
				"RSSI=" + RSSI +
				", MacSource='" + MacSource + '\'' +
				", SSID='" + SSID + '\'' +
				", Digest='" + Digest + '\'' +
				", TimeStamp='" + TimeStamp + '\'' +
				'}';
	}

	public int getRSSI() {
		return RSSI;
	}

	public void setRSSI(int RSSI) {
		this.RSSI = RSSI;
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

	private void extractRssi(String packetChar) {
		String[] begin = packetChar.split("RSSI=");
		String[] rssi = begin[1].split("/"); //split string after RSSI= by "/"
		setRSSI(Integer.parseInt(rssi[0])); //take cahracters from the beginning to /
	}
	
	private void extractMac(String packetChar) {
		String[] begin = packetChar.split("MAC_SRC=");
		String[] mac = begin[1].split("/");
		setMacSource(mac[0]);
	}
	
	private void extractSSID(String packetChar) {
		String[] begin = packetChar.split("SSID_length=");
		String[] ssid = begin[1].split("/");
		if(ssid[0]!="0")
			setSSID(ssid[0]);
		else
			setSSID(null);
	}
	
	/*
	 * NOTA: FORMAT OF STRING FOR THE DIGEST COMPUTATION:
	 * /MAC_SRC=2c:fd:a1:9c:e2:bb/ TimeStamp=1555064886/
	 */
	private void extractDigest(String packetChar) {
		String[] begin = packetChar.split("Digest=");
		String[] hash = begin[1].split("/");
		setDigest(hash[0]);
		//System.out.print("Digest: " + getDigest());
	}

	private void extractTimeStamp(String packetChar) {
		String[] begin = packetChar.split("TimeStamp=");
		String[] hash = begin[1].split("/");
		setTimeStamp(hash[0]);
	}
	

}
