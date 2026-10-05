package edu.hcmute.webpr.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.hcmute.webpr.model.Author_24133059;
import edu.hcmute.webpr.model.Book_24133059;

/**
 * TẦNG DATA ACCESS - hiện thực truy xuất bảng {@code books} bằng JDBC thuần.
 *
 * Phân trang dùng cú pháp chuẩn của SQL Server:
 * {@code ORDER BY ... OFFSET ? ROWS FETCH NEXT ? ROWS ONLY}.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class BookDao_24133059 implements IBookDao_24133059 {

    private final JDBCConnect_24133059 jdbc = new JDBCConnect_24133059();

    private static final String SELECT_COLUMNS =
            "b.bookid, b.isbn, b.title, b.publisher, b.price, b.description, "
            + "b.publish_date, b.cover_image, b.quantity, "
            + "(SELECT COUNT(*) FROM rating r WHERE r.bookid = b.bookid) AS review_count";

    // CÂU 3 - sách theo từng tác giả, có phân trang

    @Override
    public List<Book_24133059> findByAuthor(int authorId, int offset, int limit) {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM books b JOIN book_author ba ON ba.bookid = b.bookid"
                + " WHERE ba.author_id = ?"
                + " ORDER BY b.publish_date DESC, b.bookid DESC"
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            ps.setInt(2, Math.max(offset, 0));
            ps.setInt(3, Math.max(limit, 1));
            List<Book_24133059> books = readList(ps);
            attachAuthors(conn, books);
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc sách của tác giả id=" + authorId, e);
        }
    }

    @Override
    public int countByAuthor(int authorId) {
        String sql = "SELECT COUNT(*) FROM book_author WHERE author_id = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm sách của tác giả id=" + authorId, e);
        }
    }

    @Override
    public List<Book_24133059> findAll(int offset, int limit) {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM books b"
                + " ORDER BY b.bookid DESC"
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(offset, 0));
            ps.setInt(2, Math.max(limit, 1));
            List<Book_24133059> books = readList(ps);
            attachAuthors(conn, books);
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc danh sách sách", e);
        }
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm tổng số sách", e);
        }
    }

    // CÂU 4 - chi tiết 01 cuốn sách

    @Override
    public Book_24133059 findById(int bookId) {
        String sql = "SELECT " + SELECT_COLUMNS
                + ", (SELECT AVG(CAST(r2.rating AS FLOAT)) FROM rating r2 WHERE r2.bookid = b.bookid) AS avg_rating"
                + " FROM books b WHERE b.bookid = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Book_24133059 book = map(rs);
                double avg = rs.getDouble("avg_rating");
                book.setAverageRating(rs.wasNull() ? null : avg);
                attachAuthors(conn, List.of(book));
                return book;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc chi tiết sách id=" + bookId, e);
        }
    }

    // CÂU 6 - CRUD

    @Override
    public int insert(Book_24133059 book, List<Integer> authorIds) {
        String sql = "INSERT INTO books (isbn, title, publisher, price, description, "
                + "publish_date, cover_image, quantity) VALUES (?,?,?,?,?,?,?,?)";
        Connection conn = null;
        try {
            conn = jdbc.getConnection();
            // Ghi books và book_author trong CÙNG một transaction: nếu bước gắn
            // tác giả hỏng thì cuốn sách cũng không được tạo nửa vời.
            conn.setAutoCommit(false);
            int newId;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bindBook(ps, book);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Không lấy được bookid vừa sinh ra");
                    }
                    newId = keys.getInt(1);
                }
            }
            replaceAuthors(conn, newId, authorIds);
            conn.commit();
            return newId;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Lỗi khi thêm sách mới", e);
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean update(Book_24133059 book, List<Integer> authorIds) {
        String sql = "UPDATE books SET isbn=?, title=?, publisher=?, price=?, description=?, "
                + "publish_date=?, cover_image=?, quantity=? WHERE bookid=?";
        Connection conn = null;
        try {
            conn = jdbc.getConnection();
            conn.setAutoCommit(false);
            int rows;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                bindBook(ps, book);
                ps.setInt(9, book.getBookId());
                rows = ps.executeUpdate();
            }
            if (rows == 0) {
                conn.rollback();
                return false;
            }
            replaceAuthors(conn, book.getBookId(), authorIds);
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Lỗi khi cập nhật sách id=" + book.getBookId(), e);
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean delete(int bookId) {
        // Các bảng book_author và rating khai báo FOREIGN KEY ... ON DELETE
        // CASCADE nên xóa sách là tự xóa luôn liên kết tác giả và review.
        String sql = "DELETE FROM books WHERE bookid = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa sách id=" + bookId, e);
        }
    }

    private void bindBook(PreparedStatement ps, Book_24133059 b) throws SQLException {
        if (b.getIsbn() == null) {
            ps.setNull(1, Types.INTEGER);
        } else {
            ps.setInt(1, b.getIsbn());
        }
        ps.setString(2, b.getTitle());
        ps.setString(3, b.getPublisher());
        if (b.getPrice() == null) {
            ps.setNull(4, Types.DECIMAL);
        } else {
            ps.setBigDecimal(4, b.getPrice());
        }
        ps.setString(5, b.getDescription());
        if (b.getPublishDate() == null) {
            ps.setNull(6, Types.DATE);
        } else {
            ps.setDate(6, Date.valueOf(b.getPublishDate()));
        }
        ps.setString(7, b.getCoverImage());
        if (b.getQuantity() == null) {
            ps.setNull(8, Types.INTEGER);
        } else {
            ps.setInt(8, b.getQuantity());
        }
    }

    private void replaceAuthors(Connection conn, int bookId, List<Integer> authorIds)
            throws SQLException {
        try (PreparedStatement del = conn.prepareStatement(
                "DELETE FROM book_author WHERE bookid = ?")) {
            del.setInt(1, bookId);
            del.executeUpdate();
        }
        if (authorIds == null || authorIds.isEmpty()) {
            return;
        }
        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO book_author (bookid, author_id) VALUES (?,?)")) {
            for (Integer authorId : authorIds) {
                if (authorId == null) {
                    continue;
                }
                ins.setInt(1, bookId);
                ins.setInt(2, authorId);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    private List<Book_24133059> readList(PreparedStatement ps) throws SQLException {
        List<Book_24133059> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /**
     * Nạp danh sách tác giả cho nhiều cuốn sách bằng MỘT câu lệnh IN (...),
     * thay vì chạy một query cho mỗi cuốn (tránh lỗi N+1 query).
     */
    private void attachAuthors(Connection conn, List<Book_24133059> books) throws SQLException {
        if (books.isEmpty()) {
            return;
        }
        Map<Integer, Book_24133059> byId = new LinkedHashMap<>();
        StringBuilder placeholders = new StringBuilder();
        for (Book_24133059 b : books) {
            byId.put(b.getBookId(), b);
            placeholders.append(placeholders.length() == 0 ? "?" : ",?");
        }
        String sql = "SELECT ba.bookid, a.author_id, a.author_name, a.date_of_birth "
                + "FROM book_author ba JOIN author a ON a.author_id = ba.author_id "
                + "WHERE ba.bookid IN (" + placeholders + ") ORDER BY a.author_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            for (Integer id : byId.keySet()) {
                ps.setInt(i++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book_24133059 book = byId.get(rs.getInt("bookid"));
                    if (book != null) {
                        book.getAuthors().add(AuthorDao_24133059.map(rs));
                    }
                }
            }
        }
    }

    private Book_24133059 map(ResultSet rs) throws SQLException {
        Book_24133059 b = new Book_24133059();
        b.setBookId(rs.getInt("bookid"));
        int isbn = rs.getInt("isbn");
        b.setIsbn(rs.wasNull() ? null : isbn);
        b.setTitle(rs.getString("title"));
        b.setPublisher(rs.getString("publisher"));
        b.setPrice(rs.getBigDecimal("price"));
        b.setDescription(rs.getString("description"));
        Date pd = rs.getDate("publish_date");
        b.setPublishDate(pd == null ? null : pd.toLocalDate());
        b.setCoverImage(rs.getString("cover_image"));
        int qty = rs.getInt("quantity");
        b.setQuantity(rs.wasNull() ? null : qty);
        b.setReviewCount(rs.getInt("review_count"));
        return b;
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                // Không che lỗi gốc bằng lỗi rollback.
            }
        }
    }

    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
                // Giữ nguyên lỗi nghiệp vụ đã xảy ra trước khi đóng kết nối.
            }
        }
    }
}
