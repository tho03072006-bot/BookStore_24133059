package edu.hcmute.webpr.model;

import java.io.Serializable;

/**
 * CÂU 3: một khối trên trang home = 01 tác giả + trang sách (3 cuốn) của tác
 * giả đó + thông tin phân trang riêng của khối.
 *
 * Nhờ vậy mỗi tác giả có thanh "Trang trước – 1 2 3 4 – Trang sau" độc lập,
 * đúng như mẫu trong đề.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class AuthorBooks_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Author_24133059 author;
    private final PageResult_24133059<Book_24133059> page;

    public AuthorBooks_24133059(Author_24133059 author, PageResult_24133059<Book_24133059> page) {
        this.author = author;
        this.page = page;
    }

    public Author_24133059 getAuthor() {
        return author;
    }

    public PageResult_24133059<Book_24133059> getPage() {
        return page;
    }
}
