/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Demo_Caro;

import java.net.*;
import java.io.*;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();
    }
    
    private void serverLoginListen(String username, String password,String action) throws IOException{
        try {
            //tạo socket
            String serverIP = serverIPTxt.getText();
            System.out.println("Kết nối server...");
            Socket loginSocket = new Socket(serverIP,8888);
            //Nếu người dùng đăng nhập
            if(action.equals(Constant.ISLOGIN)){
                //tạo luồng dữ liệu nhâp xuất
                System.out.println("Gửi thông tin đăng nhập");
                outToSever = new ObjectOutputStream(loginSocket.getOutputStream());
                Hashtable messages = new Hashtable();
                messages.put(Constant.ISLOGIN, true);
                messages.put(Constant.USERNAME,username);
                messages.put(Constant.PASSWORD,password);
                outToSever.writeObject(messages);
                System.out.println("Đã gửi");
                inFromServer = new ObjectInputStream(loginSocket.getInputStream());
                Hashtable values = (Hashtable)inFromServer.readObject();

                if(values.containsKey(Constant.SERVERREPLY)){
                    //Nếu thành công
                    System.out.println("Có phản hồi");
                    String message =(String) values.get(Constant.SERVERREPLY);
                    if(message.equals(Constant.LOGINSUCCESS)){
                        System.out.println("thành công");
                        this.setVisible(false);
                        MainRoom main = new MainRoom(username);
                        main.setVisible(true);
                    }
                    //Nếu sai tài khoản
                    if(message.equals(Constant.USERNAME_FAIL)){
                        System.out.println("không thành công");
                        JOptionPane.showMessageDialog(this,"Tài khoản không tồn tại....");
                        return;
                    }
                    //Nếu sai mật khẩu
                    if(message.equals(Constant.PASSWORD_FAIL)){
                        System.out.println("không thành công");
                        JOptionPane.showMessageDialog(this,"Mật khẩu không đúng....");
                        return;
                    }
                    //Nếu tài khoản đã được đăng nhập
                    if(message.equals(Constant.IS_LOGINED)){
                        System.out.println("không thành công");
                        JOptionPane.showMessageDialog(this,"Tài khoản này đã được đăng nhập");
                        return;
                    }
                }
            }
            //Nếu người dùng đăng ký
            if(action.equals(Constant.ISSIGNUP)){
                //tạo luồng dữ liệu nhâp xuất
                System.out.println("Gửi thông tin đăng ký");
                outToSever = new ObjectOutputStream(loginSocket.getOutputStream());
                Hashtable messages = new Hashtable();
                messages.put(Constant.ISSIGNUP, true);
                messages.put(Constant.USERNAME,username);
                messages.put(Constant.PASSWORD,password);
                outToSever.writeObject(messages);
                System.out.println("Đã gửi");
                inFromServer = new ObjectInputStream(loginSocket.getInputStream());
                Hashtable values = (Hashtable)inFromServer.readObject();

                if(values.containsKey(Constant.SERVERREPLY)){
                    //Nếu thành công
                    System.out.println("Có phản hồi");
                    String message =(String) values.get(Constant.SERVERREPLY);
                    if(message.equals(Constant.SIGNUP_SUCCESS)){
                        System.out.println("thành công");
                        this.setVisible(false);
                        MainRoom main = new MainRoom(username);
                        main.setVisible(true);
                    }
                    //Nếu sai tài khoản
                    if(message.equals(Constant.SIGNUP_FAIL)){
                        System.out.println("không thành công");
                        JOptionPane.showMessageDialog(this,"Đăng ký không thành công");
                        return;
                    }
                    //Nếu sai mật khẩu
                    if(message.equals(Constant.IS_SIGNUPED)){
                        System.out.println("không thành công");
                        JOptionPane.showMessageDialog(this,"Tài khoản đã tồn tại");
                        return;
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
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
        java.awt.GridBagConstraints gridBagConstraints;

        userInfoPanel = new javax.swing.JPanel();
        userNameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        userNameTxt = new javax.swing.JTextField();
        passwordTxt = new javax.swing.JPasswordField();
        serverLabel = new javax.swing.JLabel();
        serverIPTxt = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        loginButton = new javax.swing.JButton();
        signupButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        setPreferredSize(new java.awt.Dimension(400, 320));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        userInfoPanel.setPreferredSize(new java.awt.Dimension(300, 100));

        userNameLabel.setText("Username :");

        passwordLabel.setText("Password :");

        serverLabel.setText("Server :");

        serverIPTxt.setText("Adminpc");

        javax.swing.GroupLayout userInfoPanelLayout = new javax.swing.GroupLayout(userInfoPanel);
        userInfoPanel.setLayout(userInfoPanelLayout);
        userInfoPanelLayout.setHorizontalGroup(
            userInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userInfoPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(userInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(userNameLabel)
                    .addComponent(passwordLabel)
                    .addComponent(serverLabel))
                .addGap(18, 18, 18)
                .addGroup(userInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userNameTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addComponent(passwordTxt)
                    .addComponent(serverIPTxt))
                .addContainerGap())
        );
        userInfoPanelLayout.setVerticalGroup(
            userInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userInfoPanelLayout.createSequentialGroup()
                .addGroup(userInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userNameLabel)
                    .addComponent(userNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(userInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(userInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverLabel)
                    .addComponent(serverIPTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 13, Short.MAX_VALUE))
        );

        getContentPane().add(userInfoPanel, new java.awt.GridBagConstraints());

        jPanel1.setPreferredSize(new java.awt.Dimension(300, 50));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        loginButton.setText("Đặng nhập");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        jPanel1.add(loginButton);

        signupButton.setText("Đăng ký");
        signupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupButtonActionPerformed(evt);
            }
        });
        jPanel1.add(signupButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        try {
            // TODO add your handling code here:
            if(userNameTxt.getText().equals("")){
                JOptionPane.showMessageDialog(this,"Chưa nhập username");
                return;
            }
            if(passwordTxt.getText().equals("")){
                JOptionPane.showMessageDialog(this,"Chưa nhập password");
                return;
            }
            serverLoginListen(userNameTxt.getText(), passwordTxt.getText(),Constant.ISLOGIN);
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    private void signupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupButtonActionPerformed
        // TODO add your handling code here:
        try {
            if(userNameTxt.getText().equals("")){
                JOptionPane.showMessageDialog(this,"Chưa nhập username");
                return;
            }
            if(passwordTxt.getText().equals("")){
                JOptionPane.showMessageDialog(this,"Chưa nhập password");
                return;
            }
            // TODO add your handling code here:
            serverLoginListen(userNameTxt.getText(), passwordTxt.getText(),Constant.ISSIGNUP);
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_signupButtonActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordTxt;
    private javax.swing.JTextField serverIPTxt;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JButton signupButton;
    private javax.swing.JPanel userInfoPanel;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField userNameTxt;
    // End of variables declaration//GEN-END:variables
    private ObjectOutputStream outToSever;
    private ObjectInputStream inFromServer;

}
