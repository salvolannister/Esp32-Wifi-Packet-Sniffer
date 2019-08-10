package DB;

import application.EspInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryConfiguration {

    private Connection conn;
    public QueryConfiguration(Connection conn) {
        this.conn=conn;
    }

    public boolean addConfiguration( String MAC, String Name, float posX, float posY)  {
        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);

            String s=new String("INSERT INTO Configuration"+ "(MAC, Name, X, Y)"+" VALUES (?, ?, ?,?)");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1, MAC);
                pstmt.setString(2, Name);
                pstmt.setFloat(3, posX);
                pstmt.setFloat(4, posY);
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("couldn't do rollback");

            }
            e.printStackTrace();
            System.out.println("errore nell'inserimento di una configurazione");
            return false;
        }
    }

    /* Reads MAC, X, Y belonging to a determinated Configuration Name */
    public ArrayList<EspInfo> readConfiguration(String name) {
        PreparedStatement pstmt;
        ArrayList<EspInfo> risultato = new ArrayList<>();

        try {
            conn.setAutoCommit(false);

            String s = new String("SELECT MAC, X, Y FROM Configuration WHERE Name = ?");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1, name);
                ResultSet res = pstmt.executeQuery();

                while (res.next()) {
                    EspInfo eI = new EspInfo( res.getFloat("X"), res.getFloat("Y"), res.getString("MAC"));
                    risultato.add(eI);
                    System.out.println("MAC " + eI.getMAC() + "X " + eI.getX() + "Y " + eI.getY());
                }


                if (risultato.isEmpty() == false) {
                    conn.commit();
                    return risultato;
                }
                return null;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<String> getConfNames(){

        PreparedStatement pstmt;
        List<String> risultato = new ArrayList<>();

        try {
            conn.setAutoCommit(false);

            String sql = new String("SELECT Name FROM Configuration GROUP BY NAME");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(sql)) {

                ResultSet res = pstmt.executeQuery();

                while (res.next()) {

                    String name = res.getString("Name");
                    risultato.add(name);
                    //System.out.println("Name " +name);
                }


                if (risultato.isEmpty() == false) {
                    conn.commit();
                    return risultato;
                }
                return null;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
