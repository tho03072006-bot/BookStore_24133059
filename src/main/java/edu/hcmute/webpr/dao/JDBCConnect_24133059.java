package edu.hcmute.webpr.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * TẦNG DATA ACCESS - lớp duy nhất mở kết nối JDBC tới SQL Server.
 * Mọi DAO khác đều gọi {@link #getConnection()} của lớp này.
 *
 * ***** SỬA THÔNG TIN KẾT NỐI Ở ĐÂY NẾU CHẠY TRÊN MÁY KHÁC *****
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class JDBCConnect_24133059 {

    private static final String SERVER_NAME = "localhost";
    private static final String PORT_NUMBER = "1433";
    private static final String DB_NAME = "BookStore";

    private static final String USER_ID = "sa";
    private static final String PASSWORD = "123456789";

    public Connection getConnection() throws SQLException {

        /*
         * Ép nạp class driver trước khi gọi DriverManager.getConnection().
         * Tomcat chạy nhiều webapp trong cùng một JVM, mỗi webapp có
         * ClassLoader riêng; cơ chế tự đăng ký driver theo chuẩn JDBC 4
         * (ServiceLoader) chỉ chạy một lần cho cả JVM và có thể dùng
         * ClassLoader của webapp khác -> sinh lỗi "No suitable driver found"
         * dù file mssql-jdbc.jar vẫn nằm trong WEB-INF/lib. Gọi Class.forName
         * buộc JVM nạp driver bằng đúng ClassLoader của webapp này.
         */
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Không tìm thấy driver JDBC SQL Server (mssql-jdbc) trong classpath!", e);
        }

        String url = "jdbc:sqlserver://" + SERVER_NAME + ":" + PORT_NUMBER
                + ";databaseName=" + DB_NAME
                + ";encrypt=true"
                + ";trustServerCertificate=true";

        return DriverManager.getConnection(url, USER_ID, PASSWORD);
    }

    /** Chạy riêng lớp này (Run As &gt; Java Application) để thử kết nối. */
    public static void main(String[] args) {
        try (Connection connection = new JDBCConnect_24133059().getConnection()) {
            System.out.println("Kết nối SQL Server thành công! Database: " + connection.getCatalog());
        } catch (SQLException e) {
            System.out.println("Kết nối SQL Server thất bại!");
            e.printStackTrace();
        }
    }
}
