package DB;
import java.io.File;
import java.sql.*;

public class DBUtil {

    private Connection conn;


    public Connection getConn() {
        return conn;
    }

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
    public boolean openConnection(String DbName){
        try{

            //rendo indipendente il db dalpercorso
            File f=new File(".");
            String path = f.getAbsolutePath();

            String url ="jdbc:sqlite:"+f.getAbsolutePath() +"//"+DbName;
           // url = url.replaceFirst("\\.","");
           System.out.println("URL "+url);
            //conenssione al db
            conn=DriverManager.getConnection(url);

            return true;

        } catch(Exception ex){
            ex.printStackTrace();

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
