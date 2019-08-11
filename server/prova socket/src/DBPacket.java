import java.util.Objects;

public class DBPacket {

  private String digest;
  private Long TimeStamp;
  private Integer Room;
  private float posX;
  private float posY;



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

    @Override
    public String toString() {
        return "DBPacket{" +
                "digest='" + digest + '\'' +
                ", TimeStamp=" + TimeStamp +
                ", Room=" + Room +
                ", posX=" + posX +
                ", posY=" + posY +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBPacket dbPacket = (DBPacket) o;
        return Float.compare(dbPacket.posX, posX) == 0 &&
                Float.compare(dbPacket.posY, posY) == 0 &&
                digest.equals(dbPacket.digest) &&
                TimeStamp.equals(dbPacket.TimeStamp) &&
                Room.equals(dbPacket.Room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digest, TimeStamp, Room, posX, posY);
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public DBPacket(String digest, Long timeStamp, Integer room, float posX, float posY) {
        this.digest = digest;
        TimeStamp = timeStamp;
        Room = room;
        this.posX = posX;
        this.posY = posY;
    }
}
