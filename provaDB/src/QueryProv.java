import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryProv {

    private Connection conn;
    public QueryProv(Connection conn) {
        this.conn=conn;
    }

    public boolean aggiungiTupla(int id, int prova) throws SQLException {

        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);

            String s=new String("INSERT INTO Test"+ "(id, prova)"+" VALUES (?, ?)");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setInt(1, (int) id);
                pstmt.setInt(2, (int) prova);
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
