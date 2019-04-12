import DB.DBUtil;

import java.sql.SQLException;
import java.util.Random;

public class main {

    public static void main(String[] args) {
        //apro una nuova connessione con il DB
        DBUtil db=new DBUtil();
        if(!db.openConnection()){
            System.err.println("Errore di Connessione al DB. Impossibile Continuare");
            System.exit(-1);
        }
        //input di prova
        Random r=new Random();
        int x=r.nextInt(100);

        try {
            if(!db.aggiungiTupla(x, 25)){
                System.err.println("Errore nell'inserimento");
                System.exit(-1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.closeConnection();

    }

}