import java.util.Objects;

public class Packet {

	private int RSSI;
	private String MacSource;
	private String SSID;
	private String Digest;
	private String TimeStamp;
	private String IdMac;
	private Integer SequenceNumber=null;

	/***
	 *
	 * @param packetChar
	 *
	 * costruttore dell'oggetto Packet crea l'oggetto richiamando i vari metodi estrattori
	 */
	public Packet(String packetChar) {
		extractMac(packetChar);
		extractRssi(packetChar);
		extractSSID(packetChar);
		extractDigest(packetChar);
		extractTimeStamp(packetChar);
		extractIdMac(packetChar);
		//extractSequenceNumber(packetChar);
		System.out.println(this.toString());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Packet packet = (Packet) o;
		return RSSI == packet.RSSI &&
				Objects.equals(MacSource, packet.MacSource) &&
				Objects.equals(SSID, packet.SSID) &&
				Objects.equals(Digest, packet.Digest) &&
				Objects.equals(TimeStamp, packet.TimeStamp) &&
				Objects.equals(IdMac, packet.IdMac) &&
				Objects.equals(SequenceNumber, packet.SequenceNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(RSSI, MacSource, SSID, Digest, TimeStamp, IdMac, SequenceNumber);
	}

	@Override
	public String toString() {
		return "Packet{" +
				"RSSI=" + RSSI +
				", MacSource='" + MacSource + '\'' +
				", SSID='" + SSID + '\'' +
				", Digest='" + Digest + '\'' +
				", TimeStamp='" + TimeStamp + '\'' +
				", IdMac='" + IdMac + '\'' +
				", SequenceNumber=" + SequenceNumber +
				'}';
	}

	public String getIdMac() {
		return IdMac;
	}

	public void setIdMac(String idMac) {
		IdMac = idMac;
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

	public Integer getSequenceNumber() {
		return SequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		SequenceNumber = sequenceNumber;
	}
	/***
	 *
	 * @param packetChar
	 *
	 * estrae dal pacchetto l'RSSI
	 */
	private void extractRssi(String packetChar) {
		String[] begin = packetChar.split("RSSI=");
		String[] rssi = begin[1].split("/"); //split string after RSSI= by "/"
		setRSSI(Integer.parseInt(rssi[0])); //take cahracters from the beginning to /
	}

	/***
	 *
	 * @param packetChar
	 *
	 * estrae dal pacchetto il MAC dell'ESP32
	 */
	private void extractIdMac(String packetChar) {
		String[] begin = packetChar.split("ESP_MAC=");
		String[] idmac = begin[1].split("/");
		setIdMac(idmac[0]); //take cahracters from the beginning to /
	}

	/***
	 *
	 * @param packetChar
	 *
	 * estrae dal pacchetto il MAC source dei dispositivi
	 */
	private void extractMac(String packetChar) {
		String[] begin = packetChar.split("MAC_SRC=");
		String[] mac = begin[1].split("/");
		setMacSource(mac[0]);
	}

	/***
	 *
	 * @param packetChar
	 *
	 * estrae dal pacchetto il SSID
	 */
	private void extractSSID(String packetChar) {
		String[] begin = packetChar.split("SSID_length=");
		String[] ssid_len = begin[1].split("/");
		//System.out.println("SSID LENGHT = " + ssid_len[0]);
		//if(ssid_len[0]!="0"){
		//TODO check if ssid capture is correct!!!!
		if(ssid_len[0].compareTo("0") != 0){
			String[] _ssid = packetChar.split("SSID_=");
			String ssid = _ssid[1].split("/")[0];
			if(ssid!=null){
				setSSID(ssid);
			}
			//setSSID(ssid[0]);
		}
		else
			setSSID(null);
	}
	
	/*
	 * NOTA: FORMAT OF STRING FOR THE DIGEST COMPUTATION:
	 * /MAC_SRC=2c:fd:a1:9c:e2:bb/ TimeStamp=1555064886/
	 */
	/***
	 *
	 * @param packetChar
	 *
	 * estrae dal pacchetto il digest
	 */
	private void extractDigest(String packetChar) {
		String[] begin = packetChar.split("Digest=");
		String[] hash = begin[1].split("/");
		setDigest(hash[0]);
		//System.out.print("Digest: " + getDigest());
	}


	/***
	 *
	 * @param packetChar
	 *
	 * estrae dal pacchetto il timestamp
	 */
	private void extractTimeStamp(String packetChar) {
		String[] begin = packetChar.split("TimeStamp=");
		String[] hash = begin[1].split("/");
		setTimeStamp(hash[0]);
	}

	private void extractSequenceNumber(String packetChar) {
		String[] begin = packetChar.split("Sequence_Number=");
		String[] seq_n = begin[1].split("/");
		setSequenceNumber(Integer.parseInt(seq_n[0]));
	}
	

}
