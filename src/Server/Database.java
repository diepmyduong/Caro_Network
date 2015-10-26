/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class Database {
    private Connection con;
    
    public Database(){
        try {
            Class.forName(Constant.SQLSERVER_DRIVER);
            con = DriverManager.getConnection(Constant.DATABASE_CONNECT_SQLSERVER_URL);
            System.out.println("Connect Success");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    //Thực hiện câu truy vấn và trả về kết quả
    public ResultSet query(String sql) throws SQLException {
        System.out.println("Query : "+sql);
        Statement sta = con.createStatement();
        return sta.executeQuery(sql);
    }
    public int execute(String sql) throws SQLException{
        System.out.println("Query : "+sql);
        Statement sta = con.createStatement();
        return sta.executeUpdate(sql);
    }
    public void disConnect() throws SQLException{
        con.close();
    }
    @Override
    protected void finalize() throws Throwable {
            if (con != null && !con.isClosed()) {
                    con.close();
            }
    }
}
