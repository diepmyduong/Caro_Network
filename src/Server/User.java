/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class User implements Serializable{
    private String username;
    private String password;
    private boolean statement;
    
    public User(){
        
    }
    
    public User(String username,String password){
        this.username = username;
        this.password = password;
        this.statement = false;
    }
    public User(String username,String password,boolean statement){
        this.username = username;
        this.password = password;
        this.statement = statement;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public boolean getStatement(){
        return this.statement;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public void setStatment(boolean statement){
        this.statement = statement;
    }
    
    //duyệt tất cả người dùng
    public static ArrayList<User> all(){
        try {
            Database db = new Database();
            String query = "Select * from users";
            ResultSet rs = db.query(query);
            ArrayList<User> users = new ArrayList<User>();
            while(rs.next()){
                String username = rs.getString("username");
                String password = rs.getString("password");
                boolean statement = Boolean.parseBoolean(rs.getString("statement"));
                users.add(new User(username,password,statement));
            }
            db.disConnect();
            return users;
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    //tìm kiếm theo username
    public static User find(String string){
        try {
            Database db = new Database();
            String query = "SELECT * FROM `users` WHERE `username` = '"+string+"'";
            ResultSet rs = db.query(query);
            rs.next();
            String username = rs.getString("username");
            String password = rs.getString("password");
            boolean statement = Boolean.parseBoolean(rs.getString("statement"));
            db.disConnect();
            return new User(username,password,statement);
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public int save(){
        try {
            Database db = new Database();
            String query = "INSERT INTO `users`(`username`, `password`, `statement`) VALUES ('"+this.getUsername()+"','"+this.password+"',"+this.statement+")";
            int rs = db.execute(query);
            System.out.println(rs);
            db.disConnect();
            return rs;
        } catch (SQLException ex) {
            System.out.println(ex.getErrorCode());
            System.out.println(ex.getSQLState());
            System.out.println(ex.getMessage());
            return ex.getErrorCode();
//            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int update(){
        try {
            Database db = new Database();
            String query = "UPDATE `users` SET `password`='"+this.getPassword()+"',`statement`="+this.getStatement()+" WHERE `username` = '"+this.getUsername()+"'";
            int rs = db.execute(query);
            System.out.println(rs);
            db.disConnect();
            return rs;
        } catch (SQLException ex) {
            System.out.println(ex.getErrorCode());
            System.out.println(ex.getSQLState());
            System.out.println(ex.getMessage());
            return ex.getErrorCode();
//            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int delete(String string){
        try {
            Database db = new Database();
            String query = "DELETE FROM `users` WHERE `username` = '"+string+"'";
            int rs = db.execute(query);
            System.out.println(rs);
            db.disConnect();
            return rs;
        } catch (SQLException ex) {
            System.out.println(ex.getErrorCode());
            System.out.println(ex.getSQLState());
            System.out.println(ex.getMessage());
            return ex.getErrorCode();
//            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public boolean equals(Object obj){

        User other = (User) obj;
        return this.username.equals(other.getUsername()) && this.password.equals(other.getPassword()) && this.statement == other.getStatement();
    }
}
