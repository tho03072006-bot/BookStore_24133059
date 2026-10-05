package edu.hcmute.webpr.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * TẦNG DATA ACCESS - lớp duy nhất mở kết nối JDBC tới SQL Server.
 * Mọi DAO khác đều gọi {@link #getConnection()} của lớp này.
 *
 * <p><b>ĐỔI THÔNG TIN KẾT NỐI KHI CHẠY TRÊN MÁY KHÁC:</b> sửa file
 * {@code src/main/resources/database.properties} là đủ, không phải sửa code
 * rồi build lại. Thiếu file đó thì lớp này dùng các giá trị mặc định ghi ngay
 * bên dưới.</p>
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class JDBCConnect_24133059 {

    private static final String FILE_CAU_HINH = "database.properties";

    // Giá trị mặc định, dùng khi không có file database.properties.
    private static final String SERVER_NAME = "localhost";
    private static final String PORT_NUMBER = "1433";
    private static final String DB_NAME = "BookStore";
    private static final String USER_ID = "sa";
    private static final String PASSWORD = "123456789";

    /** Đọc một lần khi class được nạp, các request sau dùng lại. */
    private static final Properties CAU_HINH = docCauHinh();

    private static Properties docCauHinh() {
        Properties props = new Properties();
        try (InputStream in = JDBCConnect_24133059.class.getClassLoader()
                .getResourceAsStream(FILE_CAU_HINH)) {
            if (in != null) {
                props.load(in);
            } else {
                System.out.println("[JDBCConnect] Khong co " + FILE_CAU_HINH
                        + " - dung thong so mac dinh: " + SERVER_NAME + ":" + PORT_NUMBER
                        + "/" + DB_NAME);
            }
        } catch (IOException e) {
            System.err.println("[JDBCConnect] Doc " + FILE_CAU_HINH + " that bai: " + e.getMessage());
        }
        return props;
    }

    private static String lay(String khoa, String macDinh) {
        String giaTri = CAU_HINH.getProperty(khoa);
        return (giaTri == null || giaTri.isBlank()) ? macDinh : giaTri.trim();
    }

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

        String url = "jdbc:sqlserver://" + lay("db.server", SERVER_NAME)
                + ":" + lay("db.port", PORT_NUMBER)
                + ";databaseName=" + lay("db.name", DB_NAME)
                + ";encrypt=true"
                + ";trustServerCertificate=true";

        return DriverManager.getConnection(url, lay("db.user", USER_ID), lay("db.password", PASSWORD));
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
