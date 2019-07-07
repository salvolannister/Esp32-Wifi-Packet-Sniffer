package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QueryFake {

    private Connection conn;
    public QueryFake(Connection conn) {
        this.conn=conn;
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


    public Map<String, Long> showMac(String timeI, String timeF) throws SQLException {

        PreparedStatement pstmt;
        Map<String, Long> risultato= new HashMap<>();

        try {
            conn.setAutoCommit(false);

            String s=new String("SELECT MAC, count(*) AS val FROM Position WHERE Timestamp >= ? AND Timestamp <= ? GROUP BY MAC ORDER BY count(*) DESC");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1,  timeI);
                pstmt.setString(2, timeF);
                ResultSet res=pstmt.executeQuery();

                while (res.next()){
                        risultato.put(res.getString("MAC"), res.getLong("val"));
                        //System.out.println(res.getString("MAC")+"  "+res.getLong("val"));
                    }


                System.out.println(risultato);

                if(risultato.isEmpty()==false){
                    conn.commit();
                    return risultato;
                }
                return null;


            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return null;
        }

    }

}