package edu.hcmute.webpr.service;

import java.util.List;

import edu.hcmute.webpr.model.Author_24133059;

/**
 * TẦNG BUSINESS - nghiệp vụ liên quan tới tác giả.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public interface IAuthorService_24133059 {

    List<Author_24133059> findAll();

    List<Author_24133059> findAllHavingBooks();

    Author_24133059 findById(int authorId);

    List<Integer> findAuthorIdsOfBook(int bookId);
}
