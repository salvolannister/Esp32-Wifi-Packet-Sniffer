import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryFake {

    private Connection conn;
    public QueryFake(Connection conn) {
        this.conn=conn;
    }


    public boolean dropDB() throws SQLException {
        try{
            conn.setAutoCommit(false);
            String s=new String("DROP TABLE \"Position\"");
            try (PreparedStatement pstmt = conn.prepareStatement(s)) {
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return false;

        }
    }

    public boolean createDB() throws SQLException {
        try{
            conn.setAutoCommit(false);
            String s=new String("CREATE TABLE \"Position\" ( \"MAC\" TEXT, \"Timestamp\" INTEGER, \"X\" REAL, \"Y\" REAL, \"Room\" INTEGER, \"Hash\" REAL, PRIMARY KEY(\"Hash\"))" );
            try (PreparedStatement pstmt = conn.prepareStatement(s)) {
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return false;

        }
    }



    public boolean aggiungiTupla(String hash,String mac, long time,  int room, Float posX, Float posY) throws SQLException {

        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);

            String s=new String("INSERT INTO Position"+ "(MAC, Timestamp, Room, X, Y, Hash)"+" VALUES (?, ?, ?, ?, ?, ?)");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(6, hash );
                pstmt.setString(1, mac );
                pstmt.setLong(2, time);
                pstmt.setInt(3, room);
                pstmt.setFloat(4, posX);
                pstmt.setFloat(5, posY);
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return false;
        }

    }



}