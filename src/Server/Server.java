/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Demo_Caro.Room;
import com.sun.corba.se.impl.io.InputStreamHook;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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
        class ListenRoom extends Thread {

            public ListenRoom() {
                start();
            }

            @Override
            public void run() {
                try {
                    ClientRoomListen();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        new ListenLogin();
        new ListenRoom();
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
    
    private void ClientRoomListen() throws IOException{
        //Tạo server
        ServerSocket serverRoom = new ServerSocket(9999);
        int port = 1000;
        //Vòng lặp chính
        while(true){
            try {
                //Chờ kết nới và chấp nhận kết nối
                Socket connectSocket = serverRoom.accept();
                
                //Tạo luồng dữ liệu nhập xuất
                outToClientRoom = new ObjectOutputStream(connectSocket.getOutputStream());
                inFromClientRoom = new ObjectInputStream(connectSocket.getInputStream());
                
                Hashtable values = (Hashtable)inFromClientRoom.readObject();
                //Nếu client yêu cầu tạo phòng
                if(values.containsKey(Constant.CREATE_ROOM)){
                    System.out.println("Yêu cầu tạo phòng");
                    String username = (String)values.get(Constant.USERNAME);
                    String title = (String)values.get(Constant.ROOM_TITLE);
                    String clientIP = (String)values.get(Constant.GET_IP);
                    //Tạo 1 phòng chờ
                    Room room = new Room(port, title , username, false, clientIP);
                    Rooms.add(room);
                    Hashtable messages = new Hashtable();
                    messages.put(Constant.SERVERREPLY, port);
                    outToClientRoom.writeObject(messages); // Gửi room vừa tào cho Client
                    port += 2; // Tăng port lên 2 vì user sẽ dử dụng 2 port
                }
                //Nếu client yêu cầu tìm phòng chơi
                if(values.containsKey(Constant.GET_ROOM)){
                    System.out.println("Yêu cầu tìm phòng chơi");
                    //Tạo 1 phòng chờ
                    Hashtable messages = new Hashtable();
                    messages.put(Constant.SERVERREPLY,true);
                    for(Room room : Rooms){
                        //Tìm phòng đang chờ
                        if(!room.get_State()){ // state = false : Chưa có người chơi
                            room.set_State(true);
                            //Gửi cho người chơi thông tin room
                            System.out.println(room.get_ID());
                            messages.put(Constant.GET_ROOM,room);
                            break;
                        }
                    }
                    outToClientRoom.writeObject(messages);
                }
                //Nếu client yêu cầu tìm phòng chơi tại phòng đã chọn
                if(values.containsKey(Constant.GET_ROOM_AT)){
                    System.out.println("Yêu cầu tìm phòng chơi đã chọn");
                    //Tạo 1 phòng chờ
                    Hashtable messages = new Hashtable();
                    messages.put(Constant.SERVERREPLY,true);
                    int index = (int)values.get(Constant.GET_ROOM_AT);
                    Room room = Rooms.get(index);
                    if(!room.get_State()){
                        room.set_State(true);
                        messages.put(Constant.GET_ROOM_AT, room);
                    }
                    outToClientRoom.writeObject(messages);
                }
                //Nếu client yêu cầu cập nhật phòng chơi
                if(values.containsKey(Constant.UPDATE_LIST_ROOM)){
                    System.out.println("Yêu cầu cập nhật phòng chơi");
                    outToClientRoom.writeObject(Rooms);
                }
                //Nếu client thoát khỏi phòng chơi do người khác tạo
                if(values.containsKey(Constant.CLIENT_EXIT_ROOM)){
                    System.out.println("Thoát khỏi phòng chơi");
                    int roomID = (int)values.get(Constant.CLIENT_EXIT_ROOM);
                    for(Room room: Rooms){
                        if(roomID == room.get_ID()){
                            if(room.get_State()){
                               room.set_State(false);
                                break; 
                            }else{
                                Rooms.remove(room);
                                break;
                            }
                        }
                    }
                    outToClientRoom.writeObject(true);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private ObjectOutputStream outToClient; //Phản hồi Đăng Nhập
    private ObjectInputStream inFromClient; //Tiếp nhận thông tin đăng nhập
    private ObjectOutputStream outToClientRoom; //Phản hổi yêu cầu phòng Caro
    private ObjectInputStream inFromClientRoom; //Tiếp nhận thông tin room
    private String userName = "admin";
    private String passWord = "admin";
    private boolean isUserLogin = false;
    private ArrayList<Room> Rooms = new ArrayList<Room>();
    
}
