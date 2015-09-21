/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Demo_Caro;

import javax.swing.JLabel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

/**
 *
 * @author Admin
 */
public class CaroServer extends javax.swing.JFrame {

    /**
     * Creates new form CaroFrame
     */
    public CaroServer() {
        initComponents();
        class ListenGame extends Thread {

            public ListenGame() {
                start();
            }

            @Override
            public void run() {
                clientListen();
            }
        }
        class ListenChat extends Thread {
            public ListenChat(){
                start();
            }
            @Override
            public void run(){
                clientChatListen();
            }
        }
        new ListenGame();
        new ListenChat();
        createBoard();
        
    }
//    Khởi tạo bàn cờ
    
        private void createBoard(){
            System.out.println("Creating Board....");
            oIcon = new ImageIcon(getClass().getResource("/Demo_Caro/assets/o.png"));
            xIcon = new ImageIcon(getClass().getResource("/Demo_Caro/assets/x.png"));
            startUser = closeUser;
            if(startUser){
                //Được phép đi khi là người bắt đầu
                user = true;
                isClientPause = true;
                isServerPause = false;
            }else{
                user = false;
                isClientPause = false;
                isServerPause = true;
            }
            cellSize = boardPanel.getWidth()/boardSize; // lấy kích thước của 1 ô
            int x = 0, y = 0; //Tọa độ bắt đầu từ 0
            for(int i = 0; i < boardSize; i++){
                for(int j = 0; j < boardSize; j++){
                    createCell(x,y);
                    x += cellSize;
                }
                x = 0;
                y += cellSize;
            }
        }
        // Khởi tạo ô trong bàn cờ
        private void createCell(int x,int y){
            JLabel label = new JLabel();
            label.setPreferredSize(new Dimension(cellSize,cellSize));
            label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            boardPanel.add(label,new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, -1, -1));
            
        }
        
        // Khởi tạo lại bàn cờ
        private void repaintBoard(){
            System.out.println("Repainting....");
            checked.removeAllElements();
            boardPanel.removeAll();
            boardPanel.revalidate();
            boardPanel.repaint();
            createBoard();
            //CaroServer c = new CaroServer();
            boardPanel.revalidate();
            boardPanel.repaint();
        }
        
        //Đánh cờ
        //Kiểm tra tới lược được đi chưa
        //Kiểm tra vị trí đã được đánh chưa
        //Kiểm tra thắng hay chưa
        //Xuất kết quả nếu thắng hoặc thua
        private void clientCheck(Point p) throws IOException{
            //Chỉ được đánh 1 lần và khi được phép
            if(isClientPause){
                return;
            }
            //Kiểm tra lược người dùng
            currentPoint = new Point((int)p.getX()/cellSize,(int)p.getY()/cellSize);
            //Kiểm tra điểm đánh đã được chọn chưa
            if(!checked.contains(currentPoint)){
                isClientPause = true;
                checked.add(currentPoint);
                JLabel lb = (JLabel)boardPanel.getComponentAt(p);
                lb.setIcon(oIcon);
                //Kiểm tra người chơi đã thắng chưa
               if(isWin(user)){
                  
                   closeUser = true;
                   Hashtable values = new Hashtable();
                   values.put(Constant.ISCLIENTWIN, true);
                   outToClient.writeObject(values);
                   JOptionPane.showMessageDialog(this, "Đối thủ đã thắng");
                   repaintBoard();
                   return;
               }
               //Chuyển lược đánh cho đối thủ
               isServerPause = false;
               user = true;
               System.out.println("Client Checked");
            }
        }
        
        private void serverCheck(Point p) throws IOException{
            //Chỉ được đánh 1 lần và khi được phép
            
            if(isServerPause){
                return;
            }
            currentPoint = new Point((int)p.getX()/cellSize,(int)p.getY()/cellSize);
            //Kiểm tra điểm đánh đã được chọn chưa
            if(!checked.contains(currentPoint)){
                
                JLabel lb = (JLabel)boardPanel.getComponentAt(p);
                isServerPause = true;
                checked.add(currentPoint);
                lb.setIcon(xIcon);
                //Gửi vị trí đánh cho Client
                Hashtable values = new Hashtable();
                values.put(Constant.SERVERCHECKED, true);
                values.put(Constant.SERVERPOINT, p);
                //Kiểm tra người chơi đã thắng chưa
               if(isWin(user)){
                   
                   closeUser = false;
                   values.put(Constant.ISSERVERWIN,true);
                   //Gửi giá trị tới Client;
                   outToClient.writeObject(values);
                   JOptionPane.showMessageDialog(this, "Bạn đã thắng");
                   repaintBoard();
                   return;
               }
               outToClient.writeObject(values);
               //Chuyển lược đánh cho đối thủ
               isClientPause = false;
               user = false;
               System.out.println("Server Checked");
            }
        }
        
        //Kiểm tra người dùng thắng chưa
        private boolean isWin(boolean user){
            int n = 6;
            /**
             * Kiểm tra số quân xung quanh quân mới đánh nếu = 4
             * và không bị chặn 2 đầu thì thắng
             */
            int ok = 0;
            /**
             * kiểm tra có bị chặn 2 đầu không
             */
            int soDauBiChan = 0;
            int u;// u=0 nếu là user 1; u=1 nếu là user 2
            if (startUser) {
                if (user) {
                    u = 0;
                } else {
                    u = 1;
                }
            } else {
                if (user) {
                    u = 1;
                } else {
                    u = 0;
                }
            }
        //Kiểm tra hàng ngang
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x + i, currentPoint.y);
            if (!(p.x < boardSize)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                    soDauBiChan++;
                }
                //Gặp quân của đối thủ hoặc gặp ô trống
                break;
            }
        }
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x - i, currentPoint.y);
            if (!(p.x >= 0)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                    soDauBiChan++;
                }
                //Gặp quân của đối thủ hoặc gặp ô trống
                break;
            }
        }
        if (ok == 4 && soDauBiChan != 2) {
            return true;
        }
        //Kiểm tra hàng dọc
        ok = 0;
        soDauBiChan = 0;
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x, currentPoint.y + i);
            if (!(p.y < boardSize)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                    soDauBiChan++;
                }
                //Gặp quân của đối thủ hoặc gặp ô trống
                break;
            }
        }
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x, currentPoint.y - i);
            if (!(p.y >= 0)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                    soDauBiChan++;
                }
                //Gặp quân của đối thủ hoặc gặp ô trống
                break;
            }
        }
        if (ok == 4 && soDauBiChan != 2) {
            return true;
        }
        //Kiểm tra đường chéo chính
        ok = 0;
        soDauBiChan = 0;
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x + i, currentPoint.y + i);
            if (!(p.x >= 0 && p.x < boardSize && p.y >= 0 && p.y < boardSize)) {//ô kiểm tra ra ngoài bàn cờ
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                    soDauBiChan++;
                }
                //Gặp quân của đối thủ hoặc gặp ô trống
                break;
            }
        }
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x - i, currentPoint.y - i);
            if (!(p.x >= 0 && p.x < boardSize && p.y >= 0 && p.y < boardSize)) {//ô kiểm tra ra ngoài bàn cờ
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                    soDauBiChan++;
                }
                //Gặp quân của đối thủ hoặc gặp ô trống
                break;
            }
        }
        if (ok == 4 && soDauBiChan != 2) {
            return true;
        }
        //Kiểm tra đường chéo phụ
        ok = 0;
        soDauBiChan = 0;
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x + i, currentPoint.y - i);
            if (!(p.x >= 0 && p.x < boardSize && p.y >= 0 && p.y < boardSize)) {//ô kiểm tra ra ngoài bàn cờ
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                    soDauBiChan++;
                }
                //Gặp quân của đối thủ hoặc gặp ô trống
                break;
            }
        }
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x - i, currentPoint.y + i);
            if (!(p.x >= 0 && p.x < boardSize && p.y >= 0 && p.y < boardSize)) {//ô kiểm tra ra ngoài bàn cờ
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                    soDauBiChan++;
                }
                //Gặp quân của đối thủ hoặc gặp ô trống
                break;
            }
        }
        if (ok == 4 && soDauBiChan != 2) {
            return true;
        }
        return false;
        }
        
        private void clientChat(String message){
            JLabel messageLabel = new JLabel();
            messageLabel.setText("Client : "+message);
            messageLabel.setMaximumSize(new Dimension(ChatPanel.getWidth(),20));
            messageLabel.setForeground(Color.DARK_GRAY);
            ChatPanel.add(messageLabel);
            ChatPanel.validate();
            ChatPanel.repaint();
        }
        
        private void serverChat(String message){
            JLabel messageLabel = new JLabel();
            System.out.println(message);
            messageLabel.setText("Server : "+message);
            messageLabel.setMaximumSize(new Dimension(ChatPanel.getWidth(),20));
            messageLabel.setForeground(Color.BLUE);
            ChatPanel.add(messageLabel);
            System.out.println("message added...");
            ChatPanel.validate();
            ChatPanel.repaint();
        }
        
        private void clientChatListen(){
            try{
                System.out.println("Theard Chat Running....");
                ServerSocket serverSocket = new ServerSocket(6789);
                
                //Chấp nhậ kết nối Chat
                clientChatSocket = serverSocket.accept();
                System.out.println("Kết nối chat thành công");
                
                //Tạo luồng dữ liệu nhập xuất với CLient
                outToChatClient = new ObjectOutputStream(clientChatSocket.getOutputStream());
                inFromChatClient = new ObjectInputStream(clientChatSocket.getInputStream());
                
                Hashtable values = new Hashtable();
                
                //Vòng lặp chính
                while(true){
                    values = (Hashtable)inFromChatClient.readObject();
                    clientChat((String)values.get(Constant.MESSAGE));
                }
            }catch(Exception e){
                
            }
        }
        
        private void clientListen(){
            try{
               //Tạo Socket
                System.out.println("Theard Running...");
               ServerSocket serverSocket = new ServerSocket(12345);
               
               //Chấp nhận kết nối
                clientSocket = serverSocket.accept();
                System.out.println("Socket accept");
                //Tạo luồng dữ liệu nhập xuất với Client
                outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                System.out.println("Streaming....");
               }catch(Exception e){
                    System.out.println("Could not listen on port 4444");
                    System.exit(-1);
            }
            Hashtable values = new Hashtable();
            try {
                inFromClient = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(CaroServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            while(true){
                try {
                    //Đọc dữ liệu được gửi lên từ Client
                    
                    values = (Hashtable)inFromClient.readObject();
                    if(values.containsKey(Constant.CLIENTCHECKED)){
                        System.out.println("Client Checking....");
                        clientCheck((Point)values.get(Constant.CLIENTPOINT));
                        
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CaroServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(CaroServer.class.getName()).log(Level.SEVERE, null, ex);
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

        boardPanel = new javax.swing.JPanel();
        clientPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        ScrollPaneChat = new javax.swing.JScrollPane();
        ChatPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        messageTextField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        replayButon = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        mainMenu = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Caro Network");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        boardPanel.setBackground(new java.awt.Color(255, 255, 255));
        boardPanel.setMinimumSize(new java.awt.Dimension(544, 544));
        boardPanel.setPreferredSize(new java.awt.Dimension(544, 544));
        boardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                boardPanelMouseClicked(evt);
            }
        });
        boardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        clientPanel.setBackground(new java.awt.Color(153, 255, 51));

        jPanel1.setBackground(new java.awt.Color(255, 204, 51));

        ScrollPaneChat.setBackground(new java.awt.Color(0, 255, 204));

        ChatPanel.setLayout(new javax.swing.BoxLayout(ChatPanel, javax.swing.BoxLayout.PAGE_AXIS));
        ChatPanel.add(jLabel1);

        ScrollPaneChat.setViewportView(ChatPanel);

        messageTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messageTextFieldActionPerformed(evt);
            }
        });
        messageTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageTextFieldKeyPressed(evt);
            }
        });

        sendButton.setText("Send");
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sendButtonMouseClicked(evt);
            }
        });

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        replayButon.setText("Replay");
        replayButon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                replayButonMouseClicked(evt);
            }
        });
        jPanel2.add(replayButon);

        stopButton.setText("Stop");
        jPanel2.add(stopButton);

        mainMenu.setMaximumSize(new java.awt.Dimension(78, 32769));
        mainMenu.setPreferredSize(new java.awt.Dimension(78, 21));

        fileMenu.setText("File");

        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        mainMenu.add(fileMenu);

        setJMenuBar(mainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ScrollPaneChat, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(clientPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(boardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clientPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScrollPaneChat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(messageTextField)
                    .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    private void messageTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_messageTextFieldActionPerformed

    private void boardPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_boardPanelMouseClicked
       
        Point p = new Point(evt.getX(),evt.getY());
        System.out.println("Clicked...");
        try {
            serverCheck(p);
            
        } catch (IOException ex) {
            System.out.println("Fails...");
            Logger.getLogger(CaroServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_boardPanelMouseClicked

    private void replayButonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_replayButonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_replayButonMouseClicked

    private void sendButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sendButtonMouseClicked
        try {
            // TODO add your handling code here:
            System.out.println("Send Clicked...");
            String message = messageTextField.getText();
            Hashtable values = new Hashtable();
            values.put(Constant.MESSAGE, message);
            outToChatClient.writeObject(values);
            serverChat(message);
        } catch (IOException ex) {
            Logger.getLogger(CaroServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_sendButtonMouseClicked

    private void messageTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageTextFieldKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == 10){
            try {
                // TODO add your handling code here:
                String message = messageTextField.getText();
                Hashtable values = new Hashtable();
                values.put(Constant.MESSAGE, message);
                outToChatClient.writeObject(values);
                serverChat(message);
                messageTextField.setText("");
                messageTextField.requestFocusInWindow();
            } catch (IOException ex) {
                Logger.getLogger(CaroServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_messageTextFieldKeyPressed

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
            java.util.logging.Logger.getLogger(CaroServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CaroServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CaroServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CaroServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CaroServer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ChatPanel;
    private javax.swing.JScrollPane ScrollPaneChat;
    private javax.swing.JPanel boardPanel;
    private javax.swing.JPanel clientPanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JTextField messageTextField;
    private javax.swing.JButton replayButon;
    private javax.swing.JButton sendButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
    private Point currentPoint;
    private int currentCol, currentRow; // Tọa độ điểm chọn
    private int cellSize;// Kích thước 1 ô
    private int boardSize = 32; // Số ô của bàn cờ
    private Vector<Point> checked = new Vector<Point>();
    private ImageIcon oIcon;
    private ImageIcon xIcon;
    private boolean user; // Người đang đánh > false : đối thủ; true: bạn
    private boolean startUser; // Người đánh trước > false: đối thủ, true: bạn
    private boolean isClientPause;
    private boolean isServerPause;
    private Socket clientSocket; // Socket Game
    private Socket clientChatSocket; //Socket Chat
    private boolean closeUser = true;
    private ObjectInputStream inFromClient;
    private ObjectOutputStream outToClient;
    private ObjectOutputStream outToChatClient;
    private ObjectInputStream inFromChatClient;
}