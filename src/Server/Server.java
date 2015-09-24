/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import com.sun.corba.se.impl.io.InputStreamHook;
import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class Server {
    public static void main(String[] args){
        new Server();
    }
    public Server(){
        System.out.println("Server Started.....");
        class ListenLogin extends Thread {

            public ListenLogin() {
                start();
            }

            @Override
            public void run() {
                try {
                    clientLoginListen();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        new ListenLogin();
    }
    
    private void clientLoginListen() throws IOException{
        //Tạo server
        ServerSocket serverSocket = new ServerSocket(8888);
        //Vòng lặp chính
        while(true){
            try {
                //Chờ kết nối
                System.out.println("Đang chờ kết nối...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Có kết nối...");
                //Tạo luồng dữ liệu nhập xuất
                outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                inFromClient = new ObjectInputStream(clientSocket.getInputStream());
                
                Hashtable values = (Hashtable)inFromClient.readObject();
                System.out.println("nhận được thông tin");
                //Client đăng nhập
                if(values.containsKey(Constant.ISLOGIN)){
                    //Kiểm tra thông tin đăng nhập
                    System.out.println("Đặng nhập");
                    if(userName.equals((String)values.get(Constant.USERNAME))){
                        if(passWord.equals((String)values.get(Constant.PASSWORD))){
                            //Nếu thông tin đúng thì chạy 
                            
                            Hashtable messages = new Hashtable();
                            messages.put(Constant.SERVERREPLY,Constant.LOGINSUCCESS);
                            outToClient.writeObject(messages);
                            isUserLogin = true; // Đăng nhập
                        }else{
                            Hashtable messages = new Hashtable();
                            messages.put(Constant.SERVERREPLY,Constant.PASSWORD_FAIL);
                            outToClient.writeObject(messages);
                        }
                    }else{
                        Hashtable messages = new Hashtable();
                        messages.put(Constant.SERVERREPLY,Constant.USERNAME_FAIL);
                        outToClient.writeObject(messages);
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    
    private ObjectOutputStream outToClient;
    private ObjectInputStream inFromClient;
    private String userName = "admin";
    private String passWord = "admin";
    private boolean isUserLogin = false;
    
}
