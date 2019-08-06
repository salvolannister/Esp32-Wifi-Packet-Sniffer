package DB;

import DTO.Polo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class QueryRoom {
    private Connection conn;
    public QueryRoom(Connection conn) {
        this.conn=conn;
    }

    public boolean addRoom(String Name, float X, float Y) throws SQLException {
        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);

            String s=new String("INSERT INTO Room"+ "(Name, X, Y)"+" VALUES (?, ?, ?)");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1, Name);
                pstmt.setFloat(2, X);
                pstmt.setFloat(3, Y);
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

    public boolean checkRoomExistence(String Name) throws SQLException{
        String sql = "SELECT COUNT(*) as total FROM Room WHERE Name = ?;";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, Name);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(  resultSet.getInt("total")!= 0 )
            return true;
        else
            return false;
    }

    public String[] getRoomName() throws SQLException {
        String[] rooms = new String[];
        String sql = "SELECT Name FROM Room ";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        int i = 0;
        ResultSet resultSet = preparedStatement.executeQuery();

        /* is Before first check if the pointer is before first,
        if it is this means there is data in
        result set
         */

        if (!resultSet.isBeforeFirst() ) {
            System.out.println("No data in Room");
            return null;
        }
        while(resultSet.next()) {
          rooms[i] = resultSet.getString(0);
          i++;
      }


        return rooms;
    }

    public ArrayList[] getRoomDim(String Name){
        
    }

}
