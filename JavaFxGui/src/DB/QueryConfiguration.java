package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
