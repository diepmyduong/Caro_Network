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
    public MainRoom(String username) {
        Object[] col = {"Tên phòng","Người chơi", "Trạng thái"};
        tableModel = new DefaultTableModel(col, 0);
        this.userName = username;
        initComponents();
        updateListRoom();
    }
    
    public void updateListRoom(){
        if(tableModel.getRowCount() > 0){
            for(int i = tableModel.getRowCount() -1; i > -1; i--){
                tableModel.removeRow(i);
            }
        }
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
                messages.put(Constant.USERNAME,userName);
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
                    caroServer = new CaroServer(ip,port,userName,this);
                    caroServer.setVisible(true);
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
                        String serverName = room.get_Username();
                        caroClient = new CaroClient(ip,port,userName,serverName,this);
                        caroClient.setVisible(true);
                    }
                }
                updateListRoom();
                
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
                        String serverName = room.get_Username();
                        caroClient = new CaroClient(ip,port,userName,serverName,this);
                        caroClient.setVisible(true);
                    }
                    updateListRoom();
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
        //Nếu người dùng thoát khỏi phòng chơi mà không phải là người tạo phòng
        if(action.equals(Constant.CLIENT_EXIT_ROOM)){
            try {
                //Nếu như tạo phòng
                Hashtable messages = new Hashtable();
                messages.put(Constant.CLIENT_EXIT_ROOM, currentRoom);
                outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
                outToServer.writeObject(messages);
                //Đởi phản hồi từ Server
                inFromServer = new ObjectInputStream(connectSocket.getInputStream());
                if((boolean)inFromServer.readObject()){
                    updateListRoom();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Nếu người dùng thoát khỏi Game
        if(action.equals(Constant.ISEXIT)){
            try {
                Hashtable messages = new Hashtable();
                messages.put(Constant.ISEXIT, true);
                messages.put(Constant.USERNAME, userName);
                outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
                outToServer.writeObject(messages);
                //Đởi phản hồi từ Server
                inFromServer = new ObjectInputStream(connectSocket.getInputStream());
                if((boolean)inFromServer.readObject()){
                    System.exit(0);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        //Nếu người dùng tìm kiếm người chơi
        if(action.equals(Constant.FIND_USER)){
            try {
                Hashtable messages = new Hashtable();
                messages.put(Constant.FIND_USER, true);
                messages.put(Constant.USERNAME, competitorName);
                outToServer = new ObjectOutputStream(connectSocket.getOutputStream());
                outToServer.writeObject(messages);
                //Đởi phản hồi từ Server
                inFromServer = new ObjectInputStream(connectSocket.getInputStream());
                Hashtable values = new Hashtable();
                values = (Hashtable)inFromServer.readObject();
                if(values.containsKey(Constant.SERVERREPLY)){
                    if(values.containsKey(Constant.IS_ROOM_BUSY)){
                        if((boolean)values.get(Constant.IS_ROOM_BUSY)){
                            JOptionPane.showMessageDialog(this,"Phòng đang chơi");
                        }else{
                            this.setVisible(false);
                            Room room = (Room)values.get(Constant.GET_ROOM_AT);
                            int port = room.get_ID();
                            String ip = room.get_IP();
                            String serverName = room.get_Username();
                            caroClient = new CaroClient(ip,port,userName,serverName,this);
                            caroClient.setVisible(true);
                        }
                    }else{
                        JOptionPane.showMessageDialog(this,"Chưa có phòng của người dùng này");
                    }
                    updateListRoom();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    public void clientExitRoom(int Room_ID){
        try {
            currentRoom = Room_ID;
            serverListenRoom(Constant.CLIENT_EXIT_ROOM);
        } catch (IOException ex) {
            Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
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
        refreshButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        actionMenu = new javax.swing.JMenu();
        createMenuItem = new javax.swing.JMenuItem();
        findUserMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Caro Game");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

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

        refreshButton.setText("Tải lại");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        controlPanel.add(refreshButton);

        mainPanel.add(controlPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");

        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        actionMenu.setText("Action");

        createMenuItem.setText("Tạo phòng");
        createMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMenuItemActionPerformed(evt);
            }
        });
        actionMenu.add(createMenuItem);

        findUserMenuItem.setText("Tìm người chơi");
        findUserMenuItem.setToolTipText("");
        findUserMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findUserMenuItemActionPerformed(evt);
            }
        });
        actionMenu.add(findUserMenuItem);

        menuBar.add(actionMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRoomButtonActionPerformed
        createRoom();
    }//GEN-LAST:event_createRoomButtonActionPerformed

    private void createRoom(){
        try {
            // TODO add your handling code here:
            roomTitle = JOptionPane.showInputDialog(this,"Tên phòng");
            if(roomTitle == null || (roomTitle != null && ("".equals(roomTitle))))   
            {
                return;
            }
            serverListenRoom(Constant.CREATE_ROOM);
        } catch (IOException ex) {
            Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
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

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        updateListRoom();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        //Khi người dùng thoát khỏi game
        userExit();
        
    }//GEN-LAST:event_formWindowClosing

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        // TODO add your handling code here:
        userExit();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        this.setVisible(true);
    }//GEN-LAST:event_formWindowClosed

    private void createMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMenuItemActionPerformed
        // TODO add your handling code here:
        createRoom();
    }//GEN-LAST:event_createMenuItemActionPerformed

    private void findUserMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findUserMenuItemActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            competitorName = JOptionPane.showInputDialog(this,"Tên người chơi");
            if(competitorName == null || (competitorName != null && ("".equals(competitorName))))   
            {
                return;
            }
            serverListenRoom(Constant.FIND_USER);
        } catch (IOException ex) {
            Logger.getLogger(MainRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_findUserMenuItemActionPerformed
    private void userExit(){
        int dialogResult = JOptionPane.showConfirmDialog (this, "Bạn có chắc là muốn thoát chứ?","Warning",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){
            if(outToServer != null){
                try {
                    serverListenRoom(Constant.ISEXIT);
                } catch (IOException ex) {
                    Logger.getLogger(CaroServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
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
//                new MainRoom().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu actionMenu;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JMenuItem createMenuItem;
    private javax.swing.JButton createRoomButton;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton findRoomButton;
    private javax.swing.JMenuItem findUserMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton refreshButton;
    private javax.swing.JScrollPane roomPanel;
    private javax.swing.JTable roomTable;
    // End of variables declaration//GEN-END:variables
    private String roomTitle;
    private String userName;
    private ObjectOutputStream outToServer;
    private ObjectInputStream inFromServer;
    private DefaultTableModel tableModel;
    private int currentRoom;
    private CaroClient caroClient;
    private CaroServer caroServer;
    private String competitorName;

}
