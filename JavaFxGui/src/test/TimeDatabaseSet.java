package test;

import DB.DBUtil;
import DB.QueryFake;
import DB.QueryRoom;
import DTO.Polo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class TimeDatabaseSet {

    public static void main(String[] args) throws SQLException {
        DBUtil db = new DBUtil();
        if(!db.openConnection("prova.db")){
            System.err.println("Errore di Connessione al DB. Impossibile Continuare");
            System.exit(-1);
        }

        QueryFake p=new QueryFake(db.getConn());

       /* voglio leggere nel database
       il campo timestamp in un formato comprensibile

        */

       p.printTable();
    }
}
