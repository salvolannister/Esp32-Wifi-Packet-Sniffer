


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
		System.out.println("");
	}

	@Override
	public String toString() {
		return "Packet{" +
				"RSSI=" + RSSI +
				", MacSource='" + MacSource + '\'' +
				", SSID='" + SSID + '\'' +
				", Digest='" + Digest + '\'' +
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
		System.out.print("RSSI: " + getRSSI() + "; ");
	}
	
	private void extractMac(String packetChar) {
		String[] begin = packetChar.split("MAC_SRC=");
		String[] mac = begin[1].split("/");
		setMacSource(mac[0]);
		System.out.print("Mac Source: " + getMacSource() + "; ");
	}
	
	private void extractSSID(String packetChar) {
		String[] begin = packetChar.split("SSID_length=");
		String[] ssid = begin[1].split("/");
		if(ssid[0]!="0")
			setSSID(ssid[0]);
		else
			setSSID(null);
		System.out.print("SSID: " + getSSID() + "; ");
	}
	
	private void extractDigest(String packetChar) {
		String[] begin = packetChar.split("Digest=");
		String[] hash = begin[1].split("/");
		setDigest(hash[0]);
		System.out.print("Digest: " + getDigest());
	}

	private void extractTimeStamp(String packetChar) {
		String[] begin = packetChar.split("TimeStamp=");
		String[] hash = begin[1].split("/");
		setTimeStamp(hash[0]);
		System.out.print("TimeStamp: " + getTimeStamp());
	}
	

}
