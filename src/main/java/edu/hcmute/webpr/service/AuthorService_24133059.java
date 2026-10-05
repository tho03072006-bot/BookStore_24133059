package edu.hcmute.webpr.service;

import java.util.List;

import edu.hcmute.webpr.dao.AuthorDao_24133059;
import edu.hcmute.webpr.dao.IAuthorDao_24133059;
import edu.hcmute.webpr.model.Author_24133059;

/**
 * TẦNG BUSINESS - hiện thực nghiệp vụ tác giả. Tầng này gọi xuống tầng Data
 * Access, Controller chỉ được phép gọi tới đây chứ không gọi thẳng DAO.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class AuthorService_24133059 implements IAuthorService_24133059 {

    private final IAuthorDao_24133059 authorDao = new AuthorDao_24133059();

    @Override
    public List<Author_24133059> findAll() {
        return authorDao.findAll();
    }

    @Override
    public List<Author_24133059> findAllHavingBooks() {
        return authorDao.findAllHavingBooks();
    }

    @Override
    public Author_24133059 findById(int authorId) {
        return authorDao.findById(authorId);
    }

    @Override
    public List<Integer> findAuthorIdsOfBook(int bookId) {
        return authorDao.findAuthorIdsOfBook(bookId);
    }
}
