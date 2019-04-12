package DB;
import java.io.File;
import java.sql.*;

public class DBUtil {

    private Connection conn;

    public DBUtil(){

        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch(Exception e){
            System.err.println("Driver non disponibile");
            e.printStackTrace();
        }
    }

    //apertura della connessione
    public boolean openConnection(){
        try{

            //rendo indipendente il db dalpercorso
            File f=new File(".");
            f.getAbsolutePath();
            String url ="jdbc:sqlite:"+f.getAbsolutePath()+"//prov.db";

            //conenssione al db
            conn=DriverManager.getConnection(url);

            return true;

        } catch(Exception ex){
            ex.printStackTrace();

            return false;
        }

    }

    //funzione di insert
    public boolean aggiungiTupla(int id, int prova) throws SQLException{

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



    /*
     * chiusura della connessione
     */
    public void closeConnection(){
        try {

            conn.close();
        } catch (Exception e) {
            System.err.println("Errore nel chiudere la connessione con il DB!");
        }
    }


}
