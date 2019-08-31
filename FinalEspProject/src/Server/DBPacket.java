package Server;
import java.util.Objects;

public class DBPacket {

  private String digest;
  private Long TimeStamp;
  private String Room;
  private float posX;
  private float posY;
  private String MacSource;
  private Float Err;
  private Integer LocalMacMargedNumber;
  private String SSID;
  private Integer SequenceNumber;

    public DBPacket(String digest, Long timeStamp, String room, float posX, float posY, String macSource, Float err, Integer localMacMargedNumber, String SSID, Integer sequenceNumber) {
        this.digest = digest;
        TimeStamp = timeStamp;
        Room = room;
        this.posX = posX;
        this.posY = posY;
        MacSource = macSource;
        Err = err;
        LocalMacMargedNumber = localMacMargedNumber;
        this.SSID = SSID;
        SequenceNumber = sequenceNumber;
    }

    public DBPacket(String digest, Long timeStamp, String room, float posX, float posY, String macSource, String ssid, Integer sequenceNumber) {
        this.digest = digest;
        TimeStamp = timeStamp;
        Room = room;
        this.posX = posX;
        this.posY = posY;
        MacSource = macSource;
        SSID = ssid;
        SequenceNumber = sequenceNumber;
        Err = 0f;
        LocalMacMargedNumber = 0;
    }

    public DBPacket(String digest, Long timeStamp, String room, float posX, float posY, String macSource) {
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

    public Integer getSequenceNumber() {
        return SequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        SequenceNumber = sequenceNumber;
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

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
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
                ", Room='" + Room + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", MacSource='" + MacSource + '\'' +
                ", Err=" + Err +
                ", LocalMacMargedNumber=" + LocalMacMargedNumber +
                ", SSID='" + SSID + '\'' +
                ", SequenceNumber=" + SequenceNumber +
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
                Objects.equals(SSID, dbPacket.SSID) &&
                Objects.equals(SequenceNumber, dbPacket.SequenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digest, TimeStamp, Room, posX, posY, MacSource, Err, LocalMacMargedNumber, SSID, SequenceNumber);
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }


    public boolean checkArea(float roomX, float roomY) {
        if(posX>=0 && posY>=0 && posX<=roomX && posY<=roomY)
            return true;
    return false;
    }
}
