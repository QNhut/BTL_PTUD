package ConnectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    public static Connection con = null;
    private static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        return instance;
    }

    public void connect() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=QLBANHANG";
        String user = "sa";
        String password = "2569";
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                getInstance().connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public void disconnect() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}