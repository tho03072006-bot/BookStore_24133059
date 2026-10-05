package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model ánh xạ bảng {@code author}.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class Author_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int authorId;
    private String authorName;
    private LocalDate dateOfBirth;

    public Author_24133059() {
    }

    public Author_24133059(int authorId, String authorName, LocalDate dateOfBirth) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.dateOfBirth = dateOfBirth;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /** date_of_birth dạng dd/MM/yyyy để hiển thị trên JSP. */
    public String getDateOfBirthText() {
        return dateOfBirth == null ? ""
                : dateOfBirth.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public String toString() {
        return authorName;
    }
}
