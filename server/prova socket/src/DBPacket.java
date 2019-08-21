import java.util.Objects;

public class DBPacket {

  private String digest;
  private Long TimeStamp;
  private Integer Room;
  private float posX;
  private float posY;
  private String MacSource;
  private Float Err;
  private Integer LocalMacMargedNumber;
  private String SSID;

    public DBPacket(String digest, Long timeStamp, Integer room, float posX, float posY, String macSource, String ssid) {
        this.digest = digest;
        TimeStamp = timeStamp;
        Room = room;
        this.posX = posX;
        this.posY = posY;
        MacSource = macSource;
        SSID = ssid;
        Err = 0f;
        LocalMacMargedNumber = 0;
    }

    public DBPacket(String digest, Long timeStamp, Integer room, float posX, float posY, String macSource) {
        this.digest = digest;
        TimeStamp = timeStamp;
        Room = room;
        this.posX = posX;
        this.posY = posY;
        MacSource = macSource;
        SSID = null;
        Err = 0f;
        LocalMacMargedNumber = 0;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public Float getErr() {
        return Err;
    }

    public void setErr(Float err) {
        Err = err;
    }

    public Integer getLocalMacMargedNumber() {
        return LocalMacMargedNumber;
    }

    public void setLocalMacMargedNumber(Integer localMacMargedNumber) {
        LocalMacMargedNumber = localMacMargedNumber;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    public Integer getRoom() {
        return Room;
    }

    public void setRoom(Integer room) {
        Room = room;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public String getMacSource() {
        return MacSource;
    }

    public void setMacSource(String macSource) {
        MacSource = macSource;
    }

    @Override
    public String toString() {
        return "DBPacket{" +
                "digest='" + digest + '\'' +
                ", TimeStamp=" + TimeStamp +
                ", Room=" + Room +
                ", posX=" + posX +
                ", posY=" + posY +
                ", MacSource='" + MacSource + '\'' +
                ", Err=" + Err +
                ", LocalMacMargedNumber=" + LocalMacMargedNumber +
                ", SSID='" + SSID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBPacket dbPacket = (DBPacket) o;
        return Float.compare(dbPacket.posX, posX) == 0 &&
                Float.compare(dbPacket.posY, posY) == 0 &&
                Objects.equals(digest, dbPacket.digest) &&
                Objects.equals(TimeStamp, dbPacket.TimeStamp) &&
                Objects.equals(Room, dbPacket.Room) &&
                Objects.equals(MacSource, dbPacket.MacSource) &&
                Objects.equals(Err, dbPacket.Err) &&
                Objects.equals(LocalMacMargedNumber, dbPacket.LocalMacMargedNumber) &&
                Objects.equals(SSID, dbPacket.SSID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digest, TimeStamp, Room, posX, posY, MacSource, Err, LocalMacMargedNumber, SSID);
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

}
