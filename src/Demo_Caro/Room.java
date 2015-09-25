/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Demo_Caro;
import java.io.*;

/**
 *
 * @author Admin
 */
public class Room implements Serializable{
    private int id;
    private String Title;
    private String username;
    private boolean statetus; // false là đang chờ // true là đang chơi
    private String ip;
    
    public Room(int id, String title, String username, boolean status, String ip){
        this.id = id;
        this.username = username;
        this.Title = title;
        this.statetus = status;
        this.ip = ip;
    }
    
    public int get_ID(){
        return this.id;
    }
    public String get_Title(){
        return this.Title;
    }
    public String get_Username(){
        return this.username;
    }
    public boolean get_State(){
        return this.statetus;
    }
    public String get_IP(){
        return this.ip;
    }
    
    public void set_Title(String title){
        this.Title = title;
    }
    public void set_Username(String name){
        this.username = name;
    }
    public void set_State(boolean state){
        this.statetus = state;
    }
    public void set_IP(String ip){
        this.ip = ip;
    }
}
