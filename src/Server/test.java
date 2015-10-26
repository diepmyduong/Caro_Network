/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class test {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<User>();
        users = User.all();
        for(User user : users){
            System.out.println(user.getUsername());
            System.out.println(user.getPassword());
            System.out.println(user.getStatement());
        }
        User user = User.find("admin");
        System.out.println(user.getUsername());
        System.out.println(users.contains(user));
//        user2.update();
//        user2.save();
    }
}
