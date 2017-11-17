package com.example.roboj.a_charm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by roboj on 11/12/2017.
 */

public class ConnectSQL {
    // Student database connection fields
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://107.180.46.230:21/teamthree/login.php";//jdbc:mysql://107.180.46.230:21/cosc5384.us
    private static final String USER = "teamthree@cosc5384.us";
    private static final String PASS = "vhfFN-LsnW7t";

    public boolean makeConnection(String email, String password) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {  // Try to login
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, USER, PASS);

            statement = conn.prepareStatement("SELECT * FROM Student WHERE Email = ? and Password = ?");
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet rset = statement.executeQuery();

            if (rset.next()) {
                rset.close();
                statement.close();
                connection.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return false;
        }
    }
}
