/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Demo_Caro;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class MainRoom extends javax.swing.JFrame {

    /**
     * Creates new form MainRoom
     */
    public MainRoom() {
        Object[] col = {"Tên phòng","Người chơi", "Trạng thái"};
        tableModel = new DefaultTableModel(col, 0);
        initComponents();
        try {
            serverListenRoom(Constant.UPDATE_LIST_ROOM);
        } catch (IOException ex) {
            Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void serverListenRoom(String action) throws IOException{
        //Tạo kết nối
        Socket connectSocket = new Socket("adminpc",9999);
        //Nếu chọn tạo phòng
        if(action.equals(Constant.CREATE_ROOM)){
            try {
                //Nếu như tạo phòng
                Hashtable messages = new Hashtable();
                messages.put(Constant.CREATE_ROOM, true);
                messages.put(Constant.USERNAME,"Duong Jerry");
                messages.put(Constant.ROOM_TITLE, roomTitle);
                //Gửi IP
                String ip = Inet4Address.getLocalHost().getHostAddress();
                messages.put(Constant.GET_IP, ip);
                outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
                outToServer.writeObject(messages);
                //Đởi phản hồi từ Server
                inFromServer = new ObjectInputStream(connectSocket.getInputStream());
                Hashtable values = (Hashtable)inFromServer.readObject();
                if(values.containsKey(Constant.SERVERREPLY)){
                    this.setVisible(false);
                    int port = (int)values.get(Constant.SERVERREPLY);
                    CaroServer caro = new CaroServer(ip,port);
                    caro.setVisible(true);
                }
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Nếu chọn tìm phòng chơi
        if(action.equals(Constant.GET_ROOM)){
            try {
                //Nếu như tạo phòng
                Hashtable messages = new Hashtable();
                messages.put(Constant.GET_ROOM, true);
                outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
                outToServer.writeObject(messages);
                //Đởi phản hồi từ Server
                inFromServer = new ObjectInputStream(connectSocket.getInputStream());
                Hashtable values = new Hashtable();
                values = (Hashtable)inFromServer.readObject();
                if(values.containsKey(Constant.SERVERREPLY)){
                    if(!values.containsKey(Constant.GET_ROOM)){
                        JOptionPane.showMessageDialog(this,"Không còn phòng nào");
                    }else{
                        this.setVisible(false);
                        Room room = (Room)values.get(Constant.GET_ROOM);
                        int port = room.get_ID();
                        String ip = room.get_IP();
                        CaroClient caro = new CaroClient(ip,port);
                        caro.setVisible(true);
                    }
                }
                
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Nếu chọn chơi phòng đang chọn
        if(action.equals(Constant.GET_ROOM_AT)){
            try {
                //Nếu như tạo phòng
                Hashtable messages = new Hashtable();
                messages.put(Constant.GET_ROOM_AT, roomTable.getSelectedRow());
                outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
                outToServer.writeObject(messages);
                //Đởi phản hồi từ Server
                inFromServer = new ObjectInputStream(connectSocket.getInputStream());
                Hashtable values = new Hashtable();
                values = (Hashtable)inFromServer.readObject();
                if(values.containsKey(Constant.SERVERREPLY)){
                    if(!values.containsKey(Constant.GET_ROOM_AT)){
                        JOptionPane.showMessageDialog(this,"Phòng đang chơi");
                    }else{
                        this.setVisible(false);
                        Room room = (Room)values.get(Constant.GET_ROOM_AT);
                        int port = room.get_ID();
                        String ip = room.get_IP();
                        CaroClient caro = new CaroClient(ip,port);
                        caro.setVisible(true);
                    }
                }
                
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Nếu yêu cầu cập nhật danh sách phòng chơi
        if(action.equals(Constant.UPDATE_LIST_ROOM)){
            try {
                //Nếu như tạo phòng
                Hashtable messages = new Hashtable();
                messages.put(Constant.UPDATE_LIST_ROOM, true);
                outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
                outToServer.writeObject(messages);
                //Đởi phản hồi từ Server
                inFromServer = new ObjectInputStream(connectSocket.getInputStream());
                ArrayList<Room> Rooms = (ArrayList<Room>)inFromServer.readObject();
                for(Room room : Rooms){
                    String title = room.get_Title();
                    String username = room.get_Username();
                    String state = "";
                    if(room.get_State()){
                        state = "Đang chơi";
                    }else{
                        state = "Phòng đang chờ";
                    }
                    Object[] data = {title,username,state};
                    tableModel.addRow(data);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        roomPanel = new javax.swing.JScrollPane();
        roomTable = new javax.swing.JTable();
        controlPanel = new javax.swing.JPanel();
        createRoomButton = new javax.swing.JButton();
        findRoomButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        actionMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Caro Game");

        mainPanel.setBackground(new java.awt.Color(51, 0, 255));
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        roomPanel.setPreferredSize(new java.awt.Dimension(100, 300));

        roomTable.setModel(tableModel);
        roomPanel.setViewportView(roomTable);

        mainPanel.add(roomPanel);

        controlPanel.setPreferredSize(new java.awt.Dimension(600, 100));
        controlPanel.setLayout(new java.awt.GridLayout(1, 0));

        createRoomButton.setText("Tạo phòng");
        createRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRoomButtonActionPerformed(evt);
            }
        });
        controlPanel.add(createRoomButton);

        findRoomButton.setText("Chơi");
        findRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findRoomButtonActionPerformed(evt);
            }
        });
        controlPanel.add(findRoomButton);

        mainPanel.add(controlPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");
        menuBar.add(fileMenu);

        actionMenu.setText("Action");
        menuBar.add(actionMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRoomButtonActionPerformed
        try {
            // TODO add your handling code here:
            roomTitle = JOptionPane.showInputDialog(this,"Tên phòng");
            serverListenRoom(Constant.CREATE_ROOM);
        } catch (IOException ex) {
            Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_createRoomButtonActionPerformed

    private void findRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findRoomButtonActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            if(roomTable.getSelectedRow() != -1){
                serverListenRoom(Constant.GET_ROOM_AT);
            }else{
                serverListenRoom(Constant.GET_ROOM);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_findRoomButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainRoom().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu actionMenu;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton createRoomButton;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton findRoomButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JScrollPane roomPanel;
    private javax.swing.JTable roomTable;
    // End of variables declaration//GEN-END:variables
    private String roomTitle;
    private String userName = "Duong Jerry";
    private ObjectOutputStream outToServer;
    private ObjectInputStream inFromServer;
    private DefaultTableModel tableModel;

}
